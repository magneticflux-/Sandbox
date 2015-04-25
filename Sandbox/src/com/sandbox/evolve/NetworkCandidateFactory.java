package com.sandbox.evolve;

import java.util.Random;

import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory;

import com.sandbox.neural.FeedforwardNetwork;

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
		n.randomizeWeights(rng, this.weightRange);
		return n;
	}
}
