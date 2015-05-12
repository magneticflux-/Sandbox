package com.sandbox.evolve;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Random;

import javax.swing.JComponent;

import org.uncommons.watchmaker.framework.EvolutionObserver;
import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.interactive.Renderer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.sandbox.neural.FeedforwardNetwork;

public class FeedforwardNetworkRenderer implements Renderer<FeedforwardNetwork, JComponent>, EvolutionObserver<FeedforwardNetwork>
{
	// Must be added as a Renderer<FeedforwardNetwork, JComponent> AND as an EvolutionObserver<FeedforwardNetwork>. It receives the best candidate from the
	// observer, and renders it fighting with the requested FeedforwardNetwork.
	private FeedforwardNetwork	bestNetwork;

	public FeedforwardNetworkRenderer(final String layout)
	{
		this.bestNetwork = new FeedforwardNetwork(layout);

		final Kryo kryo = new Kryo();

		Input input = null;
		try
		{
			input = new Input(new FileInputStream("codex/Basic AI/generation_13316.pop"));
		}
		catch (FileNotFoundException e2)
		{
			e2.printStackTrace();
		}
		@SuppressWarnings("unchecked")
		PopulationData<FeedforwardNetwork> oldPop = (PopulationData<FeedforwardNetwork>) kryo.readClassAndObject(input);
		input.close();
		this.bestNetwork = oldPop.getBestCandidate();
	}

	@Override
	public void populationUpdate(final PopulationData<? extends FeedforwardNetwork> data)
	{
		// this.bestNetwork = data.getBestCandidate().getDeepCopy();
	}

	@Override
	public FeedforwardNetworkRendererComponent render(final FeedforwardNetwork entity)
	{
		final FeedforwardNetwork input = entity;
		return new FeedforwardNetworkRendererComponent(input, this.bestNetwork);
	}
}

class FeedforwardNetworkRendererComponent extends JComponent
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
				* this.arena.getBounds().getHeight(), r.nextDouble() * Math.PI * 2, true, this.arena);
		this.fighter2 = this.arena.new Fighter(champ.getDeepCopy(), r.nextDouble() * this.arena.getBounds().getWidth(), r.nextDouble()
				* this.arena.getBounds().getHeight(), r.nextDouble() * Math.PI * 2, true, this.arena);
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
