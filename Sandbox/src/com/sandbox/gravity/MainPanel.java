package com.sandbox.gravity;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.apache.commons.math3.util.FastMath;

import com.electronauts.mathutil.MathUtil;

public class MainPanel extends JComponent
{
	public static final int		logSize				= 100;
	public static final int		updateSpeed			= 0;
	private static long			lastTime			= 1;
	private static final long	serialVersionUID	= 1L;

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

		Thread t = new Thread()
		{

			@Override
			public void run()
			{
				while (true)
				{
					synchronized (component)
					{
						final Mass mass = new Mass((int) (FastMath.random() * component.getWidth()), (int) (FastMath.random() * component.getHeight()),
								(int) (FastMath.random() * 100));
						mass.setxV((FastMath.random() - 0.5) * 0.000025);
						mass.setyV((FastMath.random() - 0.5) * 0.000025);
						component.masses.add(mass);
					}
					try
					{
						Thread.sleep(1000);
					}
					catch (final Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		};
		t.start();
	}

	private final LinkedList<Long>	fpsLog	= new LinkedList<Long>();

	private final ArrayList<Mass>	masses	= new ArrayList<Mass>();

	public MainPanel()
	{
		for (int i = 0; i < 0; i++)
		{
			this.masses.add(new Mass((int) (FastMath.random() * 720), (int) (FastMath.random() * 720), (int) (FastMath.random() * 200)));
			this.masses.get(i).setxV((FastMath.random() - 0.5) * 0.0001);
			this.masses.get(i).setyV((FastMath.random() - 0.5) * 0.0001);
		}

		for (int x = 0; x <= 720; x += 60)
			for (int y = 0; y <= 720; y += 60)
			{
				final Mass m = new Mass(x, y, 200);
				MathUtil.angleTo(new Point2D.Double(), new Point2D.Double());
				// PolarPoint p = new PolarPoint(1,1);
				m.setxV(0);
				this.masses.add(m);
			}
	}

	@Override
	public void paintComponent(final Graphics g)
	{
		final long startTime = System.nanoTime();
		final Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		g2d.translate(0, this.getHeight() / 2);
		g2d.scale(1, -1);
		g2d.translate(0, -this.getHeight() / 2);

		synchronized (this)
		{
			for (int i = 0; i < this.masses.size(); i++)
			{
				final boolean wallCollide = true;
				if (wallCollide) if (this.masses.get(i).getxCenter() < 0)
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
				this.masses.get(i).collideAll(this.masses);
			}

			ExecutorService es = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

			for (final Mass m : this.masses)
			{
				es.submit(new Runnable()
				{
					@Override
					public void run()
					{
						m.attractAll(masses);
					}
				});
			}
			es.shutdown();
			try
			{
				es.awaitTermination(1, TimeUnit.DAYS);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}

			for (final Mass m : this.masses)
			{
				m.stepTime();
				m.paint(g2d);
			}
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
		Mass.setTimeStep(250000 / MainPanel.getFPS());

		this.repaint();
	}
}
