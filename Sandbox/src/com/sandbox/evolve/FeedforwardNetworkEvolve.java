package com.sandbox.evolve;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

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

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.sandbox.neural.FeedforwardNetwork;

public class FeedforwardNetworkEvolve
{
	public static boolean		USE_FILE_FOR_OPPONENT	= false;
	public static boolean		FIGHT_SELF				= true;
	public static boolean		LOAD_PREVIOUS			= false;
	public static final int		INPUT_NUMBER			= 0;
	public static final String	OUTPUT_FOLDER			= "codex/AI Meta Level 0/";
	public static final String	INPUT_FOLDER			= "codex/AI Meta Level 0/";
	public static final double	MUTATION_RANGE			= 1;
	public static final double	MUTATION_PROBABILITY	= 0.05;
	public static final int		CROSSOVER_POINTS		= 2;

	public static void main(final String[] args)
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}

		CandidateFactory<FeedforwardNetwork> factory = null;

		final Kryo kryo = new Kryo();
		if (LOAD_PREVIOUS)
		{

			Input input = null;
			try
			{
				input = new Input(new FileInputStream(INPUT_FOLDER + "generation_" + INPUT_NUMBER + ".pop"));
			}
			catch (final FileNotFoundException e2)
			{
				e2.printStackTrace();
			}
			@SuppressWarnings("unchecked")
			final PopulationData<FeedforwardNetwork> oldPop = (PopulationData<FeedforwardNetwork>) kryo.readClassAndObject(input);
			input.close();
			factory = new NetworkCandidateFactory(Arrays.asList(oldPop.getBestCandidate()));
		}

		final String layout = "3 6 6";

		if (!LOAD_PREVIOUS) factory = new NetworkCandidateFactory(layout, 8);

		final List<EvolutionaryOperator<FeedforwardNetwork>> operators = new LinkedList<EvolutionaryOperator<FeedforwardNetwork>>();
		operators.add(new FeedforwardNetworkCrossover(CROSSOVER_POINTS));
		operators.add(new FeedforwardNetworkMutation(new Probability(MUTATION_PROBABILITY)));

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

				Output output = null;
				try
				{
					output = new Output(new FileOutputStream(OUTPUT_FOLDER + "generation_" + (data.getGenerationNumber() + INPUT_NUMBER) + ".pop"));
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
		final EvolutionMonitor<FeedforwardNetwork> monitor = new EvolutionMonitor<FeedforwardNetwork>(new FeedforwardNetworkRenderer(layout), false);

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
			monitor.getGUIComponent().validate();

			for (int i = 0; i < ((JTabbedPane) monitor.getGUIComponent().getComponents()[0]).getComponentCount(); i++)
				((JTabbedPane) monitor.getGUIComponent().getComponents()[0]).getComponentAt(0);
		}

		monitor.showInFrame("Evolution", true);
		engine.addEvolutionObserver(monitor);

		new File(OUTPUT_FOLDER).mkdirs();
		new File(INPUT_FOLDER).mkdirs();

		final FeedforwardNetwork result = engine.evolve(2500, 250, abort);
		System.out.println("Fittest individual: " + result);
	}
}
