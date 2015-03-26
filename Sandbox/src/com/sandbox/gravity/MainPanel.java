package com.sandbox.gravity;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.JComponent;
import javax.swing.JFrame;

public class MainPanel extends JComponent
{
	public static final int		updateSpeed			= 2;
	public static final int		logSize				= 100;
	private static final long	serialVersionUID	= 1L;
	private static long			lastTime			= 0;

	public static double getFPS()
	{
		return 1 / (MainPanel.lastTime / 1000000000d);
	}

	public static void main(final String[] args)
	{
		final MainPanel component = new MainPanel();
		final JFrame frame = new JFrame("Gravity...");
		frame.setBounds(0, 0, 720, 720);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		component.setBackground(Color.WHITE);
		frame.setBackground(Color.WHITE);
		frame.getContentPane().add(component);
		frame.setVisible(true);

		final Thread r = new Thread()
		{

			@Override
			public void run()
			{
				while (true)
				{
					synchronized (component)
					{
						Mass mass = new Mass((int) (Math.random() * 720), (int) (Math.random() * 720), (int) (Math.random() * 10));
						mass.setxV((Math.random() - 0.5) * 100);
						mass.setyV((Math.random() - 0.5) * 100);
						component.masses.add(mass);
					}
					try
					{
						Thread.sleep(20);
					}
					catch (final Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		};
		r.start();
	}

	private final LinkedList<Long>	fpsLog	= new LinkedList<Long>();

	private final ArrayList<Mass>	masses	= new ArrayList<Mass>();

	public MainPanel()
	{
		for (int i = 0; i < 250; i++)
		{
			this.masses.add(new Mass((int) (Math.random() * 720), (int) (Math.random() * 720), (int) (Math.random() * 50)));
			this.masses.get(i).setxV((int) ((Math.random() - 0.5) * 10));
			this.masses.get(i).setyV((int) ((Math.random() - 0.5) * 10));
		}

		final boolean display = false;
		if (display)
		{
			this.masses.add(new Mass(400, 400, 2000));
			this.masses.add(new Mass(400, 200, 800));

			this.masses.get(0).setxV(2.777777777);
			this.masses.get(1).setxV(-7);
		}

	}

	@Override
	public void paintComponent(final Graphics g)
	{
		synchronized (this)
		{
			final long startTime = System.nanoTime();
			final Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

			g2d.translate(0, this.getHeight() / 2);
			g2d.scale(1, -1);
			g2d.translate(0, -this.getHeight() / 2);

			for (int i = 0; i < this.masses.size(); i++)
			{
				boolean wallCollide = true;
				if (wallCollide)
				{
					if (this.masses.get(i).getxCenter() < 0)
					{
						this.masses.get(i).setxV(-this.masses.get(i).getxV() / 2);
						this.masses.get(i).setxCenter(this.masses.get(i).getxCenter() + 5);
					}
					else if (this.masses.get(i).getxCenter() > this.getWidth())
					{
						this.masses.get(i).setxV(-this.masses.get(i).getxV() / 2);
						this.masses.get(i).setxCenter(this.masses.get(i).getxCenter() - 5);
					}
					else if (this.masses.get(i).getyCenter() < 0)
					{
						this.masses.get(i).setyV(-this.masses.get(i).getyV() / 2);
						this.masses.get(i).setyCenter(this.masses.get(i).getyCenter() + 5);
					}
					else if (this.masses.get(i).getyCenter() > this.getHeight())
					{
						this.masses.get(i).setyV(-this.masses.get(i).getyV() / 2);
						this.masses.get(i).setyCenter(this.masses.get(i).getyCenter() - 5);
					}
				}
				this.masses.get(i).collideAll(this.masses);
			}

			for (final Mass m : this.masses)
				m.attractAll(this.masses);

			for (final Mass m : this.masses)
			{
				m.stepTime();
				m.paint(g);
			}

			g2d.translate(0, this.getHeight() / 2);
			g2d.scale(1, -1);
			g2d.translate(0, -this.getHeight() / 2);

			g2d.setColor(Color.BLACK);
			g2d.drawString(String.format("FPS: %08.2f Timestep: %06.5f", 1 / (MainPanel.lastTime / 1000000000d), Mass.timeStep), 10, this.getHeight() - 10);

			for (int i = 0; i < MainPanel.logSize && i < this.fpsLog.size(); i++)
				g2d.drawLine(this.getWidth() - i, this.getHeight(), this.getWidth() - i, this.getHeight() - (int) (this.fpsLog.get(i) / 200000));

			if (this.fpsLog.size() > MainPanel.logSize) this.fpsLog.removeLast();

			try
			{
				if (MainPanel.updateSpeed - (System.nanoTime() - startTime) / 1000000 > 0)
					Thread.sleep((long) (MainPanel.updateSpeed - (System.nanoTime() - startTime) / 1000000d));
				else
				{
				}
			}
			catch (final InterruptedException e)
			{
				e.printStackTrace();
			}
			while (MainPanel.updateSpeed - (System.nanoTime() - startTime) / 1000000 > 0)
			{
			}
			MainPanel.lastTime = System.nanoTime() - startTime;
			this.fpsLog.addFirst(MainPanel.lastTime);
			Mass.setTimeStep(10 / MainPanel.getFPS());
			this.repaint();
		}
	}
}
