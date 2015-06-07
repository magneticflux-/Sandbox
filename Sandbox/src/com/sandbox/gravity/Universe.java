package com.sandbox.gravity;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Universe
{
	private double			gravConst;
	private ArrayList<Body>	bodies;

	public Universe(double gravConst)
	{
		this.gravConst = gravConst;
		this.bodies = new ArrayList<Body>();
	}

	public synchronized void addBody(Body b)
	{
		this.bodies.add(b);
	}

	public long stepTime()
	{
		long startTime = System.nanoTime();
		ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

		for (Body b : this.bodies)
		{
			for (Body o : this.bodies)
			{
				if (b != o)
				{
					b.reactTo(o);
				}
			}
		}

		return System.nanoTime() - startTime;
	}
}
