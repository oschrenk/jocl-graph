package de.q2web.jocl.graph;

import static org.jocl.CL.CL_MEM_COPY_HOST_PTR;
import static org.jocl.CL.CL_MEM_READ_ONLY;
import static org.jocl.CL.CL_MEM_READ_WRITE;
import static org.jocl.CL.CL_TRUE;
import static org.jocl.CL.clBuildProgram;
import static org.jocl.CL.clCreateBuffer;
import static org.jocl.CL.clCreateKernel;
import static org.jocl.CL.clCreateProgramWithSource;
import static org.jocl.CL.clEnqueueNDRangeKernel;
import static org.jocl.CL.clEnqueueReadBuffer;
import static org.jocl.CL.clReleaseKernel;
import static org.jocl.CL.clReleaseMemObject;
import static org.jocl.CL.clReleaseProgram;
import static org.jocl.CL.clSetKernelArg;

import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_command_queue;
import org.jocl.cl_context;
import org.jocl.cl_kernel;
import org.jocl.cl_mem;
import org.jocl.cl_program;

import de.q2web.jocl.util.Resources;

/**
 * The Class Dijkstra.
 *
 * @author Oliver Schrenk <oliver.schrenk@q2web.de>
 */
public class Dijkstra {

	/** Source code of the kernels. */
	protected static final String SOURCE = Resources
			.convertStreamToString(Dijkstra.class
					.getResourceAsStream("dijkstraKernels.cl"));

	/** The Constant DEFAULT_LOCAL_WORKSIZE. */
	private static final long[] DEFAULT_LOCAL_WORKSIZE = new long[] { 1 };

	/** The Constant KERNEL_INITIALIZATION. */
	private static final String KERNEL_INITIALIZATION = "dijkstra_initialize";

	/** The Constant KERNEL_SSSP_1. */
	private static final String KERNEL_SSSP_1 = "dijkstra_sssp1";

	/** The Constant KERNEL_SSSP_2. */
	private static final String KERNEL_SSSP_2 = "dijkstra_sssp2";

	/** The graph. */
	private final Graph graph;

	/**
	 * Instantiates a new Dijkstra.
	 *
	 * @param graph
	 *            the graph
	 */
	public Dijkstra(final Graph graph) {
		this.graph = graph;
	}

