package com.sandbox.evolve;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.CandidateFactory;
import org.uncommons.watchmaker.framework.EvolutionEngine;
import org.uncommons.watchmaker.framework.EvolutionObserver;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.FitnessEvaluator;
import org.uncommons.watchmaker.framework.GenerationalEvolutionEngine;
import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.SelectionStrategy;
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline;
import org.uncommons.watchmaker.framework.selection.TournamentSelection;
import org.uncommons.watchmaker.framework.termination.Stagnation;
import org.uncommons.watchmaker.swing.evolutionmonitor.EvolutionMonitor;

import com.sandbox.neural.FeedforwardNetwork;

public class FeedforwardNetworkEvolve
{
	public static void main(String[] args)
	{
		String layout = "3 6 4";
		final CandidateFactory<FeedforwardNetwork> factory = new NetworkCandidateFactory(layout, 64);

		final List<EvolutionaryOperator<FeedforwardNetwork>> operators = new LinkedList<EvolutionaryOperator<FeedforwardNetwork>>();
		operators.add(new FeedforwardNetworkCrossover(2));
		operators.add(new FeedforwardNetworkMutation(new Probability(0.03)));

		final EvolutionaryOperator<FeedforwardNetwork> pipeline = new EvolutionPipeline<FeedforwardNetwork>(operators);

		final FeedforwardNetworkEvaluator eval = new FeedforwardNetworkEvaluator(layout);
		final FitnessEvaluator<FeedforwardNetwork> fitnessEvaluator = eval;

		final SelectionStrategy<Object> selection = new TournamentSelection(new Probability(1));

		final Random rng = new MersenneTwisterRNG();

		final EvolutionEngine<FeedforwardNetwork> engine = new GenerationalEvolutionEngine<FeedforwardNetwork>(factory, pipeline, fitnessEvaluator, selection,
				rng);
		engine.addEvolutionObserver(eval);
		engine.addEvolutionObserver(new EvolutionObserver<FeedforwardNetwork>()
		{
			@Override
			public void populationUpdate(final PopulationData<? extends FeedforwardNetwork> data)
			{
				System.out.printf("Generation %d: %s\n", data.getGenerationNumber(), data.getBestCandidate());
			}
		});

		EvolutionMonitor<FeedforwardNetwork> monitor = new EvolutionMonitor<FeedforwardNetwork>(new FeedforwardNetworkRenderer(layout), false);
		monitor.showInFrame("Evolution", true);
		engine.addEvolutionObserver(monitor);
		final FeedforwardNetwork result = engine.evolve(1000, 2, new Stagnation(1000, true));
		System.out.println("Fittest individual: " + result);
	}
}
