package com.sandbox.evolve;

import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JComponent;

import org.uncommons.watchmaker.framework.EvolutionObserver;
import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.interactive.Renderer;

import com.sandbox.neural.FeedforwardNetwork;

public class FeedforwardNetworkRenderer implements Renderer<FeedforwardNetwork, JComponent>, EvolutionObserver<FeedforwardNetwork>
{
	// Must be added as a Renderer<FeedforwardNetwork, JComponent> AND as an EvolutionObserver<FeedforwardNetwork>. It receives the best candidate from the
	// observer, and renders it fighting with the requested FeedforwardNetwork.
	private FeedforwardNetwork	bestNetwork;

	public FeedforwardNetworkRenderer(String layout)
	{
		this.bestNetwork = new FeedforwardNetwork(layout);
	}

	@Override
	public FeedforwardNetworkRendererComponent render(FeedforwardNetwork entity)
	{
		final FeedforwardNetwork input = entity;
		return new FeedforwardNetworkRendererComponent(input, input);// bestNetwork);
	}

	@Override
	public void populationUpdate(PopulationData<? extends FeedforwardNetwork> data)
	{
		this.bestNetwork = data.getBestCandidate().getDeepCopy();
	}
}

class FeedforwardNetworkRendererComponent extends JComponent
{
	private static final long	serialVersionUID	= 1L;
	private final Arena			arena;
	private final Arena.Fighter	fighter1;
	private final Arena.Fighter	fighter2;
	private static final double	updateSpeed			= 16.666666666;

	public FeedforwardNetworkRendererComponent(FeedforwardNetwork competitor, FeedforwardNetwork champ)
	{
		super();
		arena = new Arena(new Rectangle(0, 0, 600, 600), -1);
		fighter1 = arena.new Fighter(competitor.getDeepCopy(), 200, 300, Math.PI, arena);
		fighter2 = arena.new Fighter(champ.getDeepCopy(), 400, 300, 0, arena);
		arena.addFighter(fighter1);
		arena.addFighter(fighter2);
	}

	@Override
	public void paintComponent(Graphics g)
	{
		long startTime = System.nanoTime();

		super.paintComponent(g);
		arena.updatePhysics();
		arena.paint(g);

		try
		{
			if (updateSpeed - (System.nanoTime() - startTime) / 1000000 > 0)
				Thread.sleep((long) (updateSpeed - (System.nanoTime() - startTime) / 1000000d));
			else
			{
			}
		}
		catch (final InterruptedException e)
		{
			e.printStackTrace();
		}
		while (updateSpeed - (System.nanoTime() - startTime) / 1000000 > 0)
		{
		}
		this.repaint();
	}
}
