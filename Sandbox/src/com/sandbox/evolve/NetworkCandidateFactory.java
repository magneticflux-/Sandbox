package com.sandbox.evolve;

import java.util.Random;

import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory;

import com.sandbox.neural.AbstractNode;
import com.sandbox.neural.FeedforwardNetwork;
import com.sandbox.neural.NeuronNode;
import com.sandbox.neural.NodeLayer;

public class NetworkCandidateFactory extends AbstractCandidateFactory<FeedforwardNetwork>
{
	private String	networkLayout;
	private double	weightRange;

	public NetworkCandidateFactory(String networkLayout, double weightRange)
	{
		super();
		this.networkLayout = networkLayout;
		this.weightRange = weightRange;
	}

	@Override
	public FeedforwardNetwork generateRandomCandidate(Random rng)
	{
		FeedforwardNetwork n = new FeedforwardNetwork(networkLayout);
		for (NodeLayer l : n.getLayers())
		{
			for (AbstractNode abn : l.getNodes())
			{
				if (abn instanceof NeuronNode)
				{
					for (int i = 0; i < ((NeuronNode) abn).getWeights().size(); i++)
					{
						((NeuronNode) abn).getWeights().set(i, 2 * (rng.nextDouble() - 0.5) * weightRange);
					}
				}
			}
		}
		return n;
	}
}
