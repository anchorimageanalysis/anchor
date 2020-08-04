/*-
 * #%L
 * anchor-image-bean
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

package org.anchoranalysis.image.bean.nonbean.arrangeraster;

import java.util.Iterator;
import java.util.List;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.channel.factory.ChannelFactorySingleType;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.stack.rgb.RGBStack;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;

public class RasterArranger {

    private BoundingBoxesOnPlane boundingBoxes;
    private ImageDimensions dimensions;

    public void init(ArrangeRaster arrangeRaster, List<RGBStack> list) throws InitException {

        Iterator<RGBStack> rasterIterator = list.iterator();
        try {
            this.boundingBoxes = arrangeRaster.createBoundingBoxesOnPlane(rasterIterator);
        } catch (ArrangeRasterException e) {
            throw new InitException(e);
        }

        if (rasterIterator.hasNext()) {
            throw new InitException("rasterIterator has more items than can be accomodated");
        }

        dimensions = new ImageDimensions(boundingBoxes.extent());
    }

    public RGBStack createStack(List<RGBStack> list, ChannelFactorySingleType factory) {
        RGBStack stackOut = new RGBStack(dimensions, factory);
        ppltStack(list, stackOut.asStack());
        return stackOut;
    }

    private void ppltStack(List<RGBStack> generatedImages, Stack out) {

        int index = 0;
        for (RGBStack image : generatedImages) {

            BoundingBox bbox = this.boundingBoxes.get(index++);

            // NOTE
            // For a special case where our projection z-extent is different to our actual z-extent,
            // that means
            //   we should repeat
            if (bbox.extent().z() != image.dimensions().z()) {

                int zShift = 0;
                do {
                    projectImageOntoStackOut(bbox, image.asStack(), out, zShift);
                    zShift += image.dimensions().z();
                } while (zShift < out.dimensions().z());

            } else {
                projectImageOntoStackOut(bbox, image.asStack(), out, 0);
            }
        }
    }

    private void projectImageOntoStackOut(
            BoundingBox bbox, Stack stackIn, Stack stackOut, int zShift) {

        assert (stackIn.getNumberChannels() == stackOut.getNumberChannels());

        Extent extent = stackIn.dimensions().extent();
        Extent extentOut = stackIn.dimensions().extent();

        ReadableTuple3i leftCrnr = bbox.cornerMin();
        int xEnd = leftCrnr.x() + bbox.extent().x() - 1;
        int yEnd = leftCrnr.y() + bbox.extent().y() - 1;

        int numC = stackIn.getNumberChannels();
        VoxelBuffer<?>[] voxelsIn = new VoxelBuffer<?>[numC];
        VoxelBuffer<?>[] voxelsOut = new VoxelBuffer<?>[numC];

        for (int z = 0; z < extent.z(); z++) {

            int outZ = leftCrnr.z() + z + zShift;

            if (outZ >= extentOut.z()) {
                return;
            }

            for (int c = 0; c < numC; c++) {
                voxelsIn[c] = stackIn.getChannel(c).voxels().any().slice(z);
                voxelsOut[c] = stackOut.getChannel(c).voxels().any().slice(outZ);
            }

            int src = 0;
            for (int y = leftCrnr.y(); y <= yEnd; y++) {
                for (int x = leftCrnr.x(); x <= xEnd; x++) {

                    int outPos = stackOut.dimensions().offset(x, y);

                    for (int c = 0; c < numC; c++) {
                        voxelsOut[c].transferFromConvert(outPos, voxelsIn[c], src);
                    }
                    src++;
                }
            }
        }
    }
}
