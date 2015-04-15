package com.sandbox.evolve;

import java.util.Random;

import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory;

import com.sandbox.neural.AbstractNode;
import com.sandbox.neural.FeedforwardNetwork;
import com.sandbox.neural.NeuronNode;
import com.sandbox.neural.NodeLayer;

public class NetworkCandidateFactory extends AbstractCandidateFactory<FeedforwardNetwork>
{
	private final String	networkLayout;
	private final double	weightRange;

	public NetworkCandidateFactory(final String networkLayout, final double weightRange)
	{
		super();
		this.networkLayout = networkLayout;
		this.weightRange = weightRange;
	}

	@Override
	public FeedforwardNetwork generateRandomCandidate(final Random rng)
	{
		final FeedforwardNetwork n = new FeedforwardNetwork(this.networkLayout);
		for (final NodeLayer l : n.getLayers())
			for (final AbstractNode abn : l.getNodes())
				if (abn instanceof NeuronNode) for (int i = 0; i < ((NeuronNode) abn).getWeights().size(); i++)
					((NeuronNode) abn).getWeights().set(i, 2 * (rng.nextDouble() - 0.5) * this.weightRange);
		return n;
	}
}
