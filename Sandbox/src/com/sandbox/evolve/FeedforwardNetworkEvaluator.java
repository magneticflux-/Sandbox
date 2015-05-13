package com.sandbox.evolve;

import java.awt.Rectangle;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Random;

import org.uncommons.watchmaker.framework.EvolutionObserver;
import org.uncommons.watchmaker.framework.FitnessEvaluator;
import org.uncommons.watchmaker.framework.PopulationData;

import com.esotericsoftware.kryo.io.Input;
import com.sandbox.neural.FeedforwardNetwork;

public class FeedforwardNetworkEvaluator implements FitnessEvaluator<FeedforwardNetwork>, EvolutionObserver<FeedforwardNetwork>
{
	private FeedforwardNetwork	previousBest;

	public FeedforwardNetworkEvaluator(final String layout)
	{
		this.previousBest = new FeedforwardNetwork(layout);

		// final Kryo kryo = new Kryo();

		Input input = null;
		try
		{
			input = new Input(new FileInputStream("codex/AI Meta Level 2/generation_4928.pop"));
		}
		catch (FileNotFoundException e2)
		{
			e2.printStackTrace();
		}
		// @SuppressWarnings("unchecked")
		// PopulationData<FeedforwardNetwork> oldPop = (PopulationData<FeedforwardNetwork>) kryo.readClassAndObject(input);
		input.close();
		// this.previousBest = oldPop.getBestCandidate();
	}

	@Override
	public double getFitness(final FeedforwardNetwork candidate, final List<? extends FeedforwardNetwork> population)
	{

		final Arena a = new Arena(new Rectangle(0, 0, 600, 600), 1000);
		final Random r = new Random();

		a.addFighter(a.new Fighter(candidate, r.nextDouble() * a.getBounds().getWidth(), r.nextDouble() * a.getBounds().getHeight(), r.nextDouble() * Math.PI
				* 2, true, a));
		a.addFighter(a.new Fighter(this.previousBest, r.nextDouble() * a.getBounds().getWidth(), r.nextDouble() * a.getBounds().getHeight(), r.nextDouble()
				* Math.PI * 2, true, a));

		while (a.isYoung())
			a.updatePhysics();

		return a.getFighters().get(0).getScore().doubleValue();
	}

	@Override
	public boolean isNatural()
	{
		return true;
	}

	@Override
	public void populationUpdate(final PopulationData<? extends FeedforwardNetwork> data)
	{
		this.previousBest = data.getBestCandidate().getDeepCopy();
	}

}
