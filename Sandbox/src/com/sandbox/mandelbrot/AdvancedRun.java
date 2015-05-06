package com.sandbox.mandelbrot;

import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.util.MathUtils;

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

				final int maxLength = 10;
				final double scale = 100;
				for (double real = -0.5; real <= 0.5; real += 0.1)
				{
					for (double complex = -0.5; complex <= 0.5; complex += 0.1)
					{
						Complex c = new Complex(real, complex);
						for (int i = 0; i < maxLength; i++)
						{
							g.setColor(Run.getRainbow(MathUtils.TWO_PI * i / (double) maxLength));
							Complex temp = Run.getNextMandelbrotIteration(c);
							g.drawLine((int) (c.getReal() * scale) + this.getWidth() / 2, (int) (c.getImaginary() * scale) + this.getHeight() / 2,
									(int) (temp.getReal() * scale) + this.getWidth() / 2, (int) (temp.getImaginary() * scale) + this.getHeight() / 2);
							c = temp;
							if (c.abs() > 2) break;
						}
					}
				}
			}
		});
		frame.setSize(600, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
