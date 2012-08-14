package de.q2web.jocl.graph;

/**
 *
 * @author Oliver Schrenk <oliver.schrenk@q2web.de>
 *
 */
public class Graph {

	private static final int ZERO_SUM = 0;
	private static final int NO_VERTEX = Integer.MIN_VALUE;

	final int[] vertexArray;
	final int[] edgeArray;
	final int[] weightArray;
	int edgeSum;
	int lastVertexId;

	/**
	 *
	 */
	public Graph(final int numberOfVerticesWithOutgoingEdges,
			final int numberOfEdges) {
		vertexArray = new int[numberOfVerticesWithOutgoingEdges];
		edgeArray = new int[numberOfEdges];
		weightArray = new int[numberOfEdges];

		edgeSum = ZERO_SUM;
		lastVertexId = -NO_VERTEX;
	}

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
	 * @return the vertexArray
	 */
	protected int[] getVertexArray() {
		return vertexArray;
	}

	/**
	 * @return the edgeArray
	 */
	protected int[] getEdgeArray() {
		return edgeArray;
	}

	/**
	 * @return the weightArray
	 */
	protected int[] getWeightArray() {
		return weightArray;
	}

}
