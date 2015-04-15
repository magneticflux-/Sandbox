package com.sandbox.evolve;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.uncommons.maths.number.ConstantGenerator;
import org.uncommons.maths.number.NumberGenerator;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;

import com.sandbox.neural.AbstractNode;
import com.sandbox.neural.FeedforwardNetwork;
import com.sandbox.neural.NeuronNode;
import com.sandbox.neural.NodeLayer;

public class FeedforwardNetworkMutation implements EvolutionaryOperator<FeedforwardNetwork>
{
	private final NumberGenerator<Probability>	mutationProbability;

	public FeedforwardNetworkMutation(NumberGenerator<Probability> mutationProbability)
	{
		this.mutationProbability = mutationProbability;
	}

	public FeedforwardNetworkMutation(Probability mutationProbability)
	{
		this(new ConstantGenerator<Probability>(mutationProbability));
	}

	@Override
	public List<FeedforwardNetwork> apply(final List<FeedforwardNetwork> selectedCandidates, final Random rng)
	{
		List<FeedforwardNetwork> mutatedPopulation = new ArrayList<FeedforwardNetwork>(selectedCandidates.size());
		for (FeedforwardNetwork n : selectedCandidates)
		{
			for (final NodeLayer l : n.getLayers())
				for (final AbstractNode abn : l.getNodes())
					if (abn instanceof NeuronNode)
						for (int i = 0; i < ((NeuronNode) abn).getWeights().size(); i++)
							if (mutationProbability.nextValue().nextEvent(rng))
							{
								((NeuronNode) abn).getWeights().set(i,
										((NeuronNode) abn).getWeights().get(i) * (2 * (rng.nextDouble() - 0.5) * 1.25) + 2 * (rng.nextDouble() - 0.5));
							}
			mutatedPopulation.add(n);
		}
		return mutatedPopulation;
	}
}
