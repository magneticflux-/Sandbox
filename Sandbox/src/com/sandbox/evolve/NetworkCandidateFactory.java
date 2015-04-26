package com.sandbox.evolve;

import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory;

import com.sandbox.neural.FeedforwardNetwork;

public class NetworkCandidateFactory extends AbstractCandidateFactory<FeedforwardNetwork>
{
	private final String					networkLayout;
	private final double					weightRange;
	private Collection<FeedforwardNetwork>	candidates;
	Iterator<FeedforwardNetwork>			iter;

	public NetworkCandidateFactory(final String networkLayout, final double weightRange)
	{
		super();
		this.networkLayout = networkLayout;
		this.weightRange = weightRange;
		candidates = null;
		iter = null;
	}

	public NetworkCandidateFactory(Collection<FeedforwardNetwork> seedCandidates)
	{
		this.candidates = seedCandidates;
		this.iter = candidates.iterator();
		this.networkLayout = null;
		this.weightRange = 0;
	}

	@Override
	public FeedforwardNetwork generateRandomCandidate(final Random rng)
	{
		if (candidates == null && networkLayout != null)
		{
			final FeedforwardNetwork n = new FeedforwardNetwork(this.networkLayout);
			n.randomizeWeights(rng, this.weightRange);
			return n;
		}
		else if (candidates != null && networkLayout == null)
		{
			if (this.iter.hasNext())
			{
				return iter.next();
			}
			else
			{
				this.iter = this.candidates.iterator();
				return this.iter.next();
			}
		}
		else
		{
			throw new IllegalStateException();
		}
	}
}
