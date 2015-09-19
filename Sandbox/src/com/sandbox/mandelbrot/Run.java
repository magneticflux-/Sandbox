package com.sandbox.mandelbrot;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.util.FastMath;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Run {
	public static final double bound = 2;
	public static int maxIteration = 10;
	public static Complex constant = new Complex(-0.225747112829, 0);

	public static void main(final String[] args) {
		final JPanel comp = new JPanel() {
			private static final long serialVersionUID = 1L;
			public Map<Point, Integer> normalizedValues = Collections.synchronizedMap(new HashMap<Point, Integer>());
			public int renderResolution = 1;
			public double scale = 250;
			public double xTarget = -100;                                                        // 90.664;
			public double yTarget = 0;                                                            // 61.475;
			public double zoomMultiplier = 1;

			@Override
			public void paintComponent(final Graphics g) {
				final long startTime = System.nanoTime();
				final int width = this.getWidth();
				final int height = this.getHeight();

				final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
				System.out.println(Runtime.getRuntime().availableProcessors() + " processors available.");
				for (int x = 0; x < width; x += this.renderResolution) {
					final int temp1 = x;
					final Thread t = new Thread() {
						private final int column = temp1;

						@Override
						public void run() {
							// System.out.println("Thread for column " + column + " reporting for duty, sir!");
							final HashMap<Point, Integer> tempMap = new HashMap<Point, Integer>();
							for (int y = 0; y < height; y += renderResolution)
								tempMap.put(new Point(this.column, y), Run.mandelbrotDivergeRate(new Complex((this.column - width / 2 + xTarget) / scale, (y - height / 2 + yTarget) / scale)));
							normalizedValues.putAll(tempMap);
							// System.out.println("Thread for column " + column + " has completed its task.");
						}
					};
					t.setDaemon(true);
					executor.execute(t);
					// System.out.println(String.format("%d / %d - %.3f", x, width, 100 * x / (double) width) + "%");
				}
				executor.shutdown();
				try {
					executor.awaitTermination(365, TimeUnit.DAYS);
					System.out.println("Waiting for all threads to finish.");
				} catch (final InterruptedException e) {
					e.printStackTrace();
				}

				final BufferedImage bImg = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
				final Graphics2D g2d = bImg.createGraphics();

				for (final Entry<Point, Integer> e : this.normalizedValues.entrySet()) {
					g2d.setColor(Run.getRainbow(e.getValue() * FastMath.PI * 2 / Run.maxIteration));
					g2d.fillRect(e.getKey().x, e.getKey().y, this.renderResolution, this.renderResolution);
					// bImg.setRGB(e.getKey().x, e.getKey().y, Run.getRainbow(e.getValue() * FastMath.PI * 2).getRGB());
				}

				try {
					ImageIO.write(bImg, "png", new File(String.format("images/test_%04d.png", Run.maxIteration)));
				} catch (final IOException e1) {
					e1.printStackTrace();
				}

				g.drawImage(bImg, 0, 0, this);

				// renderResolution--;
				// if (renderResolution < 1) renderResolution = 1;
				this.scale *= this.zoomMultiplier;
				this.xTarget *= this.zoomMultiplier;
				this.yTarget *= this.zoomMultiplier;
				g.setColor(Color.WHITE);
				g.drawString("Max iterations: " + Run.maxIteration, 20, 20);
				System.out.println(String.format("Frame took %.4f seconds to render at %d iterations.", (System.nanoTime() - startTime) / 1000000000d, Run.maxIteration));
				Run.maxIteration++;
				// maxIteration = (int) (maxIteration * this.zoomMultiplier);
				this.repaint();
			}
		};

		final JFrame frame = new JFrame();
		frame.add(comp);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		frame.setSize(900, 700);
		frame.setVisible(true);
	}

	public static int mandelbrotDivergeRate(Complex c) {
		final Complex initial = c;

		for (int i = 0; i < Run.maxIteration; i++) {
			c = Run.getNextMandelbrotIteration(c, initial);
			if (c.abs() > Run.bound)
				return i;
		}
		return -1;
	}

	public static Color getRainbow(final double i) {
		final int r = 128 + (int) (127 * FastMath.sin(i));
		final int g = 128 + (int) (127 * FastMath.sin(i + 2 * FastMath.PI / 3));
		final int b = 128 + (int) (127 * FastMath.sin(i + 4 * FastMath.PI / 3));
		if (i >= 0)
			return new Color(r, g, b);
		else
			return Color.BLACK;// new Color(0, 0, 0);
	}

	public static Complex getNextMandelbrotIteration(final Complex c, final Complex initial) {
		return c.pow(2).add(initial);
	}
}
