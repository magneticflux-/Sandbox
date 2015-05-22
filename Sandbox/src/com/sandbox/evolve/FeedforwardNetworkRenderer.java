package com.sandbox.evolve;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.apache.commons.math3.util.FastMath;
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
	private BufferedImage		image;

	public FeedforwardNetworkRenderer(final String layout)
	{
		this.image = null;
		this.bestNetwork = new FeedforwardNetwork(layout);

		if (FeedforwardNetworkEvolve.USE_FILE_FOR_OPPONENT)
		{
			final Kryo kryo = new Kryo();
			Input input = null;
			try
			{
				input = new Input(new FileInputStream("codex/AI Meta Level -1/generation_10571.pop"));
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
		// JComponent comp2 = new JGraph(new JGraphModelAdapter<AbstractNode, DefaultWeightedEdge>(input.getGraphView()));
		if (image == null)
		{
			this.image = input.getGraphImage();
		}

		JComponent comp2 = new JPanel()
		{
			private static final long	serialVersionUID	= 1L;

			public void paintComponent(Graphics g)
			{
				super.paintComponent(g);
				Graphics2D g2d = ((Graphics2D) g);
				g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
				g2d.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), Color.PINK, this);
			}
		};
		comp1.setPreferredSize(new Dimension(1000, 400));

		JButton button = new JButton("Recalculate Visualization of Neural Network");
		button.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				image = input.getGraphImage();
			}
		});
		Dimension d = new Dimension(button.getFontMetrics(button.getFont()).stringWidth(button.getText()) + 5, button.getFontMetrics(button.getFont())
				.getHeight() + 5);
		button.setPreferredSize(d);
		button.setMaximumSize(d);
		button.setMinimumSize(d);

		panel.setLayout(new BorderLayout());
		panel.add(comp1, BorderLayout.PAGE_START);
		panel.add(comp2, BorderLayout.CENTER);
		panel.add(button, BorderLayout.PAGE_END);
		panel.setBackground(Color.WHITE);

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
		this.arena = new Arena(new Rectangle(0, 0, 1000, 400), -1);
		this.fighter1 = this.arena.new Fighter(competitor.getDeepCopy(), r.nextDouble() * this.arena.getBounds().getWidth(), r.nextDouble()
				* this.arena.getBounds().getHeight(), r.nextDouble() * FastMath.PI * 2, true, false, this.arena);
		this.fighter2 = this.arena.new Fighter(champ.getDeepCopy(), r.nextDouble() * this.arena.getBounds().getWidth(), r.nextDouble()
				* this.arena.getBounds().getHeight(), r.nextDouble() * FastMath.PI * 2, true, true, this.arena);

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