	/**
	 * Run.
	 *
	 * @param context
	 *            the context
	 * @param queue
	 *            the queue
	 * @param sourceVertexId
	 *            the source vertex id
	 * @return
	 */
	public int run(final cl_context context, final cl_command_queue queue,
			final int sourceVertexId, final int targetVertexId) {
		// initialize arrays
		cl_program program = null;
		cl_kernel intializationKernel = null;
		cl_kernel sssp1Kernel = null;
		cl_kernel sssp2Kernel = null;
		cl_mem[] memObject = null;
		try {
			program = clCreateProgramWithSource(context, 1,
					new String[] { SOURCE }, null, null);

			clBuildProgram(program, 0, null, null, null, null);

			final int vertexCount = graph.getVertexArray().length;
			final int edgeCount = graph.getEdgeArray().length;

			final int[] vertexArray = graph.getVertexArray();
			final int[] edgeArray = graph.getEdgeArray();
			final int[] weightArray = graph.getWeightArray();

			final Pointer vertexArrayPointer = Pointer.to(vertexArray);
			final Pointer edgeArrayPointer = Pointer.to(edgeArray);
			final Pointer weightArrayPointer = Pointer.to(weightArray);

			memObject = new cl_mem[6];
			memObject[0] = clCreateBuffer(context, CL_MEM_READ_ONLY
					| CL_MEM_COPY_HOST_PTR,
					Sizeof.cl_uint * vertexArray.length, vertexArrayPointer,
					null);
			memObject[1] = clCreateBuffer(context, CL_MEM_READ_ONLY
					| CL_MEM_COPY_HOST_PTR, Sizeof.cl_uint * edgeCount,
					edgeArrayPointer, null);
			memObject[2] = clCreateBuffer(context, CL_MEM_READ_ONLY
					| CL_MEM_COPY_HOST_PTR,
					Sizeof.cl_uint * weightArray.length, weightArrayPointer,
					null);

			memObject[3] = clCreateBuffer(context, CL_MEM_READ_WRITE,
					Sizeof.cl_uint * vertexCount, null, null);
			memObject[4] = clCreateBuffer(context, CL_MEM_READ_WRITE,
					Sizeof.cl_uint * vertexCount, null, null);
			memObject[5] = clCreateBuffer(context, CL_MEM_READ_WRITE,
					Sizeof.cl_uint * vertexCount, null, null);

			intializationKernel = clCreateKernel(program,
					KERNEL_INITIALIZATION, null);
			sssp1Kernel = clCreateKernel(program, KERNEL_SSSP_1, null);
			sssp2Kernel = clCreateKernel(program, KERNEL_SSSP_2, null);

			// Initialization
			//
			// __global int *maskArray,
			clSetKernelArg(intializationKernel, 0, Sizeof.cl_mem,
					Pointer.to(memObject[3]));
			// __global float *costArray,
			clSetKernelArg(intializationKernel, 1, Sizeof.cl_mem,
					Pointer.to(memObject[4]));
			// __global float *updatingCostArray,
			clSetKernelArg(intializationKernel, 2, Sizeof.cl_mem,
					Pointer.to(memObject[5]));
			// int sourceVertexId
			clSetKernelArg(intializationKernel, 3, Sizeof.cl_uint,
					Pointer.to(new int[] { sourceVertexId }));
			// int vertexCount
			clSetKernelArg(intializationKernel, 4, Sizeof.cl_uint,
					Pointer.to(new int[] { vertexCount }));

			final long[] globalWorkSize = new long[] { vertexCount };
			clEnqueueNDRangeKernel(queue, intializationKernel, 1, null,
					globalWorkSize, DEFAULT_LOCAL_WORKSIZE, 0, null, null);

			final int[] maskArray = new int[vertexCount];
			final Pointer maskArrayPointer = Pointer.to(maskArray);
			do {
				// Enqueue Kernel SSSP 1
				//
				// __global int *vertexArray,
				clSetKernelArg(sssp1Kernel, 0, Sizeof.cl_mem,
						Pointer.to(memObject[0]));
				// __global float *edgeArray,
				clSetKernelArg(sssp1Kernel, 1, Sizeof.cl_mem,
						Pointer.to(memObject[1]));
				// __global float *weightArray,
				clSetKernelArg(sssp1Kernel, 2, Sizeof.cl_mem,
						Pointer.to(memObject[2]));
				// __global int *maskArray,
				clSetKernelArg(sssp1Kernel, 3, Sizeof.cl_mem,
						Pointer.to(memObject[3]));
				// __global float *costArray,
				clSetKernelArg(sssp1Kernel, 4, Sizeof.cl_mem,
						Pointer.to(memObject[4]));
				// __global float *updatingCostArray,
				clSetKernelArg(sssp1Kernel, 5, Sizeof.cl_mem,
						Pointer.to(memObject[5]));
				// int vertexCount
				clSetKernelArg(sssp1Kernel, 6, Sizeof.cl_uint,
						Pointer.to(new int[] { vertexCount }));
				// int edgeCount
				clSetKernelArg(sssp1Kernel, 7, Sizeof.cl_uint,
						Pointer.to(new int[] { edgeCount }));
				//
				clEnqueueNDRangeKernel(queue, sssp1Kernel, 1, null,
						globalWorkSize, DEFAULT_LOCAL_WORKSIZE, 0, null, null);

				// Enqueue Kernel SSSP 2
				// __global int *maskArray,
				clSetKernelArg(sssp2Kernel, 0, Sizeof.cl_mem,
						Pointer.to(memObject[3]));
				// __global float *costArray,
				clSetKernelArg(sssp2Kernel, 1, Sizeof.cl_mem,
						Pointer.to(memObject[4]));
				// __global float *updatingCostArray
				clSetKernelArg(sssp2Kernel, 2, Sizeof.cl_mem,
						Pointer.to(memObject[5]));

				clEnqueueNDRangeKernel(queue, sssp2Kernel, 1, null,
						globalWorkSize, DEFAULT_LOCAL_WORKSIZE, 0, null, null);

				// read mask array
				clEnqueueReadBuffer(queue, memObject[3], CL_TRUE, 0,
						Sizeof.cl_uint * vertexCount, maskArrayPointer, 0,
						null, null);
			} while (!isEmpty(maskArray));

			// read cost array
			final int[] costArray = new int[vertexCount];
			final Pointer costArrayPointer = Pointer.to(costArray);
			clEnqueueReadBuffer(queue, memObject[4], CL_TRUE, 0, Sizeof.cl_uint
					* vertexCount, costArrayPointer, 0, null, null);

			return costArray[targetVertexId];

		} finally {
			// release kernel, program, and memory objects
			clReleaseMemObject(memObject[0]);
			clReleaseMemObject(memObject[1]);
			clReleaseMemObject(memObject[2]);
			clReleaseMemObject(memObject[3]);
			clReleaseMemObject(memObject[4]);
			clReleaseMemObject(memObject[5]);

			clReleaseKernel(intializationKernel);
			clReleaseKernel(sssp1Kernel);
			clReleaseKernel(sssp2Kernel);

			clReleaseProgram(program);
		}

	}

	private boolean isEmpty(final int[] ints) {
		for (final int i : ints) {
			if (i != 0) {
				return false;
			}
		}
		return true;
	}

}
