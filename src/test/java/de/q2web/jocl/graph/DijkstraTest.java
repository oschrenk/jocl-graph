package de.q2web.jocl.graph;

import static org.jocl.CL.CL_DEVICE_TYPE_GPU;
import static org.jocl.CL.clReleaseCommandQueue;
import static org.jocl.CL.clReleaseContext;

import org.jocl.cl_command_queue;
import org.jocl.cl_context;
import org.jocl.cl_device_id;
import org.jocl.cl_platform_id;
import org.jocl.utils.CommandQueues;
import org.jocl.utils.Contexts;
import org.jocl.utils.Devices;
import org.jocl.utils.Platforms;
import org.junit.BeforeClass;
import org.junit.Test;

public class DijkstraTest {

	private static final int A = 0;
	private static final int B = 1;
	private static final int C = 2;
	private static final int D = 3;

	private static final int DEFAULT_WEIGHT = 1;

	/** The graph. */
	private static Graph graph;

	@BeforeClass
	public static void setUp() {
		graph = new Graph(3, 4);
		graph.addEdge(A, B, DEFAULT_WEIGHT);
		graph.addEdge(A, C, DEFAULT_WEIGHT);
		graph.addEdge(B, D, DEFAULT_WEIGHT);
		graph.addEdge(C, D, DEFAULT_WEIGHT);
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
			final int sourceVertexId = 5;

			dijkstra.run(context, queue, sourceVertexId);
		} finally {
			clReleaseCommandQueue(queue);
			clReleaseContext(context);
		}

	}
}
