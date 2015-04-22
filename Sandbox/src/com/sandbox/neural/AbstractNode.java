package com.sandbox.neural;

import java.util.ArrayList;
import java.util.Random;

public abstract class AbstractNode
{
	protected ArrayList<AbstractNode>	parents;

	public void addParent(final AbstractNode n)
	{
		this.parents.add(n);
	}

	public abstract double getOutput();

	public ArrayList<AbstractNode> getParents()
	{
		return this.parents;
	}

	public void setParents(final ArrayList<AbstractNode> parents)
	{
		this.parents = parents;
	}

	public abstract void randomizeWeights(Random rng, double range);
}
