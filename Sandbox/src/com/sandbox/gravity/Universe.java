package com.sandbox.gravity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.math3.util.FastMath;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class Universe
{
	private double			gravConst;
	private ArrayList<Body>	bodies;
	private ExecutorService	pool;
	private double			deltaTime;
	private Path2D.Double	barycenter;
	private int				tick	= 0;
	private XYSeries		totalEnergy;

	public Universe(double gravConst, double deltaTime, XYSeriesCollection logs)
	{
		this.gravConst = gravConst;
		this.bodies = new ArrayList<Body>();
		this.pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		this.deltaTime = deltaTime;
		this.barycenter = new Path2D.Double();
		this.barycenter.moveTo(0, 0);
		this.totalEnergy = new XYSeries("Total System Energy");
		logs.addSeries(this.totalEnergy);
	}

	public int getTick()
	{
		return this.tick;
	}

	public synchronized void addBody(Body b)
	{
		b.setUniverse(this);
		this.bodies.add(b);
	}

	public double getDeltaTime()
	{
		return this.deltaTime;
	}

	public void setDeltaTime(double deltaTime)
	{
		this.deltaTime = deltaTime;
	}

	public double getGravConst()
	{
		return this.gravConst;
	}

	public BufferedImage getFrame(int width, int height)
	{
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = bi.createGraphics();

		g2d.setColor(Color.BLACK);
		for (Body b : this.bodies)
		{
			Point2D p = b.getPoint();
			double radius = FastMath.sqrt(b.getMass());
			g2d.setPaint(b.getPaint());
			g2d.fillOval((int) (p.getX() - radius), (int) (bi.getHeight() - p.getY() - radius), (int) (2 * radius), (int) (2 * radius));
		}

		g2d.setColor(Color.RED);
		// g2d.draw(this.barycenter);

		return bi;
	}

	public long stepTime()
	{
		long startTime = System.nanoTime();

		if (this.getTick() % 100 == -1)
		{
			double totalMass = 0;
			double xCenter = 0;
			double yCenter = 0;
			for (Body b : this.bodies)
			{
				totalMass += b.getMass();
				Point2D p = b.getPoint();
				xCenter += b.getMass() * p.getX();
				yCenter += b.getMass() * p.getY();
			}
			xCenter /= totalMass;
			yCenter /= totalMass;
			this.barycenter.lineTo(xCenter, 500 - yCenter);
		}
		if (this.getTick() % 100 == 0)
		{
			double momentumSum = 0;
			for (Body b : this.bodies)
			{
				momentumSum += b.getMass() * b.getVelocity();
			}
			this.totalEnergy.add(this.getTick(), momentumSum / 25);
		}

		LinkedList<Future<?>> completion = new LinkedList<Future<?>>();

		for (final Body b : this.bodies)
		{
			for (final Body o : this.bodies)
			{
				if (b != o)
				{
					completion.add(pool.submit(new Runnable()
					{
						@Override
						public void run()
						{
							b.reactTo(o);
						}
					}));
				}
			}
		}

		for (Future<?> f : completion)
		{
			try
			{
				f.get();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			catch (ExecutionException e)
			{
				e.printStackTrace();
			}
		}

		for (Body b : this.bodies)
		{
			b.move();
		}
		this.tick++;

		return System.nanoTime() - startTime;
	}
}
