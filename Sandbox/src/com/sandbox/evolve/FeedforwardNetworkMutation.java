package com.sandbox.evolve;

import com.sandbox.neural.AbstractNode;
import com.sandbox.neural.FeedforwardNetwork;
import com.sandbox.neural.NeuronNode;
import com.sandbox.neural.NodeLayer;

import org.uncommons.maths.number.ConstantGenerator;
import org.uncommons.maths.number.NumberGenerator;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FeedforwardNetworkMutation implements EvolutionaryOperator<FeedforwardNetwork> {
	private final NumberGenerator<Probability> mutationProbability;

	public FeedforwardNetworkMutation(final Probability mutationProbability) {
		this(new ConstantGenerator<Probability>(mutationProbability));
	}

	public FeedforwardNetworkMutation(final NumberGenerator<Probability> mutationProbability) {
		this.mutationProbability = mutationProbability;
	}

	@Override
	public List<FeedforwardNetwork> apply(final List<FeedforwardNetwork> selectedCandidates, final Random rng) {
		final List<FeedforwardNetwork> mutatedPopulation = new ArrayList<FeedforwardNetwork>(selectedCandidates.size());
		for (final FeedforwardNetwork selectedNetwork : selectedCandidates) {
			final FeedforwardNetwork n = selectedNetwork.getDeepCopy();
			for (final NodeLayer l : n.getLayers())
				for (final AbstractNode abn : l.getNodes())
					if (abn instanceof NeuronNode)
						for (int i = 0; i < ((NeuronNode) abn).getWeights().size(); i++)
							if (this.mutationProbability.nextValue().nextEvent(rng))
								((NeuronNode) abn).getWeights().set(i, this.mutate(((NeuronNode) abn).getWeights().get(i), rng));
			mutatedPopulation.add(n);
		}
		return mutatedPopulation;
	}

	private double mutate(final double value, final Random rng) {
		return value + (rng.nextDouble() - 0.5) * 2 * FeedforwardNetworkEvolve.MUTATION_RANGE;
	}
}
