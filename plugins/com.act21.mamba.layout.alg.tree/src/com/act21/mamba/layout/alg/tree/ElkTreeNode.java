package com.act21.mamba.layout.alg.tree;

import java.util.ArrayList;

import org.eclipse.elk.graph.ElkNode;

public class ElkTreeNode {
	private ElkNode node;
	private ElkTreeNode parent;
	private ArrayList<ElkTreeNode> children;
	private int depth;

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public ElkNode getNode() {
		return node;
	}

	public void setNode(ElkNode node) {
		this.node = node;
	}

	public ElkTreeNode getParent() {
		return parent;
	}

	public void setParent(ElkTreeNode parent) {
		this.parent = parent;
	}

	public ElkTreeNode(ElkNode node, ElkTreeNode parent, ArrayList<ElkTreeNode> children) {
		super();
		this.node = node;
		this.parent = parent;
		this.children = children;
	}

	public ArrayList<ElkTreeNode> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<ElkTreeNode> children) {
		this.children = children;
	}
}