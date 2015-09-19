package com.sandbox.neural_old;

import org.apache.commons.math3.util.FastMath;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Arrays;
import java.util.Random;

import javax.swing.JComponent;
import javax.swing.JFrame;

public class Run {
	public static void main(final String[] args) {
		final String networkType = "2 1";
		final Random r = new Random();
		final InputValue var1 = new InputValue(3d);
		final InputValue var2 = new InputValue(4d);
		final Population maxPop = new Population(1, networkType, new InputValue[]{var1, var2}, new double[]{var1.getOutput() + var2.getOutput()}, r);

		final JFrame frame = new JFrame("Network");
		final JComponent panel = new JComponent() {
			private static final long serialVersionUID = 1L;

			@Override
			public void paintComponent(final Graphics g) {
				final Network n = maxPop.selectBest(1)[0];
				final int width = this.getWidth();
				final int height = this.getHeight();
				final double[][] pixels = new double[height][width];

				double max = 0;
				for (int y = 0; y < height; y++)
					for (int x = 0; x < width; x++) {
						final double[] input = new double[]{x - width / 2, y - height / 2};

						n.setEnvironment(input);
						pixels[y][x] = n.getOutputs()[0];
						if (FastMath.abs(pixels[y][x]) > max)
							max = FastMath.abs(pixels[y][x]);
					}

				for (int y = 0; y < height; y++)
					for (int x = 0; x < width; x++) {
						final int shade = 128 + (int) (127 * (pixels[y][x] / max));
						try {
							g.setColor(new Color(shade, shade, shade));
						} catch (final IllegalArgumentException e) {
							System.out.println("Shade was: " + shade + "; pixel was " + pixels[y][x] + "; max was " + max);
						}

						g.drawLine(x, y, x, y);
					}

				g.setColor(Color.RED);
				final double x = width / 4;
				final double y = height / 4;
				final double[] input = new double[]{x - width / 2, y - height / 2};
				n.setEnvironment(input);
				final double value = n.getOutputs()[0];
				g.fillRect((int) x, (int) y, 1, 1);
				g.drawString(x - width / 2 + " [operation] " + (y - height / 2) + " = " + value, (int) x, (int) y);
			}
		};
		frame.add(panel);
		frame.setSize(400, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

		final JFrame frame2 = new JFrame("Correct");
		final JComponent panel2 = new JComponent() {
			private static final long serialVersionUID = 1L;

			@Override
			public void paintComponent(final Graphics g) {
				final int width = this.getWidth();
				final int height = this.getHeight();
				final double[][] pixels = new double[height][width];

				double max = 0;
				for (int y = 0; y < height; y++)
					for (int x = 0; x < width; x++) {
						final double[] input = new double[]{x - width / 2, y - height / 2};

						pixels[y][x] = Config.operation(input[0], input[1]);
						if (FastMath.abs(pixels[y][x]) > max)
							max = FastMath.abs(pixels[y][x]);
					}

				for (int y = 0; y < height; y++)
					for (int x = 0; x < width; x++) {
						final int shade = 128 + (int) (127 * (pixels[y][x] / max));
						try {
							g.setColor(new Color(shade, shade, shade));
						} catch (final IllegalArgumentException e) {
							System.out.println("Shade was: " + shade + "; pixel was " + pixels[y][x] + "; max was " + max);
						}

						g.drawLine(x, y, x, y);
					}

				g.setColor(Color.RED);
				final double x = width / 4;
				final double y = height / 4;
				final double value = Config.operation(x - width / 2, y - height / 2);
				g.fillRect((int) x, (int) y, 1, 1);
				g.drawString(x - width / 2 + " [operation] " + (y - height / 2) + " = " + value, (int) x, (int) y);
			}
		};
		frame2.add(panel2);
		frame2.setSize(400, 400);
		frame2.setLocation(400, 0);
		frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame2.setVisible(true);

		final JFrame frame3 = new JFrame("Difference");
		final JComponent panel3 = new JComponent() {
			private static final long serialVersionUID = 1L;

			@Override
			public void paintComponent(final Graphics g) {
				final Network n = maxPop.selectBest(1)[0];
				final int width = this.getWidth();
				final int height = this.getHeight();
				final double[][] pixels = new double[height][width];

				double max = 0;
				for (int y = 0; y < height; y++)
					for (int x = 0; x < width; x++) {
						final double[] input = new double[]{x - width / 2, y - height / 2};

						n.setEnvironment(input);
						pixels[y][x] = FastMath.abs(Config.operation(input[0], input[1]) - n.getOutputs()[0]);

						if (FastMath.abs(pixels[y][x]) > max)
							max = FastMath.abs(pixels[y][x]);
					}

				for (int y = 0; y < height; y++)
					for (int x = 0; x < width; x++) {
						final int shade = (int) (255 * (pixels[y][x] / max));
						try {
							g.setColor(new Color(shade, shade, shade));
						} catch (final IllegalArgumentException e) {
							System.out.println("Shade was: " + shade + "; pixel was " + pixels[y][x] + "; max was " + max);
							g.setColor(Color.CYAN);
						}

						g.drawLine(x, y, x, y);
					}
				g.setColor(Color.RED);
				g.drawString("Maximum: " + max, 50, 50);
			}
		};
		frame3.add(panel3);
		frame3.setSize(400, 400);
		frame3.setLocation(200, 400);
		frame3.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame3.setVisible(true);

		if (Config.DEBUG) {
			System.out.println("---DEBUG---");

			final Network n1 = new Network("2 2 1", new InputValue[]{var1, var2}, r);
			n1.randomizeWeights(-1, 1);
			n1.randomizeBiases(-2, 2);
			final Network n2 = new Network("2 2 1", new InputValue[]{var1, var2}, r);
			n2.randomizeWeights(-1, 1);
			n2.randomizeBiases(-2, 2);

			n1.evalNet();
			n1.inspectNet();
			n2.evalNet();
			n2.inspectNet();

			System.out.println("Offspring:");
			final Network[] offspring = n1.breed(n2);

			offspring[0].evalNet();
			offspring[0].inspectNet();
			offspring[1].evalNet();
			offspring[1].inspectNet();
		}
		maxPop.updateFitness();
		while (true) {
			final Population p = new Population(10000, networkType, new InputValue[]{var1, var2}, new double[]{var1.getOutput() * var2.getOutput()}, r);
			// p.viewStatus();
			p.updateFitness();
			final Network[] victors = p.selectBest(1);
			for (final Network n : Arrays.asList(victors))
				System.out.println(n.getFitness());
			maxPop.addNetwork(victors[0]);
			maxPop.updateFitness();

			frame.repaint();
			try {
				Thread.sleep(500);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
			frame2.repaint();
			try {
				Thread.sleep(500);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
			frame3.repaint();
		}
	}
}
