/*-
 * #%L
 * anchor-image
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

package org.anchoranalysis.image.voxel.neighborhood;

import java.util.List;
import java.util.function.Function;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.graph.GraphWithPayload;
import org.anchoranalysis.image.voxel.neighborhood.EdgeAdder.AddEdge;
import org.anchoranalysis.image.voxel.object.ObjectCollection;
import org.anchoranalysis.image.voxel.object.ObjectCollectionFactory;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.spatial.box.Extent;

/**
 * Creates an undirected graph where each vertex is an object, and edge exists if the objects
 * neighbor.
 *
 * @author Owen Feehan
 * @param <V> vertex-type
 */
class NeighborGraphCreator<V> {

    private final EdgeAdderParameters edgeAdderParameters;

    /** iff true outputs an undirected graph, otherwise directed. */
    private boolean undirected = true;

    /**
     * Creates a graph of neighboring objects.
     *
     * @param preventObjectIntersection iff true, objects can only be neighbors, if they have no
     *     intersecting voxels.
     */
    public NeighborGraphCreator(boolean preventObjectIntersection) {
        edgeAdderParameters = new EdgeAdderParameters(preventObjectIntersection);
    }

    /**
     * Creates an edge from two neighboring vertices.
     *
     * @author Owen Feehan
     * @param <V> vertex-type
     * @param <E> edge-type
     */
    @FunctionalInterface
    public interface EdgeFromVertices<V, E> {
        E createEdge(V vertex1, V vertex2, int numberNeighboringPixels);
    }

    /**
     * Create the graph for a given list of vertices, where edges represent the number of
     * intersecting voxels between objects.
     *
     * @param vertices vertices to construct graph from.
     * @param vertexToObject converts the vertex to an object-mask (called repeatedly so should be
     *     low-cost).
     * @param sceneExtent the size of the image, the object-masks exist in.
     * @param do3D if true, the Z-dimension is also considered for neighbors. Otherwise, only the X
     *     and Y dimensions.
     * @return the newly created graph.
     * @throws CreateException if any objects are not fully contained in the scene.
     */
    public GraphWithPayload<V, Integer> createGraphIntersectingVoxels(
            List<V> vertices,
            Function<V, ObjectMask> vertexToObject,
            Extent sceneExtent,
            boolean do3D)
            throws CreateException {
        return createGraph(
                vertices,
                vertexToObject,
                (vertex1, vertex2, numberVoxels) -> numberVoxels,
                sceneExtent,
                do3D);
    }

    /**
     * Create the graph for a given list of vertices.
     *
     * @param vertices vertices to construct graph from.
     * @param vertexToObject converts the vertex to an object-mask (called repeatedly so should be
     *     low-cost).
     * @param edgeFromVertices creates an edge for two vertices (and the number of neighboring
     *     pixels).
     * @param sceneExtent the size of the image, the object-masks exist in.
     * @param do3D if true, the Z-dimension is also considered for neighbors. Otherwise, only the X
     *     and Y dimensions.
     * @param <E> edge-type of graph
     * @return the newly created graph
     * @throws CreateException if any objects are not fully contained in the scene.
     */
    private <E> GraphWithPayload<V, E> createGraph(
            List<V> vertices,
            Function<V, ObjectMask> vertexToObject,
            EdgeFromVertices<V, E> edgeFromVertices,
            Extent sceneExtent,
            boolean do3D)
            throws CreateException {

        // Graph of neighboring objects, with the number of common pixels as an edge
        GraphWithPayload<V, E> graph = new GraphWithPayload<>(undirected);

        // Objects from each vertex
        ObjectCollection objects = ObjectCollectionFactory.mapFrom(vertices, vertexToObject::apply);
        checkObjectsInScene(objects, sceneExtent);

        EdgeAdder<V> edgeAdder =
                new EdgeAdder<>(
                        vertices,
                        vertexToObject,
                        objects,
                        createAndAddEdge(graph, edgeFromVertices),
                        edgeAdderParameters);

        for (int i = 0; i < objects.size(); i++) {

            V vertexWith = vertices.get(i);
            graph.addVertex(vertexWith);

            edgeAdder.addEdgesFor(i, objects.get(i), vertexWith, sceneExtent, do3D);
        }

        return graph;
    }

    private static void checkObjectsInScene(ObjectCollection objects, Extent sceneExtent)
            throws CreateException {
        for (ObjectMask objectMask : objects) {
            if (!sceneExtent.contains(objectMask.boundingBox())) {
                throw new CreateException(
                        String.format(
                                "Object is not contained (fully or partially) inside scene extent: %s is not in %s",
                                objectMask.boundingBox(), sceneExtent));
            }
        }
    }

    private static <V, E> AddEdge<V> createAndAddEdge(
            GraphWithPayload<V, E> graph, EdgeFromVertices<V, E> edgeFromVertices) {
        return (vertex1, vertex2, numPixels) ->
                graph.addEdge(
                        vertex1, vertex2, edgeFromVertices.createEdge(vertex1, vertex2, numPixels));
    }
}
