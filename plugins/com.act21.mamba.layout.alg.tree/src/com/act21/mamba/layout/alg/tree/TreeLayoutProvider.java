package com.act21.mamba.layout.alg.tree;

import java.util.ArrayList;
import java.util.HashMap;
import org.eclipse.elk.core.AbstractLayoutProvider;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.ElkBendPoint;
import org.eclipse.elk.graph.ElkEdge;
import org.eclipse.elk.graph.ElkEdgeSection;
import org.eclipse.elk.graph.ElkGraphFactory;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.emf.common.util.EList;

/**
 * A simple layout algorithm class. This algorithm already supports a number of
 * layout options, places nodes, and routes edges.....
 */
public class TreeLayoutProvider extends AbstractLayoutProvider {

	@Override
	public void layout(ElkNode layoutGraph, IElkProgressMonitor progressMonitor) {

		// Create edgesMap for all children of layout graph having "source node" as key
		// and list of all the "target nodes" from all the edges of that source node.
		HashMap<ElkNode, ArrayList<ElkNode>> edgesMap = createEdgesMap(layoutGraph.getContainedEdges());

		// Create edgesMap for all children of layout graph having "source node" as key
		// and a map of all the "target nodes" and respective "edges" emerging from
		// that source node.
		HashMap<ElkNode, HashMap<ElkNode, ElkEdge>> edgesObjectMap = createEdgesObject(layoutGraph.getContainedEdges());

		// Get Root node from the Tree graph
		ElkNode rootNode = layoutGraph.getChildren().get(0);

		// ElkTreeNode, a wrapper for ElkNode having list of children, parent and depth;
		ElkTreeNode rootTreeNode;
		ElkTreeNode parentRootTreeNode = null;
		ArrayList<ElkTreeNode> childrenRootTreeNode = new ArrayList<ElkTreeNode>();
		rootTreeNode = new ElkTreeNode(rootNode, parentRootTreeNode, childrenRootTreeNode);

		// Create tree of ElkTreeNode using root of ElkNode and edges
		createTree(rootTreeNode, edgesMap);

		// Do <pre> order traversal of ElkTreeNode tree and store those nodes in a
		// list(we will be assigning depth of each node in this function)
		ArrayList<ElkTreeNode> nodeList = new ArrayList<ElkTreeNode>();
		createTreeNodeList(rootTreeNode, nodeList, 0);

		// Set horizontal spacing (gap between each level of tree)
		// by getting max of widths from all the nodes
		double horizontal_spacing = 0.00;
		horizontal_spacing = getMaxHorizontalSpacing(rootTreeNode);

		// set vertical spacing by getting max of heights from all the nodes
		double vertical_spacing = 0.00;
		vertical_spacing = getMaxVerticalSpacing(rootTreeNode);

		// Create Indented Tree
		ElkTreeNode prevNode = null;
		for (int i = 0; i < nodeList.size(); i++) {
			ElkTreeNode c = nodeList.get(i);
			positionNode(c, prevNode, horizontal_spacing, true, vertical_spacing);
			prevNode = c;
		}

		// Create Routing points for all the edges in the graph
		createRoutingPoints(rootTreeNode, edgesObjectMap);
	}

	// Function to calculate max vertical spacing
	private double getMaxVerticalSpacing(ElkTreeNode root) {
		if (root == null) {
			return 0.00;
		}
		double maxH = root.getNode().getHeight();
		for (ElkTreeNode child : root.getChildren()) {
			double childH = getMaxVerticalSpacing(child);
			maxH = Math.max(maxH, childH);
		}
		return maxH;
	}

	// Function to calculate max horizontal spacing
	private double getMaxHorizontalSpacing(ElkTreeNode root) {
		if (root == null) {
			return 0.00;
		}
		double myIndent = root.getNode().getWidth();
		for (ElkTreeNode child : root.getChildren()) {
			double childIndent = getMaxHorizontalSpacing(child);
			myIndent = Math.max(myIndent, childIndent);
		}
		return myIndent;
	}

	// Function to create the edges map with source node, targetNodes and
	// corresponding edges objects
	private HashMap<ElkNode, HashMap<ElkNode, ElkEdge>> createEdgesObject(EList<ElkEdge> containedEdges) {
		HashMap<ElkNode, HashMap<ElkNode, ElkEdge>> edgesMap = new HashMap<ElkNode, HashMap<ElkNode, ElkEdge>>();
		for (ElkEdge edge : containedEdges) {
			ElkNode sourceNode = (ElkNode) edge.getSources().get(0);
			ElkNode targetNode = (ElkNode) edge.getTargets().get(0);
			if (edgesMap.containsKey(sourceNode)) {
				HashMap<ElkNode, ElkEdge> value = edgesMap.get(sourceNode);
				value.put(targetNode, edge);
				edgesMap.put(sourceNode, value);
			} else {
				HashMap<ElkNode, ElkEdge> value = new HashMap<ElkNode, ElkEdge>();
				value.put(targetNode, edge);
				edgesMap.put(sourceNode, value);
			}
		}
		return edgesMap;
	}

