package com.sandbox.neural;

import java.util.ArrayList;

public abstract class AbstractNode
{
	protected ArrayList<AbstractNode>	parents;

	public abstract double getOutput();

	public ArrayList<AbstractNode> getParents()
	{
		return parents;
	}

	public void setParents(ArrayList<AbstractNode> parents)
	{
		this.parents = parents;
	}

	public void addParent(AbstractNode n)
	{
		this.parents.add(n);
	}
}
