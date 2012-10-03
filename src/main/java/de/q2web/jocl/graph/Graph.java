package de.q2web.jocl.graph;

import java.util.LinkedList;
import java.util.List;

import com.google.common.primitives.Ints;

/**
 * The Class Graph.
 * 
 * @author Oliver Schrenk <oliver.schrenk@q2web.de>
 */
public class Graph {

	/** The Constant ZERO_SUM. */
	private static final int ZERO_SUM = 0;

	/** The Constant NO_VERTEX. */
	private static final int NO_VERTEX = Integer.MIN_VALUE;

	/** The vertex array. */
	final int[] vertexArray;

	/** The edge array. */
	final List<Integer> edgeList;

	/** The weight array. */
	final List<Integer> weightList;

	/** The edge sum. */
	int edgeCount;

	/** The last vertex id. */
	int lastVertexId;

	/** The number of vertices. */
	private final int numberOfVertices;

	/**
	 * Instantiates a new graph.
	 * 
	 * @param numberOfVertices
	 *            the number of vertices
	 * @param numberOfEdges
	 *            the number of edges
	 */
	public Graph(final int numberOfVertices) {
		this.numberOfVertices = numberOfVertices;

		vertexArray = new int[numberOfVertices];
		edgeList = new LinkedList<Integer>();
		weightList = new LinkedList<Integer>();

		edgeCount = ZERO_SUM;
		lastVertexId = -NO_VERTEX;
	}

	/**
	 * Adds the edge.
	 * 
	 * @param from
	 *            the from
	 * @param to
	 *            the to
	 * @param weight
	 *            the weight
	 */
	public void addEdge(final int from, final int to, final int weight) {
		if (from != lastVertexId) {
			lastVertexId = from;
			vertexArray[from] = edgeCount;
		}
		edgeList.add(to);
		weightList.add(weight);
		edgeCount++;
	}

	/**
	 * Gets the vertex array.
	 * 
	 * @return the vertexArray
	 */
	protected int[] getVertexArray() {
		return vertexArray;
	}

	/**
	 * Gets the edge array.
	 * 
	 * @return the edgeArray
	 */
	protected int[] getEdgeArray() {
		return Ints.toArray(edgeList);
	}

	/**
	 * Gets the weight array.
	 * 
	 * @return the weightArray
	 */
	protected int[] getWeightArray() {
		return Ints.toArray(weightList);
	}

	/**
	 * Gets the number of vertices.
	 * 
	 * @return the number of vertices
	 */
	protected int getVertexCount() {
		return numberOfVertices;
	}

	/**
	 * Gets the number of edges.
	 * 
	 * @return the number of edges
	 */
	protected int getEdgeCount() {
		return edgeList.size();
	}

}
