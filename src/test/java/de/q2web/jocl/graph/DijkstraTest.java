package de.q2web.jocl.graph;

import static org.jocl.CL.CL_DEVICE_TYPE_GPU;
import static org.jocl.CL.clBuildProgram;
import static org.jocl.CL.clCreateProgramWithSource;
import static org.jocl.CL.clReleaseCommandQueue;
import static org.jocl.CL.clReleaseContext;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;

import org.jocl.CL;
import org.jocl.cl_command_queue;
import org.jocl.cl_context;
import org.jocl.cl_device_id;
import org.jocl.cl_platform_id;
import org.jocl.cl_program;
import org.jocl.utils.CommandQueues;
import org.jocl.utils.Contexts;
import org.jocl.utils.Devices;
import org.jocl.utils.Platforms;
import org.jocl.utils.Programs;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.Lists;

public class DijkstraTest {

	// vertices
	private static final int O = 0;
	private static final int A = 1;
	private static final int B = 2;
	private static final int C = 3;
	private static final int D = 4;
	private static final int E = 5;
	private static final int F = 6;
	private static final int T = 7;

	@BeforeClass
	public static void setUp() {
		// Enable exceptions and subsequently omit error checks in this sample
		CL.setExceptionsEnabled(true);
	}

	@Test
	public void testBuildProgram() {
		final cl_platform_id platformId = Platforms.getPlatforms().get(0);
		final cl_device_id deviceId = Devices.getDevices(platformId,
				CL_DEVICE_TYPE_GPU).get(0);
		final cl_context context = Contexts.create(platformId, deviceId);

		final cl_program program = clCreateProgramWithSource(context, 1,
				new String[] { Dijkstra.SOURCE }, null, null);
		final int returnCode = clBuildProgram(program, 0, null, null, null,
				null);

		if (returnCode != CL.CL_SUCCESS) {
			final String buildLogs = Programs.obtainBuildLogs(program);
			System.err.println(buildLogs);
			fail();
		}
	}

	@Test
	public void testVerySimple() {

		Graph graph = new Graph(T + 1);
		graph.addEdge(O, A, 1);
		graph.addEdge(O, T, 1);
		graph.addEdge(A, B, 1);
		graph.addEdge(B, C, 1);
		graph.addEdge(C, D, 1);
		graph.addEdge(D, E, 1);
		graph.addEdge(E, F, 1);
		graph.addEdge(F, T, 1);
		// is allowed because D is the target, so we don't have cycles
		graph.addEdge(T, 0, 666);

		// int[] vertexArray = graph.getVertexArray();
		// int[] edgeArray = graph.getEdgeArray();

		final cl_platform_id platformId = Platforms.getPlatforms().get(0);
		final cl_device_id deviceId = Devices.getDevices(platformId,
				CL_DEVICE_TYPE_GPU).get(0);
		final cl_context context = Contexts.create(platformId, deviceId);
		final cl_command_queue queue = CommandQueues.create(context, deviceId);
		try {
			final Dijkstra dijkstra = new Dijkstra(graph);
			final int sourceVertexId = O;
			final int targetVertexId = T;
			final int expectedLowestCost = 1;

			dijkstra.run(context, queue, sourceVertexId, targetVertexId);

			final int actualLowestCost = dijkstra.getCost();

			assertEquals(expectedLowestCost, actualLowestCost);

		} finally {
			clReleaseCommandQueue(queue);
			clReleaseContext(context);
		}

	}

	@Test
	public void testRun() {

		Graph graph = new Graph(4);
		graph.addEdge(O, A, 4);
		graph.addEdge(O, B, 2);
		graph.addEdge(A, B, 3);
		graph.addEdge(A, C, 1);
		graph.addEdge(B, O, 2);
		graph.addEdge(B, A, 1);
		graph.addEdge(B, C, 5);
		// is allowed because D is the target, so we don't have cycles
		graph.addEdge(C, O, 666);

		final cl_platform_id platformId = Platforms.getPlatforms().get(0);
		final cl_device_id deviceId = Devices.getDevices(platformId,
				CL_DEVICE_TYPE_GPU).get(0);
		final cl_context context = Contexts.create(platformId, deviceId);
		final cl_command_queue queue = CommandQueues.create(context, deviceId);
		try {
			final Dijkstra dijkstra = new Dijkstra(graph);
			final int sourceVertexId = O;
			final int targetVertexId = C;
			final int expectedLowestCost = 4;

			dijkstra.run(context, queue, sourceVertexId, targetVertexId);

			final int actualLowestCost = dijkstra.getCost();

			assertEquals(expectedLowestCost, actualLowestCost);

		} finally {
			clReleaseCommandQueue(queue);
			clReleaseContext(context);
		}

	}

	/**
	 * Example from
	 * http://optlab-server.sce.carleton.ca/POAnimations2007/DijkstrasAlgo.html
	 */
	@Test
	public void testAnotherGraph() {
		Graph graph = new Graph(8);
		graph.addEdge(O, A, 2);
		graph.addEdge(O, B, 5);
		graph.addEdge(O, C, 4);

		graph.addEdge(A, B, 2);
		graph.addEdge(A, F, 12);

		graph.addEdge(B, C, 1);
		graph.addEdge(B, D, 4);
		graph.addEdge(B, E, 3);
		graph.addEdge(C, E, 4);

		graph.addEdge(D, E, 1);
		graph.addEdge(D, T, 5);

		graph.addEdge(E, T, 7);

		graph.addEdge(F, T, 3);

		// is allowed because D is the target, so we don't have cycles
		graph.addEdge(T, 0, 666);

		final cl_platform_id platformId = Platforms.getPlatforms().get(0);
		final cl_device_id deviceId = Devices.getDevices(platformId,
				CL_DEVICE_TYPE_GPU).get(0);
		final cl_context context = Contexts.create(platformId, deviceId);
		final cl_command_queue queue = CommandQueues.create(context, deviceId);
		try {
			final Dijkstra dijkstra = new Dijkstra(graph);
			final int sourceVertexId = O;
			final int targetVertexId = T;
			final int expectedLowestCost = 13;
			final List<Integer> expectedPath = Lists
					.newArrayList(O, A, B, D, T);

			dijkstra.run(context, queue, sourceVertexId, targetVertexId);

			final int actualLowestCost = dijkstra.getCost();
			final List<Integer> actualPath = dijkstra.getPath();

			assertEquals(expectedLowestCost, actualLowestCost);
			assertEquals(expectedPath, actualPath);

		} finally {
			clReleaseCommandQueue(queue);
			clReleaseContext(context);
		}

	}
}
