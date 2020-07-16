/*-
 * #%L
 * anchor-mpp
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

package org.anchoranalysis.anchor.mpp.mark.conic;

import static org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMembershipUtilities.*;
import static org.anchoranalysis.anchor.mpp.mark.GlobalRegionIdentifiers.*;
import static org.anchoranalysis.anchor.mpp.mark.conic.TensorUtilities.*;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.jet.math.Functions;
import java.io.Serializable;
import java.util.Optional;
import lombok.Getter;
import org.anchoranalysis.anchor.mpp.mark.GlobalRegionIdentifiers;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.mark.MarkConic;
import org.anchoranalysis.anchor.mpp.mark.QuickOverlapCalculation;
import org.anchoranalysis.anchor.overlay.OverlayProperties;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.extent.ImageResolution;
import org.anchoranalysis.image.orientation.Orientation;
import org.anchoranalysis.image.orientation.Orientation3DEulerAngles;

//
//  3 sub-marks
//
//  Sub-Mark 0:  Center Ellipsoid
//  Sub-Mark 1:  Ellipsoid with shell
//
public class MarkEllipsoid extends MarkConic implements Serializable {

    /** */
    private static final long serialVersionUID = -2678275834893266874L;

    private static final int NUM_DIM = 3;

    private static final byte FLAG_SUBMARK_NONE = flagForNoRegion();
    private static final byte FLAG_SUBMARK_REGION0 =
            flagForRegion(SUBMARK_INSIDE, SUBMARK_CORE, SUBMARK_CORE_INNER);
    private static final byte FLAG_SUBMARK_REGION1 = flagForRegion(SUBMARK_INSIDE, SUBMARK_CORE);
    private static final byte FLAG_SUBMARK_REGION2 = flagForRegion(SUBMARK_INSIDE, SUBMARK_SHELL);
    private static final byte FLAG_SUBMARK_REGION3 =
            flagForRegion(SUBMARK_SHELL, SUBMARK_SHELL_OUTSIDE);
    private static final byte FLAG_SUBMARK_REGION4 = flagForRegion(SUBMARK_OUTSIDE);

    // START Mark State
    private double shellRad = 0.1;
    private double innerCoreDistance = 0.4;

    @Getter private Point3d radii;
    private Orientation orientation = new Orientation3DEulerAngles();
    // END mark state

    // START internal objects
    private EllipsoidMatrixCalculator ellipsoidCalculator;

    // Relative distances to various shells squared (expressed as a ratio of the radii)
    private double shellInnerCore;
    private double shellInt;
    private double shellExt;
    private double shellExtOut;

    // Relative distances to various shells squared (expressed as a ratio of the radii squared)
    private double shellInnerCoreSq;
    private double shellIntSq;
    private double shellExtSq;
    private double shellExtOutSq;

    private double radiiShellMaxSq;
    // END internal objects

    // Default Constructor
    public MarkEllipsoid() {
        super();
        this.radii = new Point3d();
        ellipsoidCalculator = new EllipsoidMatrixCalculator(NUM_DIM);
    }

    // Copy Constructor
    public MarkEllipsoid(MarkEllipsoid src) {
        super(src);
        this.radii = new Point3d(src.radii);

        this.shellRad = src.shellRad;
        this.innerCoreDistance = src.innerCoreDistance;

        ellipsoidCalculator = new EllipsoidMatrixCalculator(src.ellipsoidCalculator);

        this.orientation = src.orientation.duplicate();
        this.radiiShellMaxSq = src.radiiShellMaxSq;

        this.shellExt = src.shellExt;
        this.shellExtOut = src.shellExtOut;
        this.shellInt = src.shellInt;
        this.shellInnerCore = src.shellInnerCore;

        this.shellExtSq = src.shellExtSq;
        this.shellExtOutSq = src.shellExtOutSq;
        this.shellIntSq = src.shellIntSq;
        this.shellInnerCoreSq = src.shellInnerCoreSq;
    }

    @Override
    public String getName() {
        return "ellipsoid";
    }

    public static double getEllipsoidSum(double x, double y, double z, DoubleMatrix2D mat) {
        return x * (x * mat.get(0, 0) + y * mat.get(1, 0) + z * mat.get(2, 0))
                + y * (x * mat.get(0, 1) + y * mat.get(1, 1) + z * mat.get(2, 1))
                + z * (x * mat.get(0, 2) + y * mat.get(1, 2) + z * mat.get(2, 2));
    }

    private static double l2norm(double x, double y, double z) {
        return Math.pow(x, 2.0) + Math.pow(y, 2.0) + Math.pow(z, 2.0);
    }

    // Where is a point in relation to the current object
    @Override
    public final byte evalPointInside(Point3d pt) {

        // It is permissible to mutate the point during calculation
        double x = pt.getX() - getPos().getX();
        double y = pt.getY() - getPos().getY();
        double z = pt.getZ() - getPos().getZ();

        if (l2norm(x, y, z) > radiiShellMaxSq) {
            return FLAG_SUBMARK_NONE;
        }

        // We exit early if it's inside the internal shell
        double sum = getEllipsoidSum(x, y, z, ellipsoidCalculator.getEllipsoidMatrix());
        if (sum <= shellInnerCoreSq) {
            return FLAG_SUBMARK_REGION0;
        }

        // We exit early if it's inside the internal shell
        sum = getEllipsoidSum(x, y, z, ellipsoidCalculator.getEllipsoidMatrix());
        if (sum <= shellIntSq) {
            return FLAG_SUBMARK_REGION1;
        }

        if (sum <= 1) {
            return FLAG_SUBMARK_REGION2;
        }

        if (sum <= shellExtSq) {
            return FLAG_SUBMARK_REGION3;
        }

        if (sum <= shellExtOutSq) {
            return FLAG_SUBMARK_REGION4;
        }

        return FLAG_SUBMARK_NONE;
    }

    @Override
    public Mark duplicate() {
        return new MarkEllipsoid(this);
    }

    @Override
    public String toString() {
        return String.format(
                "%s %s pos=%s %s vol=%e", "Ellpsd", strId(), strPos(), strMarks(), volume(0));
    }

    @Override
    public double volume(int regionID) {

        if (regionID == GlobalRegionIdentifiers.SUBMARK_INSIDE) {
            return volumeForShell(1);
        } else if (regionID == GlobalRegionIdentifiers.SUBMARK_SHELL_OUTSIDE) {
            return volumeForShell(shellExt) - volumeForShell(1);
        } else if (regionID == GlobalRegionIdentifiers.SUBMARK_SHELL) {
            return volumeForShell(shellExt) - volumeForShell(shellInt);
        } else if (regionID == GlobalRegionIdentifiers.SUBMARK_CORE) {
            return volumeForShell(shellInt);
        } else if (regionID == GlobalRegionIdentifiers.SUBMARK_OUTSIDE) {
            return volumeForShell(shellExtOut) - volumeForShell(shellExt);
        } else if (regionID == GlobalRegionIdentifiers.SUBMARK_CORE_INNER) {
            return volumeForShell(shellInnerCore);
        } else {
            assert false;
            return 0.0;
        }
    }

    private double volumeForShell(double multiplier) {
        return (4
                        * Math.PI
                        * this.radii.getX()
                        * this.radii.getY()
                        * this.radii.getZ()
                        * Math.pow(multiplier, 3))
                / 3;
    }

    public void updateAfterMarkChange() {

        DoubleMatrix2D matRot = orientation.createRotationMatrix().getMatrix();

        double[] radiusArray =
                threeElementArray(this.radii.getX(), this.radii.getY(), this.radii.getZ());
        assert matRot.rows() == 3;
        this.ellipsoidCalculator.update(radiusArray, matRot);

        this.shellInt = 1.0 - this.shellRad;
        this.shellExt = 1.0 + this.shellRad;
        this.shellExtOut = 1.0 + (this.shellRad * 2);
        this.shellInnerCore = 1.0 - innerCoreDistance;

        this.shellIntSq = squared(shellInt);
        this.shellExtSq = squared(shellExt);
        this.shellExtOutSq = squared(shellExtOut);
        this.shellInnerCoreSq = squared(shellInnerCore);

        this.radiiShellMaxSq = squared(ellipsoidCalculator.getMaximumRadius() * shellExtOut);

        assert shellInt > 0;
    }

    @Override
    public BoundingBox bbox(ImageDimensions bndScene, int regionID) {

        DoubleMatrix1D s = ellipsoidCalculator.getBoundingBoxMatrix().copy();

        assert shellInt > 0;
        assert shellInnerCore > 0;

        if (regionID == GlobalRegionIdentifiers.SUBMARK_SHELL
                || regionID == GlobalRegionIdentifiers.SUBMARK_SHELL_OUTSIDE) {
            s.assign(Functions.mult(shellExt));
        } else if (regionID == GlobalRegionIdentifiers.SUBMARK_CORE) {
            s.assign(Functions.mult(shellInt));
        } else if (regionID == GlobalRegionIdentifiers.SUBMARK_OUTSIDE) {
            s.assign(Functions.mult(shellExtOut));
        } else if (regionID == GlobalRegionIdentifiers.SUBMARK_CORE_INNER) {
            s.assign(Functions.mult(shellInnerCore));
        }

        return BoundingBoxCalculator.bboxFromBounds(getPos(), s, true, bndScene);
    }

    private String strMarks() {
        return String.format(
                "rad=[%3.3f, %3.3f, %3.3f] rot=[%s] shellRad=[%f]",
                this.radii.getX(),
                this.radii.getY(),
                this.radii.getZ(),
                this.orientation.toString(),
                shellRad);
    }

    @SuppressWarnings("static-access")
    private transient QuickOverlapCalculation quickOverlap =
            (Mark mark, int regionID) -> {
                // No quick tests unless it's the same type of class
                if (!(mark instanceof MarkEllipsoid)) {
                    return false;
                }

                MarkEllipsoid trgtMark = (MarkEllipsoid) mark;

                DoubleMatrix1D relPos =
                        TensorUtilities.threeElementMatrix(
                                trgtMark.getPos().getX() - getPos().getX(),
                                trgtMark.getPos().getY() - getPos().getY(),
                                trgtMark.getPos().getZ() - getPos().getZ());

                DoubleMatrix1D relPosSquared = relPos.copy();
                relPosSquared.assign(Functions.functions.square); // NOSONAR
                double distance = relPosSquared.zSum();

                // Definitely outside
                return distance
                        > Math.pow(
                                getMaximumRadius(regionID) + trgtMark.getMaximumRadius(regionID),
                                2.0);
            };

    @Override
    public Optional<QuickOverlapCalculation> quickOverlap() {
        return Optional.of(quickOverlap);
    }

    private double getMaximumRadius(int regionID) {

        double maxRadius = ellipsoidCalculator.getMaximumRadius();

        if (regionID == GlobalRegionIdentifiers.SUBMARK_SHELL) {
            maxRadius *= (1 + shellRad);
        }

        return maxRadius;
    }

    public double getShellRad() {
        return shellRad;
    }

    public void setShellRad(double shellRad) {
        this.shellRad = shellRad;
    }

    @Override
    public void setMarksExplicit(Point3d pos, Orientation orientation, Point3d radii) {
        super.setPos(pos);
        this.orientation = orientation;
        this.radii = radii;
        updateAfterMarkChange();
        assert shellInt > 0;
    }

    @Override
    public void setMarksExplicit(Point3d pos) {
        setMarksExplicit(pos, orientation, radii);
    }

    @Override
    public double[] createRadiiArray() {
        return threeElementArray(this.radii.getX(), this.radii.getY(), this.radii.getZ());
    }

    @Override
    public double[] createRadiiArrayResolved(ImageResolution sr) {
        return EllipsoidUtilities.normalisedRadii(this, sr);
    }

    // NB objects are scaled in pre-rotated position i.e. when aligned to axes
    @Override
    public void scale(double multFactor) {
        super.scale(multFactor);

        this.radii.setX(this.radii.getX() * multFactor);
        this.radii.setY(this.radii.getY() * multFactor);
        this.radii.setZ(this.radii.getZ() * multFactor);
        updateAfterMarkChange();
    }

    @Override
    public boolean equalsDeep(Mark m) {

        if (!super.equalsDeep(m)) {
            return false;
        }

        if (!(m instanceof MarkEllipsoid)) {
            return false;
        }

        MarkEllipsoid trgt = (MarkEllipsoid) m;

        if (!radii.equals(trgt.radii)) {
            return false;
        }

        return orientation.equals(trgt.orientation);
    }

    @Override
    public int numDims() {
        return 3;
    }

    @Override
    public void setMarksExplicit(Point3d pos, Orientation orientation) {
        setMarksExplicit(pos, orientation, radii);
    }

    @Override
    public OverlayProperties generateProperties(ImageResolution sr) {
        OverlayProperties op = super.generateProperties(sr);

        op.addDoubleAsString("Radius X (pixels)", radii.getX());
        op.addDoubleAsString("Radius Y (pixels)", radii.getY());
        op.addDoubleAsString("Radius Z (pixels)", radii.getZ());

        if (sr != null) {
            double[] arr = EllipsoidUtilities.normalisedRadii(this, sr);
            op.addDoubleAsString("Normalized Radius 0 (pixels)", arr[0]);
            op.addDoubleAsString("Normalized Radius 1 (pixels)", arr[1]);
            op.addDoubleAsString("Normalized Radius 2 (pixels)", arr[2]);
        }
        orientation.addProperties(op.getNameValueSet());
        op.addDoubleAsString("Shell Radius Ratio", shellRad);
        op.addDoubleAsString("Inner Core Radius Ratio ", innerCoreDistance);
        return op;
    }

    @Override
    public int numRegions() {
        return 5;
    }

    @Override
    public BoundingBox bboxAllRegions(ImageDimensions bndScene) {
        return bbox(bndScene, GlobalRegionIdentifiers.SUBMARK_OUTSIDE);
    }

    public EllipsoidMatrixCalculator getEllipsoidCalculator() {
        return ellipsoidCalculator;
    }

    public Orientation getOrientation() {
        return orientation;
    }
}
