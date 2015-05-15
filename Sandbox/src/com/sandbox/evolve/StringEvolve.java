package com.sandbox.evolve;

import java.awt.Color;
import java.awt.Graphics;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JComponent;

import org.apache.commons.math3.util.FastMath;
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
import org.uncommons.watchmaker.framework.factories.StringFactory;
import org.uncommons.watchmaker.framework.interactive.Renderer;
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline;
import org.uncommons.watchmaker.framework.operators.StringCrossover;
import org.uncommons.watchmaker.framework.operators.StringMutation;
import org.uncommons.watchmaker.framework.selection.RouletteWheelSelection;
import org.uncommons.watchmaker.framework.termination.TargetFitness;
import org.uncommons.watchmaker.swing.evolutionmonitor.EvolutionMonitor;

class StringEvaluator implements FitnessEvaluator<String>
{
	private final String	targetString;

	/**
	 * Assigns one "fitness point" for every character in the candidate String that matches the corresponding position in the target string.
	 */
	public StringEvaluator(final String target)
	{
		super();
		this.targetString = target;
	}

	@Override
	public double getFitness(final String candidate, final List<? extends String> population)
	{
		int matches = 0;
		for (int i = 0; i < candidate.length(); i++)
			if (candidate.charAt(i) == this.targetString.charAt(i)) matches++;
		return matches;
	}

	@Override
	public boolean isNatural()
	{
		return true;
	}
}

public class StringEvolve
{
	public static void main(final String[] args)
	{
		System.out.print("Input Target String: ");
		final Scanner s = new Scanner(System.in);
		final String target = s.nextLine();
		s.close();
		final char[] chars = new char[57];
		for (char c = 'A'; c <= 'Z'; c++)
			chars[c - 'A'] = c;
		for (char c = 'a'; c <= 'z'; c++)
			chars[c - 'a' + 26] = c;
		chars[52] = ' ';
		chars[53] = ',';
		chars[54] = '-';
		chars[55] = '.';
		chars[56] = ';';
		final CandidateFactory<String> factory = new StringFactory(chars, target.length());

		final List<EvolutionaryOperator<String>> operators = new LinkedList<EvolutionaryOperator<String>>();
		operators.add(new StringCrossover(2));
		operators.add(new StringMutation(chars, new Probability(0.03)));
		final EvolutionaryOperator<String> pipeline = new EvolutionPipeline<String>(operators);

		final FitnessEvaluator<String> fitnessEvaluator = new StringEvaluator(target);

		final SelectionStrategy<Object> selection = new RouletteWheelSelection();

		final Random rng = new MersenneTwisterRNG();

		final EvolutionEngine<String> engine = new GenerationalEvolutionEngine<String>(factory, pipeline, fitnessEvaluator, selection, rng);

		engine.addEvolutionObserver(new EvolutionObserver<String>()
				{
			@Override
			public void populationUpdate(final PopulationData<? extends String> data)
			{
				System.out.printf("Generation %d: %s\n", data.getGenerationNumber(), data.getBestCandidate());
			}
				});
		final EvolutionMonitor<String> monitor = new EvolutionMonitor<String>(new StringRenderer(), false);
		monitor.showInFrame("Evolution", true);
		engine.addEvolutionObserver(monitor);
		final String result = engine.evolve(10000, 500, new TargetFitness(target.length(), true));
		System.out.println("Fittest individual: " + result);
	}
}

class StringRenderer implements Renderer<String, JComponent>
{
	@Override
	public JComponent render(final String entity)
	{
		final String input = entity;
		final JComponent jc = new JComponent()
		{
			private static final long	serialVersionUID	= 1L;
			private final String		toRender			= input;
			private double				inc					= 0;

			@Override
			public void paintComponent(final Graphics g)
			{
				g.setColor(new Color((int) (128 + 127 * FastMath.sin(this.inc + 2 * FastMath.PI / 3)), (int) (128 + 127 * FastMath.sin(this.inc + 4 * FastMath.PI / 3)),
						(int) (128 + 127 * FastMath.sin(this.inc + 0 * FastMath.PI / 3))));
				g.fillRect(0, 0, this.getWidth(), this.getHeight());
				g.setColor(new Color((int) (128 + 127 * FastMath.sin(this.inc + 0)), (int) (128 + 127 * FastMath.sin(this.inc + 2 * FastMath.PI / 3)),
						(int) (128 + 127 * FastMath.sin(this.inc + 4 * FastMath.PI / 3))));
				g.drawString(this.toRender, 50, 50);
				this.inc += 0.001;
				this.repaint();
			}
		};
		return jc;
	}
}