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

	@Override
	public FeedforwardNetworkRendererComponent render(FeedforwardNetwork entity)
	{
		final FeedforwardNetwork input = entity;
		return new FeedforwardNetworkRendererComponent(input, bestNetwork);
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

	public FeedforwardNetworkRendererComponent(FeedforwardNetwork competitor, FeedforwardNetwork champ)
	{
		super();
		arena = new Arena(new Rectangle(0, 0, 500, 500), -1);
		fighter1 = arena.new Fighter(competitor.getDeepCopy(), 50, 50, arena);
		fighter2 = arena.new Fighter(champ.getDeepCopy(), 50, 50, arena);
		arena.addFighter(fighter1);
		arena.addFighter(fighter2);
	}

	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
	}
}
