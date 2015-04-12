package com.sandbox.neural;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Arrays;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Run
{
	public static void main(String[] args)
	{
		Random r = new Random(4);
		InputValue var1 = new InputValue(3d);
		InputValue var2 = new InputValue(4d);
		Population p = new Population(10000, "2 2 1", new InputValue[] { var1, var2 }, new double[] { var1.getOutput() * var2.getOutput() }, r);
		// p.viewStatus();
		Network[] victors = p.selectBest(2);
		for (Network n : Arrays.asList(victors))
			System.out.println(n.getFitness());
		final Network n = victors[0];

		JFrame frame = new JFrame();
		JPanel panel = new JPanel()
		{
			private static final long	serialVersionUID	= 1L;

			@Override
			public void paintComponent(Graphics g)
			{
				int width = this.getWidth();
				int height = this.getHeight();
				double[][] pixels = new double[height][width];

				double max = 0;
				for (int y = 0; y < height; y++)
				{
					for (int x = 0; x < width; x++)
					{
						double[] input = new double[] { (x - (width / 2)), (y - (height / 2)) };
						n.setEnvironment(input);

						pixels[y][x] = n.getOutputs()[0];
						//pixels[y][x] = (x - (width / 2)) * (y - (height / 2));
						if (pixels[y][x] > max) max = pixels[y][x];
					}
				}

				for (int y = 0; y < height; y++)
				{
					for (int x = 0; x < width; x++)
					{
						int shade = 128 + (int) (127 * (pixels[y][x] / max));
						g.setColor(new Color(shade, shade, shade));
						g.drawLine(x, y, x, y);
					}
				}
			}
		};
		frame.add(panel);
		frame.setSize(200, 200);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

		if (Config.DEBUG)
		{
			System.out.println("---DEBUG---");

			Network n1 = new Network("2 2 1", new InputValue[] { var1, var2 }, r);
			n1.randomizeWeights(-1, 1);
			n1.randomizeBiases(-2, 2);
			Network n2 = new Network("2 2 1", new InputValue[] { var1, var2 }, r);
			n2.randomizeWeights(-1, 1);
			n2.randomizeBiases(-2, 2);

			n1.evalNet();
			n1.inspectNet();
			n2.evalNet();
			n2.inspectNet();

			System.out.println("Offspring:");
			Network[] offspring = n1.breed(n2);

			offspring[0].evalNet();
			offspring[0].inspectNet();
			offspring[1].evalNet();
			offspring[1].inspectNet();
		}
	}
}
