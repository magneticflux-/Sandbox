package com.sandbox.evolve;

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
	public static boolean		USE_FILE_FOR_OPPONENT	= true;
	public static boolean		FIGHT_SELF				= false;
	public static boolean		LOAD_PREVIOUS			= false;
	public static final int		INPUT_NUMBER			= 0;
	public static final String	OUTPUT_FOLDER			= "codex/AI Meta Level 1/";
	public static final String	INPUT_FOLDER			= "codex/AI Meta Level 0/";
	public static final double	MUTATION_RANGE			= 0.00001;

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

		final String layout = "3 12 6";

		if (!LOAD_PREVIOUS) factory = new NetworkCandidateFactory(layout, 2);

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
					this.setLayout(new GridBagLayout());
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
					}, new GridBagConstraints(0, 0, 1, 1, .5, .5, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
				}
			});
			monitor.getGUIComponent().validate();
		}

		monitor.showInFrame("Evolution", true);
		engine.addEvolutionObserver(monitor);

		new File(OUTPUT_FOLDER).mkdirs();
		new File(INPUT_FOLDER).mkdirs();

		final FeedforwardNetwork result = engine.evolve(500, 25, abort);
		System.out.println("Fittest individual: " + result);
	}
}