	// Function to create Routing points for each edge
	private void createRoutingPoints(ElkTreeNode parent, HashMap<ElkNode, HashMap<ElkNode, ElkEdge>> map) {
		if (parent == null || parent.getChildren() == null) {
			return;
		}
		for (ElkTreeNode child : parent.getChildren()) {
			ElkEdge edge = map.get(parent.getNode()).get(child.getNode());

			// Create edge section and add bending points for each child of the parent node
			ElkEdgeSection ec = ElkGraphFactory.eINSTANCE.createElkEdgeSection();
			ElkBendPoint bp2 = ElkGraphFactory.eINSTANCE.createElkBendPoint();
			double x = ((2 * parent.getNode().getX()) + parent.getNode().getWidth()) / 2;
			double y = ((2 * child.getNode().getY()) + child.getNode().getHeight()) / 2;
			bp2.set(x, y);
			ec.getBendPoints().add(bp2);
			ec.setStartLocation(parent.getNode().getX(), parent.getNode().getY());
			ec.setEndLocation(child.getNode().getX(), child.getNode().getY());
			ec.setStartX(((2 * parent.getNode().getX()) + parent.getNode().getWidth()) / 2);
			ec.setStartY(parent.getNode().getY() + parent.getNode().getHeight());
			ec.setEndX(child.getNode().getX());
			ec.setEndY(((2 * child.getNode().getY()) + child.getNode().getHeight()) / 2);
			edge.getSections().add(ec);
			createRoutingPoints(child, map);
		}
	}

	// Function to create list of all the nodes in the ElkTreeNode by traversing
	// nodes in a pre-order fashion
	private void createTreeNodeList(ElkTreeNode root, ArrayList<ElkTreeNode> nodeList, int depth) {
		if (root != null) {
			root.setDepth(depth);
			nodeList.add(root);
			for (ElkTreeNode child : root.getChildren()) {
				createTreeNodeList(child, nodeList, depth + 1);
			}
		}
	}

	// Function to create tree of ElkTreeNode using root and edgesMap
	private void createTree(ElkTreeNode Node, HashMap<ElkNode, ArrayList<ElkNode>> edgesMap) {
		if (edgesMap.containsKey(Node.getNode())) {
			for (ElkNode child : edgesMap.get(Node.getNode())) {
				ElkTreeNode babyChild = new ElkTreeNode(child, Node, new ArrayList<ElkTreeNode>());
				Node.getChildren().add(babyChild);
				createTree(babyChild, edgesMap);
			}
		}
		return;
	}

	// Function to create EdgesMap having source node and list of target Nodes
	private HashMap<ElkNode, ArrayList<ElkNode>> createEdgesMap(EList<ElkEdge> containedEdges) {
		HashMap<ElkNode, ArrayList<ElkNode>> edgesMap = new HashMap<ElkNode, ArrayList<ElkNode>>();
		for (ElkEdge edge : containedEdges) {
			ElkNode sourceNode = (ElkNode) edge.getSources().get(0);
			ElkNode targetNode = (ElkNode) edge.getTargets().get(0);
			if (edgesMap.containsKey(sourceNode)) {
				ArrayList<ElkNode> target = edgesMap.get(sourceNode);
				target.add(targetNode);
				edgesMap.put(sourceNode, target);
			} else {
				ArrayList<ElkNode> target = new ArrayList<ElkNode>();
				target.add(targetNode);
				edgesMap.put(sourceNode, target);
			}
		}
		return edgesMap;
	}

	// Function to position each node in the tree
	private void positionNode(ElkTreeNode root, ElkTreeNode previousNode, double horizontal_Space, Boolean dropcap,
			double verticalSpace) {
		if (!dropcap) {
			try {
				ElkTreeNode parent = root.getParent();
				if (root == parent.getChildren().get(0)) {
					root.getNode().setX(root.getNode().getX() + (horizontal_Space * root.getDepth()));
					if (previousNode != null) {
						root.getNode().setY(previousNode.getNode().getY());
					} else {
						root.getNode().setY(0);
					}
					return;
				}
			} catch (Exception e) {// skip to normal when a node has no parent
			}
		}
		root.getNode().setX(root.getNode().getX() + (horizontal_Space * root.getDepth()));
		if (previousNode != null) {
			root.getNode().setY(previousNode.getNode().getY() + previousNode.getNode().getHeight() + verticalSpace);
		} else {
			root.getNode().setY(0);
		}
	}
}
