package com.sandbox.evolve;

import java.awt.Graphics;

import javax.swing.JComponent;

import org.uncommons.watchmaker.framework.interactive.Renderer;

import com.sandbox.neural.FeedforwardNetwork;

public class FeedforwardNetworkRenderer implements Renderer<FeedforwardNetwork, JComponent>
{
	@Override
	public JComponent render(FeedforwardNetwork entity)
	{
		final FeedforwardNetwork input = entity;
		JComponent jc = new JComponent()
		{
			private static final long			serialVersionUID	= 1L;
			private final FeedforwardNetwork	fighter1			= input.getDeepCopy();

			public void paintComponent(Graphics g)
			{
			}
		};
		return null;
	}
}
