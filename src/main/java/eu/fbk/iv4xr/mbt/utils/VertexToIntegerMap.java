package eu.fbk.iv4xr.mbt.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.jgrapht.util.CollectionUtil;

/**
 * Helper class for building a one-to-one mapping for a collection of vertices to the integer range
 * $[0, n)$ where $n$ is the number of vertices in the collection.
 *
 * <p>
 * This class computes the mapping only once, on instantiation. It does not support live updates.
 * </p>
 *
 * @author Alexandru Valeanu
 *
 * January 2021, copied and updated to make it Serializable
 *
 * @param <V> the graph vertex type
 */
public class VertexToIntegerMap <V> implements Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4603576268692322412L;
	private final Map<V, Integer> vertexMap;
    private final List<V> indexList;

    /**
     * Create a new mapping from a list of vertices. The input list will be used as the
     * {@code indexList} so it must not be modified.
     *
     * @param vertices the input list of vertices
     * @throws NullPointerException if {@code vertices} is {@code null}
     * @throws IllegalArgumentException if the vertices are not distinct
     */
    public VertexToIntegerMap(List<V> vertices)
    {
        Objects.requireNonNull(vertices, "the input collection of vertices cannot be null");

        vertexMap = CollectionUtil.newHashMapWithExpectedSize(vertices.size());
        indexList = vertices;

        for (V v : vertices) {
            if (vertexMap.put(v, vertexMap.size()) != null) {
                throw new IllegalArgumentException("vertices are not distinct");
            }
        }
    }

    /**
     * Create a new mapping from a collection of vertices.
     *
     * @param vertices the input collection of vertices
     * @throws NullPointerException if {@code vertices} is {@code null}
     * @throws IllegalArgumentException if the vertices are not distinct
     */
    public VertexToIntegerMap(Collection<V> vertices)
    {
        this(
            new ArrayList<>(
                Objects
                    .requireNonNull(vertices, "the input collection of vertices cannot be null")));
    }

    /**
     * Get the {@code vertexMap}, a mapping from vertices to integers (i.e. the inverse of
     * {@code indexList}).
     *
     * @return a mapping from vertices to integers
     */
    public Map<V, Integer> getVertexMap()
    {
        return vertexMap;
    }

    /**
     * Get the {@code indexList}, a mapping from integers to vertices (i.e. the inverse of
     * {@code vertexMap}).
     *
     * @return a mapping from integers to vertices
     */
    public List<V> getIndexList()
    {
        return indexList;
    }

}
