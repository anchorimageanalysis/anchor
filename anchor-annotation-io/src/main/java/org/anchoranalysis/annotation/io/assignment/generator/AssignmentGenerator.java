/* (C)2020 */
package org.anchoranalysis.annotation.io.assignment.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.anchor.overlay.bean.DrawObject;
import org.anchoranalysis.annotation.io.assignment.Assignment;
import org.anchoranalysis.core.color.ColorList;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.bean.provider.stack.StackProviderArrangeRaster;
import org.anchoranalysis.image.io.bean.stack.arrange.StackProviderWithLabel;
import org.anchoranalysis.image.io.generator.raster.RasterGenerator;
import org.anchoranalysis.image.io.generator.raster.StackGenerator;
import org.anchoranalysis.image.io.generator.raster.obj.rgb.DrawObjectsGenerator;
import org.anchoranalysis.image.io.stack.TileRasters;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.object.ObjectCollectionFactory;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.object.properties.ObjectCollectionWithProperties;
import org.anchoranalysis.image.object.properties.ObjectWithProperties;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.stack.rgb.RGBStack;
import org.anchoranalysis.io.bean.object.writer.Filled;
import org.anchoranalysis.io.bean.object.writer.IfElse;
import org.anchoranalysis.io.bean.object.writer.Outline;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

public class AssignmentGenerator extends RasterGenerator {

    private DisplayStack background;
    private Assignment assignment;
    private StackGenerator delegate;
    private boolean mipOutline;

    private ColorPool colorPool;

    @Getter @Setter private int outlineWidth = 1;

    @Getter @Setter private String leftName = "annotation";

    @Getter @Setter private String rightName = "result";

    /**
     * @param background
     * @param assignment
     * @param colorSetGeneratorPaired
     * @param colorSetGeneratorUnpaired
     * @param mipOutline
     * @param factory
     * @param replaceMatchesWithSolids if TRUE, then any matching objects are displayed as solids,
     *     rather than outlines. if FALSE, all objects are displayed as outlines.
     */
    AssignmentGenerator(
            DisplayStack background,
            Assignment assignment,
            ColorPool colorPool,
            boolean mipOutline) {
        super();
        this.background = background;
        this.assignment = assignment;
        this.mipOutline = mipOutline;
        this.colorPool = colorPool;

        delegate = new StackGenerator(true, "assignmentComparison");
    }

    @Override
    public boolean isRGB() {
        return true;
    }

    @Override
    public Stack generate() throws OutputWriteFailedException {

        StackProviderArrangeRaster stackProvider =
                createTiledStackProvider(
                        createRGBOutlineStack(true),
                        createRGBOutlineStack(false),
                        leftName,
                        rightName);

        try {
            Stack combined = stackProvider.create();
            delegate.setIterableElement(combined);
            return delegate.generate();

        } catch (CreateException e) {
            throw new OutputWriteFailedException(e);
        }
    }

    private static StackProviderArrangeRaster createTiledStackProvider(
            Stack stackLeft, Stack stackRight, String nameLeft, String nameRight) {
        List<StackProviderWithLabel> listProvider = new ArrayList<>();
        listProvider.add(new StackProviderWithLabel(stackLeft, nameLeft));
        listProvider.add(new StackProviderWithLabel(stackRight, nameRight));

        return TileRasters.createStackProvider(listProvider, 2, false, false, true);
    }

    private Stack createRGBOutlineStack(boolean left) throws OutputWriteFailedException {
        try {
            return createRGBOutlineStack(
                    assignment.getListPaired(left), colorPool, assignment.getListUnassigned(left));
        } catch (OperationFailedException e) {
            throw new OutputWriteFailedException(e);
        }
    }

    private Stack createRGBOutlineStack(
            List<ObjectMask> matchedObjects,
            ColorPool colorPool,
            final List<ObjectMask> otherObjects)
            throws OutputWriteFailedException, OperationFailedException {
        return createGenerator(
                        otherObjects,
                        colorPool.createColors(otherObjects.size()),
                        ObjectCollectionFactory.from(matchedObjects, otherObjects))
                .generate();
    }

    private DrawObjectsGenerator createGenerator(
            List<ObjectMask> otherObjects, ColorList cols, ObjectCollection objects) {

        DrawObject outlineWriter = createOutlineWriter();

        if (colorPool.isDifferentColorsForMatches()) {
            DrawObject conditionalWriter = createConditionalWriter(otherObjects, outlineWriter);
            return createGenerator(conditionalWriter, cols, objects);
        } else {
            return createGenerator(outlineWriter, cols, objects);
        }
    }

    private DrawObjectsGenerator createGenerator(
            DrawObject drawObject, ColorList cols, ObjectCollection objects) {
        return new DrawObjectsGenerator(
                drawObject,
                new ObjectCollectionWithProperties(objects),
                Optional.of(background),
                cols);
    }

    private DrawObject createConditionalWriter(List<ObjectMask> otherObjects, DrawObject writer) {
        return new IfElse(
                (ObjectWithProperties mask, RGBStack stack, int id) ->
                        otherObjects.contains(mask.getMask()),
                writer,
                new Filled());
    }

    private DrawObject createOutlineWriter() {
        return new Outline(outlineWidth, mipOutline);
    }

    @Override
    public Optional<ManifestDescription> createManifestDescription() {
        return Optional.of(new ManifestDescription("raster", "assignment"));
    }
}
