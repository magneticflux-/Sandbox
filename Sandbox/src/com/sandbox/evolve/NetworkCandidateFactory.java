package com.sandbox.evolve;

import com.sandbox.neural.FeedforwardNetwork;

import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory;

import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

public class NetworkCandidateFactory extends AbstractCandidateFactory<FeedforwardNetwork> {
	private final String networkLayout;
	private final double weightRange;
	private final Collection<FeedforwardNetwork> candidates;
	Iterator<FeedforwardNetwork> iter;

	public NetworkCandidateFactory(final Collection<FeedforwardNetwork> seedCandidates) {
		this.candidates = seedCandidates;
		this.iter = this.candidates.iterator();
		this.networkLayout = null;
		this.weightRange = 0;
	}

	public NetworkCandidateFactory(final String networkLayout, final double weightRange) {
		super();
		this.networkLayout = networkLayout;
		this.weightRange = weightRange;
		this.candidates = null;
		this.iter = null;
	}

	@Override
	public FeedforwardNetwork generateRandomCandidate(final Random rng) {
		if (this.candidates == null && this.networkLayout != null) {
			final FeedforwardNetwork n = new FeedforwardNetwork(this.networkLayout);
			n.randomizeWeights(rng, this.weightRange);
			return n;
		} else if (this.candidates != null && this.networkLayout == null) {
			if (this.iter.hasNext())
				return this.iter.next();
			else {
				this.iter = this.candidates.iterator();
				return this.iter.next();
			}
		} else
			throw new IllegalStateException();
	}
}
