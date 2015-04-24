package com.sandbox.evolve;

import java.awt.Rectangle;
import java.util.List;

import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.watchmaker.framework.EvolutionObserver;
import org.uncommons.watchmaker.framework.FitnessEvaluator;
import org.uncommons.watchmaker.framework.PopulationData;

import com.sandbox.neural.FeedforwardNetwork;

public class FeedforwardNetworkEvaluator implements FitnessEvaluator<FeedforwardNetwork>, EvolutionObserver<FeedforwardNetwork>
{
	private FeedforwardNetwork	previousBest;

	public FeedforwardNetworkEvaluator(String layout)
	{
		this.previousBest = new FeedforwardNetwork(layout);
	}

	@Override
	public double getFitness(FeedforwardNetwork candidate, List<? extends FeedforwardNetwork> population)
	{
		// TODO Auto-generated method stub
		Arena a = new Arena(new Rectangle(0, 0, 600, 600), 1000);

		if ((new MersenneTwisterRNG()).nextBoolean())
		{
			a.addFighter(a.new Fighter(candidate, 200, 300, Math.PI, a));
			a.addFighter(a.new Fighter(this.previousBest, 400, 300, 0, a));
		}
		else
		{
			a.addFighter(a.new Fighter(candidate, 400, 300, 0, a));
			a.addFighter(a.new Fighter(this.previousBest, 200, 300, Math.PI, a));
		}

		while (a.isYoung())
			a.updatePhysics();

		return a.getFighters().get(0).getScore();
	}

	@Override
	public boolean isNatural()
	{
		return true;
	}

	@Override
	public void populationUpdate(PopulationData<? extends FeedforwardNetwork> data)
	{
		previousBest = data.getBestCandidate().getDeepCopy();
	}

}
