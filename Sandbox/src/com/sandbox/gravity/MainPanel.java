package com.sandbox.gravity;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.JComponent;
import javax.swing.JFrame;

public class MainPanel extends JComponent
{
	public static final int		updateSpeed			= 4;
	public static final int		logSize				= 500;
	private static final long	serialVersionUID	= 1L;
	private LinkedList<Long>	fpsLog				= new LinkedList<Long>();

	public static void main(final String[] args)
	{
		final MainPanel component = new MainPanel();
		final JFrame frame = new JFrame("Gravity...");
		frame.setBounds(0, 0, 720, 720);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		component.setBackground(Color.WHITE);
		frame.setBackground(Color.WHITE);
		frame.getContentPane().add(component);
		frame.setVisible(true);
	}

	private long					lastTime	= 0;

	private final ArrayList<Mass>	masses		= new ArrayList<Mass>();

	public MainPanel()
	{
		for (int i = 0; i < 0; i++)
		{
			this.masses.add(new Mass((int) (Math.random() * 720), (int) (Math.random() * 720), (int) (Math.random() * 50)));
			this.masses.get(i).setxV((int) ((Math.random() - 0.5) * 50));
			this.masses.get(i).setyV((int) ((Math.random() - 0.5) * 50));
		}

		this.masses.add(new Mass(400, 400, 2000));
		this.masses.add(new Mass(400, 200, 800));

		this.masses.get(0).setxV(2.777777777);
		this.masses.get(1).setxV(-7);
	}

	@Override
	public void paintComponent(final Graphics g)
	{
		final long startTime = System.nanoTime();
		final Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		g2d.translate(0, this.getHeight() / 2);
		g2d.scale(1, -1);
		g2d.translate(0, -this.getHeight() / 2);

		for (int i = 0; i < masses.size(); i++)
		{
			if (masses.get(i).getxCenter() < 0 || masses.get(i).getxCenter() > getWidth())
			{
				masses.get(i).setxV(-masses.get(i).getxV());
			}
			if (masses.get(i).getyCenter() < 0 || masses.get(i).getyCenter() > getHeight())
			{
				masses.get(i).setyV(-masses.get(i).getyV());
			}
			masses.get(i).collideAll(this.masses);
		}

		for (final Mass m : this.masses)
		{
			m.attractAll(this.masses);
		}

		for (final Mass m : this.masses)
		{
			m.stepTime();
			m.paint(g);
		}

		g2d.translate(0, this.getHeight() / 2);
		g2d.scale(1, -1);
		g2d.translate(0, -this.getHeight() / 2);

		g2d.setColor(Color.BLACK);
		g2d.drawString(String.format("FPS: %06.2f", 1 / (this.lastTime / 1000000000d)), 10, this.getHeight() - 10);

		g2d.setPaint(new GradientPaint(getWidth(), getHeight(), Color.GREEN, getWidth(), getHeight() - 100, Color.RED));
		for (int i = 0; i < logSize && i < fpsLog.size(); i++)
		{
			g2d.drawLine(getWidth() - i, getHeight(), getWidth() - i, getHeight() - (int) (fpsLog.get(i) / 200000));
		}

		if (fpsLog.size() > logSize)
		{
			fpsLog.removeLast();
		}

		try
		{
			if (MainPanel.updateSpeed - (System.nanoTime() - startTime) / 1000000 > 0)
				Thread.sleep((long) (MainPanel.updateSpeed - (System.nanoTime() - startTime) / 1000000d));
		}
		catch (final InterruptedException e)
		{
			e.printStackTrace();
		}
		while (MainPanel.updateSpeed - (System.nanoTime() - startTime) / 1000000 > 0) // Mop up the rest with a high-precision timer
		{
		}
		this.lastTime = System.nanoTime() - startTime;
		fpsLog.addFirst(lastTime);
		this.repaint();
	}
}
