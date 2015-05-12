package com.sandbox.mandelbrot;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.commons.math3.complex.Complex;

import com.sandbox.utils.HSLColor;

public class AdvancedRun
{
	private static int	maxIteration	= 0;

	public static Complex function(Complex z)
	{
		Complex value = z;

		for (int i = 0; i < maxIteration; i++)
		{
			value = value.pow(2).add(new Complex(-0.62772, 0.42193));
		}

		return value;
	}

	public static void main(String[] args)
	{
		JFrame frame = new JFrame("Complex Plane");
		frame.add(new JPanel()
		{
			private static final long	serialVersionUID	= 1L;

			public void paintComponent(Graphics g)
			{
				long start = System.currentTimeMillis();
				super.paintComponent(g);

				final int width = this.getWidth();
				final int height = this.getHeight();

				ExecutorService pool = Executors.newFixedThreadPool(4);

				@SuppressWarnings("unchecked")
				Future<Complex>[][] pixels = new Future[height][width];

				for (int row = 0; row < pixels.length; row++)
				{
					for (int col = 0; col < pixels[row].length; col++)
					{
						final double x = (col - width / 2) / 150d;
						final double y = (row - height / 2) / 150d;
						pixels[row][col] = pool.submit(new Callable<Complex>()
						{
							@Override
							public Complex call() throws Exception
							{
								// Thread.sleep(0, 0);
								return function(new Complex(x, y));
							}
						});
					}
				}

				BufferedImage bi = new BufferedImage(pixels[0].length, pixels.length, BufferedImage.TYPE_INT_ARGB);

				for (int row = 0; row < pixels.length; row++)
				{
					for (int col = 0; col < pixels[row].length; col++)
					{
						try
						{
							bi.setRGB(
									col,
									row,
									HSLColor.toRGB((float) ((-pixels[row][col].get().getArgument()) / (2 * Math.PI)), 1,
											(float) ((Math.log1p(pixels[row][col].get().abs())) % 1)));
							// System.out.println("Drew " + col + ", " + row);
						}
						catch (InterruptedException e)
						{
							e.printStackTrace();
						}
						catch (ExecutionException e)
						{
							e.printStackTrace();
						}
					}
				}
				g.drawImage(bi, 0, 0, this);
				System.out.println("Time elapsed: " + (System.currentTimeMillis() - start) + "ms");
				maxIteration++;
				repaint();
			}
		});
		frame.setSize(600, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
