The algorithm uses the programmatic structure of SPOrE, which stands 
for Structure, Processing Order, and Execution.

### The Three Phases
1. The structure phase extracts geometric information from the given layout
   to capture the topology that should be preserved during the subsequent
   adjustment of the layout.
   
   For this purpose, a Delaunay triangulation is implemented, which captures proximity information.
2. Now, the processing order for the last phase is determined and represented by a tree.
3. The execution phase traverses the previously constructed tree and applies an algorithm specific 
   operation to each node.


### The ShrinkTree Approach
The functional principle is based on a modification of an overlap 
removal algorithm presented by Nachmanson et al. in "Node overlap removal by growing a tree". 
First, the set of vertices corresponding to the nodes' centers is Delaunay triangulated. 
Second, a spanning tree of the triangulation edges is constructed. 
Third, the spanning tree is shrunk pulling the nodes closer together. 

### Use Case
Compacting the inclusion tree of a hierarchical Ptolemy model:

![original](/images/controller-ro.png)
![compact](/images/controller-ros.png)
