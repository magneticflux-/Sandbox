package com.sandbox.gravity;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Iterator;

public class Mass
{
	private double				mass;
	private static double		maxMass					= 0;

	private double				xCenter, yCenter;

	private double				xV, yV;

	public static final double	GRAVITATIONAL_CONSTANT	= 6.673848 * Math.pow(10, 0);
	public static final double	TIME_STEP				= 1 / 5d;

	public Mass(final double xCenter, final double yCenter, final double mass)
	{
		this.xCenter = xCenter;
		this.yCenter = yCenter;
		this.mass = mass;
		this.setxV(0);
		this.setyV(0);

		if (mass == 0)
		{
			this.mass = 1;
		}
	}

	public void paint(Graphics g)
	{
		double radius = this.getRadius();
		if (this.getMass() > maxMass) maxMass = this.getMass();

		g.setColor(new Color(191 - (int) (191 * (this.getMass() / maxMass)), 191 - (int) (191 * (this.getMass() / maxMass)),
				191 - (int) (191 * (this.getMass() / maxMass))));
		g.fillOval((int) (xCenter - radius), (int) (yCenter - radius), (int) (2 * radius), (int) (2 * radius));
	}

	public double getRadius()
	{
		return Math.sqrt(mass / Math.PI) * (100 / (100 + Math.pow(Math.E, mass / 1000))) + (mass / 250000) + 2;
	}

	public void stepTime()
	{
		this.setxCenter(this.getxCenter() + this.getxV() * TIME_STEP);
		this.setyCenter(this.getyCenter() + this.getyV() * TIME_STEP);
	}

	public double getMass()
	{
		return this.mass;
	}

	public double getxCenter()
	{
		return this.xCenter;
	}

	public double getxV()
	{
		return this.xV;
	}

	public double getyCenter()
	{
		return this.yCenter;
	}

	public double getyV()
	{
		return this.yV;
	}

	public void setMass(final double mass)
	{
		this.mass = mass;
	}

	public void setxCenter(final double xCenter)
	{
		this.xCenter = xCenter;
	}

	public void setxV(final double xV)
	{
		this.xV = xV;
	}

	public void setyCenter(final double yCenter)
	{
		this.yCenter = yCenter;
	}

	public void setyV(final double yV)
	{
		this.yV = yV;
	}

	public void attractAll(ArrayList<Mass> masses)
	{
		for (Mass mass : masses)
		{
			if (!this.equals(mass)) this.attract(mass);
		}
	}

	public void collideAll(ArrayList<Mass> masses)
	{
		Iterator<Mass> i = masses.iterator();

		while (i.hasNext())
		{
			Mass mass = i.next();
			if (!this.equals(mass) && this.collide(mass)) if (this.getMass() > mass.getMass())
			{
				this.setMass(this.getMass() + mass.getMass());
				i.remove();
			}
		}
	}

	public boolean collide(Mass mass)
	{
		return Math.sqrt(Math.pow(this.getxCenter() - mass.getxCenter(), 2) + Math.pow(this.getyCenter() - mass.getyCenter(), 2)) < this.getRadius()
				+ mass.getRadius();
	}

	public void attract(Mass mass)
	{
		// F = G * M1 * M2 / r^2
		double force = Mass.GRAVITATIONAL_CONSTANT * (this.getMass() * mass.getMass() / Math.pow(this.distanceTo(mass), 2));
		double angle = this.angleTo(mass);

		double xForce = force * Math.cos(angle);
		double yForce = force * Math.sin(angle);

		// F = m * a

		double xAcceleration = xForce / mass.getMass();
		double yAcceleration = yForce / mass.getMass();

		double deltaXVelocity = xAcceleration * Mass.TIME_STEP;
		double deltaYVelocity = yAcceleration * Mass.TIME_STEP;

		mass.setxV(mass.getxV() + deltaXVelocity);
		mass.setyV(mass.getyV() + deltaYVelocity);

		if ((new Double(mass.getxV())).isNaN())
		{
			System.out.println(this.toString() + " " + mass.toString());
			throw new IllegalArgumentException("Oh no...");
		}
	}

	public double distanceTo(Mass mass)
	{
		return Math.sqrt(Math.pow(this.getxCenter() - mass.getxCenter(), 2) + Math.pow(this.getyCenter() - mass.getyCenter(), 2));
	}

	public double angleTo(Mass mass)
	{
		return Math.atan2(this.getyCenter() - mass.getyCenter(), this.getxCenter() - mass.getxCenter());
	}

	public String toString()
	{
		return "Mass: " + mass + " Location: (" + this.xCenter + ", " + this.yCenter + ") ";
	}
}
