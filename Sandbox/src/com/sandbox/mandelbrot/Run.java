package com.sandbox.mandelbrot;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Collections;
import java.util.HashMap;
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
	public static int			maxIteration	= 250;

	public static Complex getMandelbrotIteration(final Complex c, final int n)
	{
		if (n == 0)
			return c;
		else
			return Run.getMandelbrotIteration(c, n - 1).pow(2).add(c);
	}

	public static Color getRainbow(final double i)
	{
		final int r = 128 + (int) (127 * Math.sin(i));
		final int g = 128 + (int) (127 * Math.sin(i + 2 * Math.PI / 3));
		final int b = 128 + (int) (127 * Math.sin(i + 4 * Math.PI / 3));
		if (i > 0)
			return new Color(r, g, b);
		else
			return Run.getRainbow(1);
	}

	public static void main(final String[] args)
	{
		final JPanel comp = new JPanel()
		{
			private static final long	serialVersionUID	= 1L;
			public Map<Point, Double>	normalizedValues	= Collections.synchronizedMap(new HashMap<Point, Double>());
			public int					renderResolution	= 1;
			public double				scale				= 400;
			public double				xTarget				= 100;
			public double				yTarget				= 0;
			public double				zoomMultiplier		= 1.5;

			@Override
			public void paintComponent(final Graphics g)
			{
				final long startTime = System.nanoTime();
				final int width = this.getWidth();
				final int height = this.getHeight();
				final double max = Run.maxIteration;

				final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
				System.out.println(Runtime.getRuntime().availableProcessors() + " processors available.");
				for (int x = 0; x < width; x += this.renderResolution)
				{
					final int temp1 = x;
					final Thread t = new Thread()
					{
						private final int	column	= temp1;

						@Override
						public void run()
						{
							// System.out.println("Thread for column " + column + " reporting for duty, sir!");
							final HashMap<Point, Double> tempMap = new HashMap<Point, Double>();
							for (int y = 0; y < height; y += renderResolution)
								tempMap.put(
										new Point(this.column, y),
										1
												- Run.mandelbrotDivergeRate(new Complex((this.column - width / 2 + xTarget) / scale, (y - height / 2 + yTarget)
														/ scale)) / max);
							normalizedValues.putAll(tempMap);
							// System.out.println("Thread for column " + column + " has completed its task.");
						}
					};
					t.setDaemon(true);
					executor.execute(t);
					// System.out.println(String.format("%d / %d - %.3f", x, width, 100 * x / (double) width) + "%");
				}
				executor.shutdown();
				try
				{
					executor.awaitTermination(365, TimeUnit.DAYS);
					System.out.println("Waiting for all threads to finish.");
				}
				catch (final InterruptedException e)
				{
					e.printStackTrace();
				}

				for (final Entry<Point, Double> e : this.normalizedValues.entrySet())
				{
					g.setColor(Run.getRainbow(e.getValue() * Math.PI * 2));
					g.fillRect(e.getKey().x, e.getKey().y, this.renderResolution, this.renderResolution);
				}
				// renderResolution--;
				// if (renderResolution < 1) renderResolution = 1;
				this.scale *= this.zoomMultiplier;
				this.xTarget *= this.zoomMultiplier;
				this.yTarget *= this.zoomMultiplier;
				g.setColor(Color.BLACK);
				g.drawString("Max iterations: " + Run.maxIteration, 20, 20);
				System.out.println(String.format("Frame took %.4f seconds to render.", (System.nanoTime() - startTime) / 1000000000d));
				// maxIteration++;
				this.repaint();
			}
		};

		final JFrame frame = new JFrame();
		frame.add(comp);
		frame.setBackground(Color.WHITE);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		// frame.setSize(200, 1000);
		frame.setVisible(true);
	}

	public static int mandelbrotDivergeRate(final Complex c)
	{
		final double p = Math.sqrt(Math.pow(c.getReal() - 0.25, 2) + Math.pow(c.getImaginary(), 2));
		if (c.getReal() < p - 2 * Math.pow(p, 2) + 0.25 || Math.pow(c.getReal() + 1, 2) + Math.pow(c.getImaginary(), 2) < 0.0625) return -1;

		for (int i = 0; i < Run.maxIteration; i++)
			if (Run.getMandelbrotIteration(c, i).abs() > Run.bound) return i;
		return -1;
	}
}
