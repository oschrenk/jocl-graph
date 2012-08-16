package de.q2web.jocl.graph;

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
	final int[] edgeArray;

	/** The weight array. */
	final int[] weightArray;

	/** The edge sum. */
	int edgeSum;

	/** The last vertex id. */
	int lastVertexId;

	/** The number of vertices. */
	private final int numberOfVertices;

	/** The number of edges. */
	private final int numberOfEdges;

	/**
	 * Instantiates a new graph.
	 *
	 * @param numberOfVertices
	 *            the number of vertices
	 * @param numberOfEdges
	 *            the number of edges
	 */
	public Graph(final int numberOfVertices, final int numberOfEdges) {
		this.numberOfVertices = numberOfVertices;
		this.numberOfEdges = numberOfEdges;

		vertexArray = new int[numberOfVertices];
		edgeArray = new int[numberOfEdges];
		weightArray = new int[numberOfEdges];

		edgeSum = ZERO_SUM;
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
			vertexArray[from] = edgeSum;
		}
		edgeArray[edgeSum] = to;
		weightArray[edgeSum] = weight;
		edgeSum++;
	}

	/**
	 * Adds the vertex.
	 *
	 * @param vertex
	 *            the vertex
	 */
	public void addVertex(final int vertex) {
		vertexArray[vertex] = edgeSum;
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
		return edgeArray;
	}

	/**
	 * Gets the weight array.
	 *
	 * @return the weightArray
	 */
	protected int[] getWeightArray() {
		return weightArray;
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
		return numberOfEdges;
	}

}
