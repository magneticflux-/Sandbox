package com.sandbox.gravity;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Iterator;

import com.electronauts.mathutil.MathUtil;
import com.electronauts.mathutil.PolarPoint;

public class Mass
{
	public static final double	EXPLOSION_CONSTANT		= 0.0000001;
	public static final double	GRAVITATIONAL_CONSTANT	= 6.673848 * Math.pow(10, -11);
	public static double		timeStep				= 1 / 500d;

	public static double		maxMass					= 0;

	public static void decrementTimeStep()
	{
		if (Mass.timeStep > 1) Mass.timeStep = 1 / (1 / Mass.timeStep - 1);
	}

	public static void incrementTimeStep()
	{
		Mass.timeStep = 1 / (1 / Mass.timeStep + 1);
	}

	public static void setTimeStep(final double timeStep)
	{
		Mass.timeStep = timeStep;
	}

	private double	mass;

	private double	xCenter, yCenter;

	private double	xV, yV;

	public Mass(final double xCenter, final double yCenter, final double mass)
	{
		this.xCenter = xCenter;
		this.yCenter = yCenter;
		this.mass = mass;
		this.setxV(0);
		this.setyV(0);

		if (mass == 0) this.mass = 1;
	}

	public double angleTo(final Mass mass)
	{
		return Math.atan2(this.getyCenter() - mass.getyCenter(), this.getxCenter() - mass.getxCenter());
	}

	public void attract(final Mass mass)
	{
		// F = G * M1 * M2 / r^2
		final double force = Mass.GRAVITATIONAL_CONSTANT * (this.getMass() * mass.getMass() / Math.pow(this.distanceTo(mass), 2));
		final double angle = this.angleTo(mass);

		final double xForce = force * Math.cos(angle);
		final double yForce = force * Math.sin(angle);

		// F = m * a

		final double xAcceleration = xForce / mass.getMass();
		final double yAcceleration = yForce / mass.getMass();

		final double deltaXVelocity = xAcceleration * Mass.timeStep;
		final double deltaYVelocity = yAcceleration * Mass.timeStep;

		mass.setxV(mass.getxV() + deltaXVelocity);
		mass.setyV(mass.getyV() + deltaYVelocity);

		if (!(MathUtil.isFinite(mass.getyV()) || MathUtil.isFinite(mass.getxV()) || MathUtil.isFinite(mass.getxCenter()) || MathUtil
				.isFinite(mass.getyCenter())))
		{
			System.out.println(this.toString() + " " + mass.toString());
			mass.reset();
		}
	}

	public void attractAll(final ArrayList<Mass> masses)
	{
		for (final Mass mass : masses)
			if (!this.equals(mass)) this.attract(mass);
	}

	public void collideAll(final ArrayList<Mass> masses)
	{
		final Iterator<Mass> i = masses.iterator();

		while (i.hasNext())
		{
			final Mass mass = i.next();
			if (!this.equals(mass) && this.collides(mass))
				if (this.getMass() > mass.getMass())
				{
					this.setxV((this.getxV() * this.getMass() + mass.getxV() * mass.getMass()) / (this.getMass() + mass.getMass()));
					this.setyV((this.getyV() * this.getMass() + mass.getyV() * mass.getMass()) / (this.getMass() + mass.getMass()));
					final double strength = Math.sqrt(Math.pow(this.getMass(), 2) + Math.pow(mass.getMass(), 2))
							* Math.sqrt(Math.pow(this.getVelocity(), 2) + Math.pow(mass.getVelocity(), 2));
					this.setMass(this.getMass() + mass.getMass());
					i.remove();
					this.repellAll(masses, strength);
				}
		}
	}

	public boolean collides(final Mass mass)
	{
		return Math.sqrt(Math.pow(this.getxCenter() - mass.getxCenter(), 2) + Math.pow(this.getyCenter() - mass.getyCenter(), 2)) < this.getRadius()
				+ mass.getRadius();
	}

	public double distanceTo(final Mass mass)
	{
		return Math.sqrt(Math.pow(this.getxCenter() - mass.getxCenter(), 2) + Math.pow(this.getyCenter() - mass.getyCenter(), 2));
	}

	public double getMass()
	{
		return this.mass;
	}

	public double getRadius()
	{
		return Math.sqrt(this.mass / Math.PI) * (100 / (100 + Math.pow(Math.E, this.mass / 1000))) + this.mass / 250000 + 2;
	}

	public double getVelocity()
	{
		return Math.sqrt(Math.pow(this.xV, 2) + Math.pow(this.yV, 2));
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

	public void paint(final Graphics g)
	{
		final double radius = this.getRadius();
		boolean hold = false;
		if (this.getMass() >= Mass.maxMass)
		{
			Mass.maxMass = this.getMass();
			if (hold)
			{
				this.setxV(0);
				this.setyV(0);
			}
		}

		g.setColor(new Color(191 - (int) (191 * (this.getMass() / Mass.maxMass)), 191 - (int) (191 * (this.getMass() / Mass.maxMass)), 191 - (int) (191 * (this
				.getMass() / Mass.maxMass))));
		g.fillOval((int) (this.xCenter - radius), (int) (this.yCenter - radius), (int) (2 * radius), (int) (2 * radius));

		final PolarPoint p = new PolarPoint(100000 * Math.sqrt(Math.pow(this.getxV(), 2) + Math.pow(this.getyV(), 2)), Math.atan2(this.getyV(), this.getxV()));
		g.setColor(Color.RED);
		g.drawLine((int) this.getxCenter(), (int) this.getyCenter(), (int) (p.getX() + this.getxCenter()), (int) (p.getY() + this.getyCenter()));
	}

	public void repell(final Mass mass, final double strength)
	{
		// F = G * M1 * M2 / r^2
		final double force = -Mass.EXPLOSION_CONSTANT * strength * (this.getMass() * mass.getMass() / Math.pow(this.distanceTo(mass), 1));
		final double angle = this.angleTo(mass);

		final double xForce = force * Math.cos(angle);
		final double yForce = force * Math.sin(angle);

		// F = m * a

		final double xAcceleration = xForce / mass.getMass();
		final double yAcceleration = yForce / mass.getMass();

		final double deltaXVelocity = xAcceleration * Mass.timeStep;
		final double deltaYVelocity = yAcceleration * Mass.timeStep;

		mass.setxV(mass.getxV() + deltaXVelocity);
		mass.setyV(mass.getyV() + deltaYVelocity);

		if (!(MathUtil.isFinite(mass.getyV()) || MathUtil.isFinite(mass.getxV()) || MathUtil.isFinite(mass.getxCenter()) || MathUtil
				.isFinite(mass.getyCenter())))
		{
			System.out.println(this.toString() + " " + mass.toString());
			mass.reset();
		}
	}

	public void repellAll(final ArrayList<Mass> masses, final double strength)
	{
		for (final Mass mass : masses)
			if (!this.equals(mass)) this.repell(mass, strength);
	}

	public void reset()
	{
		this.mass = 100;
		this.xCenter = 100;
		this.yCenter = 100;
		this.xV = 0;
		this.yV = 0;
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

	public void stepTime()
	{
		this.setxCenter(this.getxCenter() + this.getxV() * Mass.timeStep);
		this.setyCenter(this.getyCenter() + this.getyV() * Mass.timeStep);
	}

	@Override
	public String toString()
	{
		return "Mass: " + this.mass + " Location: (" + this.xCenter + ", " + this.yCenter + ") ";
	}
}
