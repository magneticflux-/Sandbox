package com.sandbox.mandelbrot;

import com.sandbox.utils.HSLColor;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.util.FastMath;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class AdvancedRun {
	private static int maxIteration = 0;

	public static void main(final String[] args) {
		final JFrame frame = new JFrame("Complex Plane");
		frame.add(new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
			public void paintComponent(final Graphics g) {
				final long start = System.currentTimeMillis();
				super.paintComponent(g);

				final int width = this.getWidth();
				final int height = this.getHeight();

				final ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

				@SuppressWarnings("unchecked") final Future<Complex>[][] pixels = new Future[height][width];

				for (int row = 0; row < pixels.length; row++)
					for (int col = 0; col < pixels[row].length; col++) {
						final double x = (col - width / 2) / 150d;
						final double y = (row - height / 2) / 150d;
						pixels[row][col] = pool.submit(new Callable<Complex>() {
							@Override
							public Complex call() throws Exception {
								// Thread.sleep(0, 0);
								return AdvancedRun.function(new Complex(x, y));
							}
						});
					}

				final BufferedImage bi = new BufferedImage(pixels[0].length, pixels.length, BufferedImage.TYPE_INT_ARGB);

				for (int row = 0; row < pixels.length; row++)
					for (int col = 0; col < pixels[row].length; col++)
						try {
							bi.setRGB(col, row, HSLColor.toRGB((float) (-pixels[row][col].get().getArgument() / (2 * FastMath.PI)), 1, (float) (FastMath.log1p(pixels[row][col].get().abs()) % 1)));
							// System.out.println("Drew " + col + ", " + row);
						} catch (final InterruptedException e) {
							e.printStackTrace();
						} catch (final ExecutionException e) {
							e.printStackTrace();
						}
				g.drawImage(bi, 0, 0, this);
				System.out.println("Time elapsed: " + (System.currentTimeMillis() - start) + "ms");
				AdvancedRun.maxIteration++;
				this.repaint();
			}
		});
		frame.setSize(600, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	public static Complex function(final Complex z) {
		Complex value = z;

		for (int i = 0; i < AdvancedRun.maxIteration; i++)
			value = value.pow(2).add(new Complex(-0.62772, 0.42193));

		return value;
	}
}
