package com.sandbox.evolve;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.uncommons.maths.number.NumberGenerator;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.operators.AbstractCrossover;
import org.uncommons.watchmaker.framework.operators.ListCrossover;

import com.sandbox.neural.FeedforwardNetwork;
import com.sandbox.neural.NeuronNode;

public class FeedforwardNetworkCrossover extends AbstractCrossover<FeedforwardNetwork>
{
	ListCrossover<Double>	weightCrossover;

	public FeedforwardNetworkCrossover()
	{
		this(1);
	}

	protected FeedforwardNetworkCrossover(final int crossoverPoints)
	{
		super(crossoverPoints);
		this.weightCrossover = new ListCrossover<Double>(crossoverPoints);
	}

	public FeedforwardNetworkCrossover(final int crossoverPoints, final Probability crossoverProbability)
	{
		super(crossoverPoints, crossoverProbability);
		this.weightCrossover = new ListCrossover<Double>(crossoverPoints, crossoverProbability);
	}

	public FeedforwardNetworkCrossover(final NumberGenerator<Integer> crossoverPointsVariable)
	{
		super(crossoverPointsVariable);
		this.weightCrossover = new ListCrossover<Double>(crossoverPointsVariable);
	}

	public FeedforwardNetworkCrossover(final NumberGenerator<Integer> crossoverPointsVariable, final NumberGenerator<Probability> crossoverProbabilityVariable)
	{
		super(crossoverPointsVariable, crossoverProbabilityVariable);
		this.weightCrossover = new ListCrossover<Double>(crossoverPointsVariable, crossoverProbabilityVariable);
	}

	@Override
	protected List<FeedforwardNetwork> mate(final FeedforwardNetwork parent1, final FeedforwardNetwork parent2, final int numberOfCrossoverPoints,
			final Random rng)
			{
		if (!parent1.getLayout().equals(parent2.getLayout())) throw new IllegalArgumentException("Cannot perform crossover with incompatible parents.");
		final FeedforwardNetwork offspring1 = new FeedforwardNetwork(parent1.getLayout());
		final FeedforwardNetwork offspring2 = new FeedforwardNetwork(parent1.getLayout());

		for (int i = 0; i < numberOfCrossoverPoints; i++)
			for (int l = 0; l < parent1.getLayers().size(); l++)
				for (int abn = 0; abn < parent1.getLayers().get(l).getNodes().size(); abn++)
					if (parent1.getLayers().get(l).getNodes().get(abn) instanceof NeuronNode)
					{
						final List<List<Double>> weights = new ArrayList<List<Double>>();
						weights.add(((NeuronNode) parent1.getLayers().get(l).getNodes().get(abn)).getWeights());
						weights.add(((NeuronNode) parent2.getLayers().get(l).getNodes().get(abn)).getWeights());

						final List<List<Double>> offspringWeights = this.weightCrossover.apply(weights, rng);

						((NeuronNode) offspring1.getLayers().get(l).getNodes().get(abn)).setWeights(offspringWeights.get(0));
						((NeuronNode) offspring2.getLayers().get(l).getNodes().get(abn)).setWeights(offspringWeights.get(1));
					}

		final List<FeedforwardNetwork> offspring = new ArrayList<FeedforwardNetwork>(2);
		offspring.add(offspring1);
		offspring.add(offspring2);

		return offspring;
			}
}
