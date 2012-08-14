/*
 *******************************************************************************
 * GraphTest.java
 * $Id: $
 *
 *******************************************************************************
 *
 * Copyright:   Q2WEB GmbH
 *              quality to the web
 *
 *              Tel  : +49 (0) 211 / 159694-00	Kronprinzenstr. 82-84
 *              Fax  : +49 (0) 211 / 159694-09	40217 Düsseldorf
 *              eMail: info@q2web.de						http://www.q2web.de
 *
 *
 * Author:      oliver.schrenk
 *
 * Created:     Aug 13, 2012
 *
 * Copyright (c) 2009 Q2WEB GmbH.
 * All rights reserved.
 *
 *******************************************************************************
 */
package de.q2web.jocl.graph;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * 
 * @author oliver.schrenk
 * @version
 *          $Id: $
 */
public class GraphTest {

	private static final int A=0;
	private static final int B=1;
	private static final int C=2;
	private static final int D=3;

	private static final int DEFAULT_WEIGHT=1;

	@Test
	public void test() {
		final int[] expectedVertexList={
		                                0, 2, 3
		};
		int[] expectedEdgeList={
		                        1, 2, 3, 3
		};
		int[] expectedWeightList={
		                          DEFAULT_WEIGHT, DEFAULT_WEIGHT, DEFAULT_WEIGHT, DEFAULT_WEIGHT
		};
		Graph graph=new Graph(3, 4);
		graph.addEdge(A, B, DEFAULT_WEIGHT);
		graph.addEdge(A, C, DEFAULT_WEIGHT);
		graph.addEdge(B, D, DEFAULT_WEIGHT);
		graph.addEdge(C, D, DEFAULT_WEIGHT);

		assertArrayEquals(expectedVertexList, graph.getVertexArray());
		assertArrayEquals(expectedEdgeList, graph.getEdgeArray());
		assertArrayEquals(expectedWeightList, graph.getWeightArray());
	}

}
