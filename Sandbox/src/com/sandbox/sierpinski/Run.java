package com.sandbox.sierpinski;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Run
{
	public static void main(String[] args)
	{
		JFrame frame = new JFrame("Fractal");
		JPanel panel = new JPanel()
		{
			private static final long	serialVersionUID	= 1L;

			@Override
			public void paintComponent(Graphics g)
			{
				super.paintComponent(g);

				Point p1 = new Point(0, 0);
				Point p2 = new Point(this.getWidth(), 0);
				Point p3 = new Point(this.getWidth() / 2, this.getHeight());
				Point p4 = new Point(this.getWidth(), this.getHeight());

				Point draw = new Point(this.getWidth()/2, this.getHeight()/2);
				BufferedImage bi = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
				Graphics2D g2d = bi.createGraphics();
				Random r = new Random();

				g2d.setColor(Color.BLACK);
				for (int i = 0; i < 1000; i++)
				{
					g2d.drawLine(draw.x, draw.y, draw.x, draw.y);

					int decide = r.nextInt(4);

					switch (decide)
					{
					case 0:
						// draw = new Point((draw.x + p1.x * 2) / 3, (draw.y + p1.y * 2) / 3);
						// draw = new Point(draw.x + 1, draw.y);
						draw.x *= 9999 / 10000d;
						break;
					case 1:
						// draw = new Point((draw.x + p2.x * 2) / 3, (draw.y + p2.y * 2) / 3);
						// draw = new Point(draw.x, draw.y + 1);
						draw.y *= 9999 / 10000d;
						break;
					case 2:
						// draw = new Point((draw.x + p3.x * 2) / 3, (draw.y + p3.y * 2) / 3);
						// draw = new Point(draw.x - 1, draw.y);
						draw.x *= 10000 / 9999d;
						break;
					case 3:
						// draw = new Point((draw.x + p4.x) / 2, (draw.y + p4.y) / 2);
						// draw = new Point(draw.x, draw.y - 1);
						draw.y *= 10000 / 9999d;
						break;
					}
				}
				g.drawImage(bi, 0, 0, this);
				repaint();
			}
		};
		frame.add(panel);
		frame.setSize(600, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
