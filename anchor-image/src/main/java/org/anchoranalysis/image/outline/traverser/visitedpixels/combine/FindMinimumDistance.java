/* (C)2020 */
package org.anchoranalysis.image.outline.traverser.visitedpixels.combine;

import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.outline.traverser.contiguouspath.ContiguousPixelPath;
import org.anchoranalysis.image.outline.traverser.contiguouspath.DistanceIndex;
import org.anchoranalysis.image.outline.traverser.contiguouspath.DistanceToContiguousPath;

/**
 * TODO naming is too complicated in functions with lots of min/max
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FindMinimumDistance {

    /** The maximum-distance of a point to the closest contiguous-path */
    public static int minDistanceMaxToHeadTail(Point3i point, List<ContiguousPixelPath> paths) {

        // Finds the minimum distance to any of the paths
        return paths.stream()
                .mapToInt(path -> DistanceToContiguousPath.maxDistanceToHeadTail(path, point))
                .min()
                .orElse(Integer.MAX_VALUE);
    }

    /** The index of whichever of the ContiguousPixelPaths has the nearest point */
    public static DistanceIndexTwice indexDistanceMaxToClosestPoint(
            Point3i point, List<ContiguousPixelPath> paths, int avoidIndex) {

        // The -1 is arbitrary
        DistanceIndex minDistance = new DistanceIndex(Integer.MAX_VALUE, -1);
        int indexWithMin = -1;

        // Finds the minimum distance to any of the paths
        for (int i = 0; i < paths.size(); i++) {

            if (i == avoidIndex) {
                continue;
            }

            ContiguousPixelPath path = paths.get(i);

            DistanceIndex distance =
                    DistanceToContiguousPath.maxDistanceToClosestPoint(path, point);
            if (distance.getDistance() < minDistance.getDistance()) {
                minDistance = distance;
                indexWithMin = i;
            }
        }

        return new DistanceIndexTwice(
                minDistance.getDistance(), indexWithMin, minDistance.getIndex());
    }
}
