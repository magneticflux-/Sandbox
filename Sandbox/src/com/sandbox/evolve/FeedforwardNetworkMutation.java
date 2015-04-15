package com.sandbox.evolve;

import java.util.List;
import java.util.Random;

import org.uncommons.watchmaker.framework.EvolutionaryOperator;

import com.sandbox.neural.AbstractNode;
import com.sandbox.neural.FeedforwardNetwork;
import com.sandbox.neural.NeuronNode;
import com.sandbox.neural.NodeLayer;

public class FeedforwardNetworkMutation implements EvolutionaryOperator<FeedforwardNetwork>
{
	@Override
	public List<FeedforwardNetwork> apply(List<FeedforwardNetwork> selectedCandidates, Random rng)
	{
		FeedforwardNetwork n = selectedCandidates.get(0);
		for (NodeLayer l : n.getLayers())
		{
			for (AbstractNode abn : l.getNodes())
			{
				if (abn instanceof NeuronNode)
				{
					for (int i = 0; i < ((NeuronNode) abn).getWeights().size(); i++)
					{
						((NeuronNode) abn).getWeights().set(i,
								((NeuronNode) abn).getWeights().get(i) * (2 * (rng.nextDouble() - 0.5) * 1.25) + (2 * (rng.nextDouble() - 0.5)));
					}
				}
			}
		}
		return null;
	}
}
