package de.q2web.jocl.graph;

import static org.jocl.CL.CL_DEVICE_TYPE_GPU;
import static org.jocl.CL.clBuildProgram;
import static org.jocl.CL.clCreateProgramWithSource;
import static org.jocl.CL.clReleaseCommandQueue;
import static org.jocl.CL.clReleaseContext;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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

public class DijkstraTest {

	// vertices
	private static final int A = 0;
	private static final int B = 1;
	private static final int C = 2;
	private static final int D = 3;

	/** The graph. */
	private static Graph graph;

	@BeforeClass
	public static void setUp() {
		// Enable exceptions and subsequently omit error checks in this sample
		CL.setExceptionsEnabled(true);

		graph = new Graph(4, 7);
		graph.addEdge(A, B, 4);
		graph.addEdge(A, C, 2);
		graph.addEdge(B, C, 3);
		graph.addEdge(B, D, 1);
		graph.addEdge(C, A, 2);
		graph.addEdge(C, B, 1);
		graph.addEdge(C, D, 5);
		graph.addVertex(D);
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
	public void testRun() {

		final cl_platform_id platformId = Platforms.getPlatforms().get(0);
		final cl_device_id deviceId = Devices.getDevices(platformId,
				CL_DEVICE_TYPE_GPU).get(0);
		final cl_context context = Contexts.create(platformId, deviceId);
		final cl_command_queue queue = CommandQueues.create(context, deviceId);
		try {
			final Dijkstra dijkstra = new Dijkstra(graph);
			final int sourceVertexId = A;
			final int targetVertexId = D;
			final int expectedLowestCost = 4;
			final int actualLowestCost = dijkstra.run(context, queue,
					sourceVertexId, targetVertexId);

			assertEquals(expectedLowestCost, actualLowestCost);

		} finally {
			clReleaseCommandQueue(queue);
			clReleaseContext(context);
		}

	}
}
