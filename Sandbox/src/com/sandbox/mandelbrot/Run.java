package com.sandbox.mandelbrot;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.apache.commons.math3.complex.Complex;

public class Run
{
	public static void main(String[] args)
	{
		JComponent comp = new JComponent()
		{
			private static final long	serialVersionUID	= 1L;
			public static final double	scale				= 200;

			@Override
			public void paint(Graphics g)
			{
				long startTime = System.nanoTime();
				double normalizedValue = 0;
				final int width = this.getWidth();
				final int height = this.getHeight();
				double max = maxIteration;

				Complex c = new Complex(0, 0);
				for (int x = 0; x < width; x++)
				{
					for (int y = 0; y < height; y++)
					{
						c = new Complex((x - (5 * width / 8)) / scale, (y - (height / 2)) / scale);
						normalizedValue = 1 - (mandelbrotDivergeRate(c) / max);
						try
						{
							g.setColor(new Color((int) (normalizedValue * 255), (int) (normalizedValue * 255), (int) (normalizedValue * 255)));
						}
						catch (IllegalArgumentException e)
						{
							//System.out.println(mandelbrotDivergeRate(c) + " / " + max);
							g.setColor(Color.BLACK);
						}
						g.drawLine(x, y, x, y);
					}
				}
				System.out.println(String.format("Frame took %.3f seconds to render.", (System.nanoTime() - startTime) / 1000000000d));
			}
		};

		JFrame frame = new JFrame();
		frame.add(comp);
		frame.setSize(700, 600);
		frame.setBackground(Color.WHITE);
		frame.setVisible(true);
	}

	public static final double	bound	= 2;
	public static final int maxIteration = 50;

	public static int mandelbrotDivergeRate(Complex c)
	{
		for (int i = 1; i <= maxIteration; i++)
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
}
