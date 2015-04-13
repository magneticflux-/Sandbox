package com.sandbox.neural;

import java.util.ArrayList;
import java.util.List;

public class NodeLayer
{
	private ArrayList<AbstractNode>	nodes;

	public NodeLayer(List<AbstractNode> nodes)
	{
		nodes = new ArrayList<AbstractNode>(nodes);
	}

	public NodeLayer()
	{
		nodes = new ArrayList<AbstractNode>();
	}

	public ArrayList<AbstractNode> getNodes()
	{
		return nodes;
	}

	public void setNodes(ArrayList<AbstractNode> nodes)
	{
		this.nodes = nodes;
	}

	public void addNode(AbstractNode n)
	{
		nodes.add(n);
	}

	public AbstractNode getNode(int index)
	{
		return nodes.get(index);
	}
}
