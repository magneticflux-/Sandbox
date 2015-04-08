package com.sandbox.mandelbrot;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.commons.math3.complex.Complex;

public class Run
{
	public static final double	bound			= 2;
	public static final int		maxIteration	= 200;

	public static void main(String[] args)
	{
		JPanel comp = new JPanel()
		{
			private static final long	serialVersionUID	= 1L;
			public int					renderResolution	= 2;
			public double				scale				= 200;
			public double				xTarget				= 0;
			public double				yTarget				= 0;
			public double				zoomMultiplier		= 1;
			public Map<Point, Double>	normalizedValues	= Collections.synchronizedMap(new HashMap<Point, Double>());

			@Override
			public void paintComponent(Graphics g)
			{
				long startTime = System.nanoTime();
				double normalizedValue = 0;
				final int width = this.getWidth();
				final int height = this.getHeight();
				final double max = maxIteration;

				final LinkedList<Thread> threads = new LinkedList<Thread>();
				for (int x = 0; x < width; x += renderResolution)
				{
					final int temp1 = x;
					Thread t = new Thread()
					{
						private int	column	= temp1;

						@Override
						public void run()
						{
							System.out.println("Thread for column " + column + " reporting for duty, sir!");
							for (int y = 0; y < height; y += renderResolution)
							{
								synchronized (normalizedValues)
								{
									normalizedValues.put(new Point(column, y), 1 - (mandelbrotDivergeRate(new Complex((column - (width / 2) + xTarget) / scale,
											(y - (height / 2) + yTarget) / scale)) / max));
								}
							}
							System.out.println("Thread for column " + column + " has completed its task.");
						}
					};
					t.setDaemon(true);
					t.setPriority(Thread.MIN_PRIORITY);
					threads.addLast(t);

					boolean test = false;
					if (test)
					{
						for (int y = 0; y < height; y += renderResolution)
						{
							Complex c = new Complex((x - (width / 2) + xTarget) / scale, (y - (height / 2) + yTarget) / scale);
							normalizedValue = 1 - (mandelbrotDivergeRate(c) / max);
							g.setColor(Run.getRainbow(normalizedValue * 5));
							g.fillRect(x, y, renderResolution, renderResolution);
						}
					}

					// System.out.println(String.format("%d / %d - %.3f", x, width, 100 * x / (double) width) + "%");
				}

				ExecutorService executor = Executors.newFixedThreadPool(1024);
				for (Thread t : threads)
				{
					executor.execute(t);
				}
				executor.shutdown();
				try
				{
					executor.awaitTermination(120, TimeUnit.SECONDS);
					System.out.println("Waiting for all threads to finish.");
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}

				for (Entry<Point, Double> e : normalizedValues.entrySet())
				{
					g.setColor(Run.getRainbow(e.getValue() * 5));
					g.fillRect(e.getKey().x, e.getKey().y, renderResolution, renderResolution);
				}
				// renderResolution--;
				// if (renderResolution < 1) renderResolution = 1;
				scale *= zoomMultiplier;
				xTarget *= zoomMultiplier;
				yTarget *= zoomMultiplier;
				System.out.println(String.format("Frame took %.4f seconds to render.", (System.nanoTime() - startTime) / 1000000000d));
				// repaint();
			}
		};

		JFrame frame = new JFrame();
		frame.add(comp);
		frame.setBackground(Color.WHITE);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		//frame.setSize(200, 900);
		frame.setVisible(true);
		System.out.println(comp.getSize());
	}

	public static int mandelbrotDivergeRate(Complex c)
	{
		for (int i = 0; i < maxIteration; i++)
		{
			if (getMandelbrotIteration(c, i).abs() > bound) return i;
		}
		return -1;
	}

	public static Complex getMandelbrotIteration(Complex c, int n)
	{
		if (n == 0)
		{
			return c;
		}
		else
		{
			return getMandelbrotIteration(c, n - 1).pow(2).add(c);
		}
	}

	public static Color getRainbow(double i)
	{
		int r = 128 + (int) (127 * Math.sin(i));
		int g = 128 + (int) (127 * Math.sin(i + (2 * Math.PI / 3)));
		int b = 128 + (int) (127 * Math.sin(i + (4 * Math.PI / 3)));
		if (i > 0)
			return new Color(r, g, b);
		else
			return Run.getRainbow(1);
	}
}
