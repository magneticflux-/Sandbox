package com.sandbox.gravity;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class RunV2
{
	public static void main(String[] args)
	{
		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries testSeries = new XYSeries("Test");
		dataset.addSeries(testSeries);
		JFreeChart chart = ChartFactory.createXYLineChart("Test Chart", "Simulation Ticks", "Particle Speed", dataset, PlotOrientation.VERTICAL, false, true,
				false);
		ChartPanel chartPanel = new ChartPanel(chart, ChartPanel.DEFAULT_WIDTH, ChartPanel.DEFAULT_HEIGHT, ChartPanel.DEFAULT_MINIMUM_DRAW_WIDTH,
				ChartPanel.DEFAULT_MINIMUM_DRAW_HEIGHT, ChartPanel.DEFAULT_MAXIMUM_DRAW_WIDTH, ChartPanel.DEFAULT_MAXIMUM_DRAW_HEIGHT, false, false, true,
				true, false, false);
		JFrame frame2 = new JFrame("Chart");
		frame2.add(chartPanel);
		frame2.setSize(700, 800);
		frame2.setLocation(550, 0);
		frame2.setVisible(true);
		frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		final JFrame frame = new JFrame("Gravity");

		final Universe u = new Universe(0.1, 0.01);
		Random r = new Random(0);

		for (int i = 0; i < 5; i++)
		{
			u.addBody(new Body(r.nextInt(200) + 100, r.nextInt(200) + 100, (r.nextDouble() - 0.5) / 2, (r.nextDouble() - 0.5) / 2, dataset, chart.getXYPlot()
					.getRenderer()));
		}

		// u.addBody(new Body(100, 100, 0.3, 0.1, dataset));
		// u.addBody(new Body(300, 100, 0.1, 0.1, dataset));
		// u.addBody(new Body(150, 300, 0.1, -0.1, dataset));
		// u.addBody(new Body(100, 150, -0.1, -0.1, dataset));

		frame.add(new JPanel()
		{
			{
				this.setBackground(Color.WHITE);
			}

			private static final long	serialVersionUID	= 1L;
			private BufferedImage		frame				= null;

			@Override
			public void paintComponent(Graphics g)
			{
				long startTime = System.nanoTime();
				super.paintComponent(g);
				g.drawImage(frame, 0, 0, this);

				u.stepTime();
				if (u.getTick() % 1000 == 0)
				{
					frame = u.getFrame(this.getWidth(), this.getHeight());
				}

				double framerate = 1000000000 / (System.nanoTime() - startTime);
				g.drawString(framerate + " FPS", 50, 50);

				repaint();
			}
		});
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(500, 500);
		frame.setBackground(Color.WHITE);
		frame.setVisible(true);
	}
}
