package com.sandbox.evolve;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JComponent;
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
import org.uncommons.watchmaker.framework.interactive.Renderer;
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline;
import org.uncommons.watchmaker.framework.selection.TournamentSelection;
import org.uncommons.watchmaker.framework.termination.UserAbort;
import org.uncommons.watchmaker.swing.evolutionmonitor.EvolutionMonitor;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import com.sandbox.neural.FeedforwardNetwork;

public class MarioBrosEvolve
{
	public static void main(String[] args)
	{
		final String layout = "100 20 20 8";
		CandidateFactory<FeedforwardNetwork> candidateFactory = new NetworkCandidateFactory(layout, 10);
		List<EvolutionaryOperator<FeedforwardNetwork>> operators = new LinkedList<EvolutionaryOperator<FeedforwardNetwork>>();
		operators.add(new FeedforwardNetworkCrossover(2));
		operators.add(new FeedforwardNetworkMutation(new Probability(0.05)));
		EvolutionaryOperator<FeedforwardNetwork> evolutionScheme = new EvolutionPipeline<FeedforwardNetwork>(operators);
		FitnessEvaluator<FeedforwardNetwork> fitnessEvaluator = new NESFitnessEvaluator();
		SelectionStrategy<Object> selectionStrategy = new TournamentSelection(new Probability(1));
		Random rng = new MersenneTwisterRNG();
		final Kryo kryo = new Kryo();
		EvolutionEngine<FeedforwardNetwork> engine = new GenerationalEvolutionEngine<FeedforwardNetwork>(candidateFactory, evolutionScheme, fitnessEvaluator,
				selectionStrategy, rng);
		engine.addEvolutionObserver(new EvolutionObserver<FeedforwardNetwork>()
		{
			@Override
			public void populationUpdate(PopulationData<? extends FeedforwardNetwork> data)
			{
				System.out.printf("Generation %d: %s\n", data.getGenerationNumber(), data.getBestCandidate());

				Output output = null;
				try
				{
					output = new Output(new FileOutputStream("codex/Mario/" + "generation_" + data.getGenerationNumber() + ".pop"));
				}
				catch (final FileNotFoundException e1)
				{
					e1.printStackTrace();
				}
				kryo.writeClassAndObject(output, data);
				output.close();
			}
		});
		final UserAbort abort = new UserAbort();
		final EvolutionMonitor<FeedforwardNetwork> monitor = new EvolutionMonitor<FeedforwardNetwork>(new Renderer<FeedforwardNetwork, JComponent>()
		{
			@Override
			public JComponent render(FeedforwardNetwork entity)
			{
				JPanel panel = new JPanel();
				return panel;
			}
		}, false);
		synchronized (monitor.getGUIComponent().getTreeLock())
		{
			((JTabbedPane) monitor.getGUIComponent().getComponents()[0]).add(new JPanel()
			{
				private static final long	serialVersionUID	= 1L;

				{
					this.setName("Abort Button");
					this.setLayout(new BorderLayout());
					this.add(new JButton("ABORT")
					{
						private static final long	serialVersionUID	= 1L;

						{
							this.setBackground(Color.RED);
							this.setMaximumSize(new Dimension(100, 50));
							this.setPreferredSize(new Dimension(100, 50));

							this.addActionListener(new ActionListener()
							{
								@Override
								public void actionPerformed(final ActionEvent e)
								{
									abort.abort();
									System.out.println("*** ABORT SEQUENCE ACTIVATED ***");
								}
							});
						}
					}, BorderLayout.PAGE_START);
				}
			});
		}
		monitor.showInFrame("Evolution", true);
		engine.addEvolutionObserver(monitor);
		final FeedforwardNetwork result = engine.evolve(100, 10, abort);
		System.out.println("Fittest individual: " + result);
	}
}
