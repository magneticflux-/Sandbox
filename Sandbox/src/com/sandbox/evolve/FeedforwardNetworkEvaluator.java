package com.sandbox.evolve;

import java.awt.Rectangle;
import java.util.List;

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

		a.addFighter(a.new Fighter(candidate, a.r.nextDouble() * a.getBounds().getWidth(), a.r.nextDouble() * a.getBounds().getHeight(), a.r.nextDouble()
				* Math.PI * 2, a));
		a.addFighter(a.new Fighter(this.previousBest, a.r.nextDouble() * a.getBounds().getWidth(), a.r.nextDouble() * a.getBounds().getHeight(), a.r
				.nextDouble() * Math.PI * 2, a));

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
