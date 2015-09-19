package com.sandbox.neural;

import java.util.ArrayList;
import java.util.List;

public class NodeLayer {
	private ArrayList<AbstractNode> nodes;

	public NodeLayer() {
		this.nodes = new ArrayList<AbstractNode>();
	}

	public NodeLayer(List<AbstractNode> nodes) {
		nodes = new ArrayList<AbstractNode>(nodes);
	}

	public void addNode(final AbstractNode n) {
		this.nodes.add(n);
	}

	public AbstractNode getNode(final int index) {
		return this.nodes.get(index);
	}

	public ArrayList<AbstractNode> getNodes() {
		return this.nodes;
	}

	public void setNodes(final ArrayList<AbstractNode> nodes) {
		this.nodes = nodes;
	}
}
