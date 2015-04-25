package com.sandbox.evolve;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

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
import org.uncommons.watchmaker.framework.termination.UserAbort;
import org.uncommons.watchmaker.swing.evolutionmonitor.EvolutionMonitor;

import com.sandbox.neural.FeedforwardNetwork;

public class FeedforwardNetworkEvolve
{
	public static void main(final String[] args)
	{
		final String layout = "6 6 4 4";
		final CandidateFactory<FeedforwardNetwork> factory = new NetworkCandidateFactory(layout, 4);

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
		final UserAbort abort = new UserAbort();
		final EvolutionMonitor<FeedforwardNetwork> monitor = new EvolutionMonitor<FeedforwardNetwork>(new FeedforwardNetworkRenderer(layout), false);

		synchronized (monitor.getGUIComponent().getTreeLock())
		{
			((JTabbedPane) monitor.getGUIComponent().getComponents()[0]).add(new JPanel()
			{
				private static final long	serialVersionUID	= 1L;

				{
					this.setName("Abort Button");
					this.setLayout(new GridBagLayout());
					this.add(new JButton()
					{
						private static final long	serialVersionUID	= 1L;

						{
							this.setName("ABORT");
							this.setMaximumSize(new Dimension(50, 50));
							this.setPreferredSize(new Dimension(50, 50));
							this.setBackground(Color.RED);

							this.addActionListener(new ActionListener()
							{
								@Override
								public void actionPerformed(final ActionEvent e)
								{
									abort.abort();
									System.out.println("*** ABORT SEQUENCE ACTIVATED ***\n");
								}
							});
						}
					}, new GridBagConstraints(0, 0, 1, 1, .5, .5, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
				}
			});
			monitor.getGUIComponent().validate();
		}

		monitor.showInFrame("Evolution", true);
		engine.addEvolutionObserver(monitor);

		final FeedforwardNetwork result = engine.evolve(1000, 2, abort);
		System.out.println("Fittest individual: " + result);
	}
}
