/**
 * Implementation of Dijkstra's Single-Source Shortest Path (SSSP) algorithm on
 * the GPU. 
 *
 * <p>
 * The basis of this implementation in the paper "Accelerating large graph 
 * algorithms on the GPU using CUDA" by Parwan Harish and P.J. Narayanan
 *
 * <p>
 * A graph is composed of vertices and edges that connect these vertices. Each
 * edge has a number associated with, also called a weight. This weight is the
 * cost of travelling the edge (think of it as the physical distance).
 *
 * <p>
 * <b>Construction</b>
 * Normally you would represent a graph G (V, E)(with V being a set vertices, 
 * E being a set of edges between these vertices ) as an adjacency matrix. But
 * given the  fact that we operate on a very sparse matrix, we implement 
 * adjacency lists via two arrays, <code>vertexArray</code>, with 
 * <code>size(vertexArray)=O(V)</code> and <code>edgeArray</code>, with 
 * <code>size(edgeArray)=O(E)</code>. 
 * 
 * The <code>vertexArray</code> describes all vertices that have outgoing edges.
 * As not all vertices have outgoing edges another array on the host should 
 * If there are vertices that don't have outgoing edges, an array on the host 
 * should be created to map the vertex id to its <code>vertexArray</code> index.
 * The <code>edgeArray</code> contains indexes pointing to the vertex array. 
 *
 * Thus uni-directional edges are realized.
 *
 * @author Dan Ginsburg <daniel.ginsburg@childrens.harvard.edu>
 *
 * @param vertexArray
 *			each entry contains an index that points to <code>edgeArray</code>.
 *			The entry is the edge for that vertex.
 * @param edgeArray
 *			
 * @param weightArray
 * 			stores the weight for each edge
 * @param maskArray
 * @param costArray
 * @param updatingCostArray
 * @param vertexCount
 * @param edgeCount
 */
__kernel  void dijkstra_sssp1(
	__global uint *vertexArray,
	__global uint *edgeArray,
	__global uint *weightArray,
	__global uint *maskArray,
	__global uint *costArray,
	__global uint *updatingCostArray,
	const uint vertexCount,
	const uint edgeCount
) {
    // access thread id
    int tid = get_global_id(0);

    if ( maskArray[tid] != 0 )
    {
        maskArray[tid] = 0;

        int edgeStart = vertexArray[tid];
        int edgeEnd;
        if (tid + 1 < (vertexCount)) {
            edgeEnd = vertexArray[tid + 1];
        } else {
            edgeEnd = edgeCount;
        }

		for(int edge = edgeStart; edge < edgeEnd; edge++) {
			int nid = edgeArray[edge];
			
			// One note here: whereas the paper specified weightArray[nid], I
			//  found that the correct thing to do was weightArray[edge].  I think
			//  this was a typo in the paper.  Either that, or I misunderstood
			//  the data structure.
			if (updatingCostArray[nid] > (costArray[tid] + weightArray[edge])) {
			    updatingCostArray[nid] = (costArray[tid] + weightArray[edge]);
			}
		}
    }
}

/**
 * Implementation of Dijkstra's Single-Source Shortest Path (SSSP) algorithm on
 * the GPU. 
 *
 * <p>
 * This is the second kernel
 *
 * @author Dan Ginsburg <daniel.ginsburg@childrens.harvard.edu>
 *
 * @param vertexArray
 * @param edgeArray
 * @param weightArray
 * @param maskArray
 * @param costArray
 * @param updatingCostArray
 * @param vertexCount
 */
__kernel  void dijkstra_sssp2(
	__global uint *maskArray,
	__global uint *costArray,
	__global uint *updatingCostArray
) {
    // access thread id
    int tid = get_global_id(0);

    if (costArray[tid] > updatingCostArray[tid]) {
        costArray[tid] = updatingCostArray[tid];
        maskArray[tid] = 1;
    }

    updatingCostArray[tid] = costArray[tid];
}

/**
 * Implementation of Dijkstra's Single-Source Shortest Path (SSSP) algorithm on
 * the GPU. 
 *
 * <p>
 * Buffer initialization. This step saves time transferring data from host to
 * the device.
 *
 * <ul>
 *  <li>set <code>maskArray[]</code> to <code>0</code></li>
 *  <li>set <code>maskArray[sourceVertex]</code> to <code>1</code></li>
 *  <li>set <code>costArray[]</code> to <code>INT_MAX</code></li>
 *  <li>set <code>costArray[sourceVertex]</code> to <code>0</code></li>
 *  <li>set <code>updatingCostArray[]</code> to <code>INT_MAX</code></li>
 *  <li>set <code>updatingCostArray[sourceVertex]</code> to <code>1</code></li>
 * </ul>
 *
 * @author Dan Ginsburg <daniel.ginsburg@childrens.harvard.edu>
 *
 * @param maskArray
 * @param costArray
 * @param updatingCostArray
 * @param sourceVertex
 * @param vertexCount
 */
__kernel void dijkstra_initialize(
	__global uint *maskArray,
	__global uint *costArray,
	__global uint *updatingCostArray,
	const uint sourceVertex,
	const uint vertexCount
) {
    // access thread id
    int tid = get_global_id(0);

	if (sourceVertex == tid) {
	    maskArray[tid] = 1;
	    costArray[tid] = 0;
	    updatingCostArray[tid] = 0;
	} else {
	    maskArray[tid] = 0;
	    costArray[tid] = INT_MAX;
	    updatingCostArray[tid] = INT_MAX;
	}
}