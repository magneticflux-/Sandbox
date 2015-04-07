package com.sandbox.mandelbrot;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.commons.math3.complex.Complex;

public class Run
{

	public static void main(String[] args)
	{
		JPanel comp = new JPanel()
		{
			private static final long	serialVersionUID	= 1L;
			public int					renderResolution	= 8;
			public double				scale				= 200;
			public double				xTarget				= 86;
			public double				yTarget				= 75;
			public double				zoomMultiplier		= 1.5;

			@Override
			public void paintComponent(Graphics g)
			{
				long startTime = System.nanoTime();
				double normalizedValue = 0;
				final int width = this.getWidth();
				final int height = this.getHeight();
				double max = maxIteration;

				Complex c = null;
				for (int x = 0; x < width; x += renderResolution)
				{
					for (int y = 0; y < height; y += renderResolution)
					{
						c = new Complex((x - (width / 2) + xTarget) / scale, (y - (height / 2) + yTarget) / scale);
						normalizedValue = 1 - (mandelbrotDivergeRate(c) / max);
						g.setColor(Run.getRainbow(normalizedValue * 5));
						g.fillRect(x, y, renderResolution, renderResolution);
					}
				}
				// renderResolution--;
				// if (renderResolution < 1) renderResolution = 1;
				scale *= zoomMultiplier;
				xTarget *= zoomMultiplier;
				yTarget *= zoomMultiplier;
				System.out.println(String.format("Frame took %.4f seconds to render.", (System.nanoTime() - startTime) / 1000000000d));
				repaint();
			}
		};

		JFrame frame = new JFrame();
		frame.add(comp);
		frame.setSize(400, 300);
		frame.setBackground(Color.WHITE);
		frame.setVisible(true);
	}

	public static final double	bound			= 2;
	public static final int		maxIteration	= 25;

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
