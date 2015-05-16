package com.sandbox.evolve;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Random;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.apache.commons.math3.util.FastMath;
import org.jgraph.JGraph;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.uncommons.watchmaker.framework.EvolutionObserver;
import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.interactive.Renderer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.sandbox.neural.AbstractNode;
import com.sandbox.neural.FeedforwardNetwork;

public class FeedforwardNetworkRenderer implements Renderer<FeedforwardNetwork, JComponent>, EvolutionObserver<FeedforwardNetwork>
{
	// Must be added as a Renderer<FeedforwardNetwork, JComponent> AND as an EvolutionObserver<FeedforwardNetwork>. It receives the best candidate from the
	// observer, and renders it fighting with the requested FeedforwardNetwork.
	private FeedforwardNetwork	bestNetwork;

	public FeedforwardNetworkRenderer(final String layout)
	{
		this.bestNetwork = new FeedforwardNetwork(layout);

		if (FeedforwardNetworkEvolve.USE_FILE_FOR_OPPONENT)
		{
			final Kryo kryo = new Kryo();
			Input input = null;
			try
			{
				input = new Input(new FileInputStream("codex/AI Meta Level 0/generation_14321.pop"));
			}
			catch (final FileNotFoundException e2)
			{
				e2.printStackTrace();
			}
			@SuppressWarnings("unchecked")
			final PopulationData<FeedforwardNetwork> oldPop = (PopulationData<FeedforwardNetwork>) kryo.readClassAndObject(input);
			input.close();
			this.bestNetwork = oldPop.getBestCandidate();
		}
	}

	@Override
	public void populationUpdate(final PopulationData<? extends FeedforwardNetwork> data)
	{
		if (!FeedforwardNetworkEvolve.USE_FILE_FOR_OPPONENT) this.bestNetwork = data.getBestCandidate().getDeepCopy();
	}

	@Override
	public JPanel render(final FeedforwardNetwork entity)
	{
		final FeedforwardNetwork input = entity;
		if (FeedforwardNetworkEvolve.FIGHT_SELF && !FeedforwardNetworkEvolve.USE_FILE_FOR_OPPONENT) this.bestNetwork = input;

		JPanel panel = new JPanel();
		JComponent comp1 = new FeedforwardNetworkRendererComponent(input, this.bestNetwork);
		JComponent comp2 = new JGraph(new JGraphModelAdapter<AbstractNode, DefaultWeightedEdge>(input.getGraphView()));

		comp1.setPreferredSize(new Dimension(800, 600));
		comp2.setPreferredSize(new Dimension(800, 800));

		panel.add(comp1, BorderLayout.LINE_START);
		panel.add(comp2, BorderLayout.LINE_END);

		return panel;

	}
}

class FeedforwardNetworkRendererComponent extends JPanel
{
	private static final long	serialVersionUID	= 1L;
	private static final double	updateSpeed			= 50 / 3d;
	private final Arena			arena;
	private final Arena.Fighter	fighter1;
	private final Arena.Fighter	fighter2;

	public FeedforwardNetworkRendererComponent(final FeedforwardNetwork competitor, final FeedforwardNetwork champ)
	{
		super();
		final Random r = new Random();
		this.arena = new Arena(new Rectangle(0, 0, 600, 600), -1);
		this.fighter1 = this.arena.new Fighter(competitor.getDeepCopy(), r.nextDouble() * this.arena.getBounds().getWidth(), r.nextDouble()
				* this.arena.getBounds().getHeight(), r.nextDouble() * FastMath.PI * 2, true, this.arena);
		this.fighter2 = this.arena.new Fighter(champ.getDeepCopy(), r.nextDouble() * this.arena.getBounds().getWidth(), r.nextDouble()
				* this.arena.getBounds().getHeight(), r.nextDouble() * FastMath.PI * 2, true, this.arena);

		this.arena.addFighter(this.fighter1);
		this.arena.addFighter(this.fighter2);
	}

	@Override
	public void paintComponent(final Graphics g)
	{
		final long startTime = System.nanoTime();

		super.paintComponent(g);

		this.arena.updatePhysics();
		this.arena.paint(g);

		try
		{
			if (FeedforwardNetworkRendererComponent.updateSpeed - (System.nanoTime() - startTime) / 1000000 > 0)
				Thread.sleep((long) (FeedforwardNetworkRendererComponent.updateSpeed - (System.nanoTime() - startTime) / 1000000d));
			else
			{
			}
		}
		catch (final InterruptedException e)
		{
			e.printStackTrace();
		}
		while (FeedforwardNetworkRendererComponent.updateSpeed - (System.nanoTime() - startTime) / 1000000 > 0)
		{
		}
		this.repaint();
	}
}
