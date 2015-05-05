package com.sandbox.mandelbrot;

import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class AdvancedRun
{
	public static void main(String[] args)
	{
		JFrame frame = new JFrame("Julia Set");
		frame.add(new JPanel()
		{
			private static final long	serialVersionUID	= 1L;

			public void paintComponent(Graphics g)
			{
				super.paintComponent(g);

				final int[][] values = new int[this.getHeight()][this.getWidth()];
				final int width = this.getWidth();
				final int height = this.getHeight();

				long start = System.currentTimeMillis();

				ExecutorService e = Executors.newFixedThreadPool(4, Executors.defaultThreadFactory());

				for (int y = 0; y < height; y++)
				{
					for (int x = 0; x < width; x++)
					{
						final int tempX = x;
						final int tempY = y;

						e.execute(new Runnable()
						{
							@Override
							public void run()
							{
								synchronized (values)
								{
									values[tempY][tempX] = (int) (new Point2D.Double(tempX - width / 2, tempY - height / 2)).distance(0, 0);
								}
							}
						});
					}
				}

				e.shutdown();
				try
				{
					e.awaitTermination(1, TimeUnit.DAYS);
				}
				catch (InterruptedException e1)
				{
					e1.printStackTrace();
				}

				for (int y = 0; y < this.getHeight(); y++)
				{
					for (int x = 0; x < this.getWidth(); x++)
					{
						g.setColor(Run.getRainbow(values[y][x] / (Math.PI * 64)));
						g.drawLine(x, y, x, y);
					}
				}
				System.out.println("Time taken " + (System.currentTimeMillis() - start));
				repaint();
			}
		});
		frame.setSize(600, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
