package de.q2web.jocl.graph;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

/**
 *
 * @author Oliver Schrenk <oliver.schrenk@q2web.de>
 * @version $Id: $
 */
public class GraphTest {

	// vertices
	private static final int A = 0;
	private static final int B = 1;
	private static final int C = 2;
	private static final int D = 3;

	private static final int DEFAULT_WEIGHT = 1;

	@Test
	public void testUniDirectional() {
		final int[] expectedVertexList = { 0, 2, 3, 4 };
		final int[] expectedEdgeList = { 1, 2, 3, 3 };
		final int[] expectedWeightList = { DEFAULT_WEIGHT, DEFAULT_WEIGHT,
				DEFAULT_WEIGHT, DEFAULT_WEIGHT };

		final Graph graph = new Graph(4, 4);
		graph.addEdge(A, B, DEFAULT_WEIGHT);
		graph.addEdge(A, C, DEFAULT_WEIGHT);
		graph.addEdge(B, D, DEFAULT_WEIGHT);
		graph.addEdge(C, D, DEFAULT_WEIGHT);
		graph.addVertex(D);

		assertArrayEquals(expectedVertexList, graph.getVertexArray());
		assertArrayEquals(expectedEdgeList, graph.getEdgeArray());
		assertArrayEquals(expectedWeightList, graph.getWeightArray());
	}

	@Test
	public void testBiDirectional() {
		final int[] expectedVertexList = { 0, 2, 4, 7 };
		final int[] expectedEdgeList = { 1, 2, 2, 3, 0, 1, 3 };
		final int[] expectedWeightList = { 4, 2, 3, 1, 2, 1, 5 };

		final Graph graph = new Graph(4, 7);
		graph.addEdge(A, B, 4);
		graph.addEdge(A, C, 2);
		graph.addEdge(B, C, 3);
		graph.addEdge(B, D, 1);
		graph.addEdge(C, A, 2);
		graph.addEdge(C, B, 1);
		graph.addEdge(C, D, 5);
		graph.addVertex(D);

		assertArrayEquals(expectedVertexList, graph.getVertexArray());
		assertArrayEquals(expectedEdgeList, graph.getEdgeArray());
		assertArrayEquals(expectedWeightList, graph.getWeightArray());
	}

}
