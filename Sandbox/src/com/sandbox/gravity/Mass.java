package com.sandbox.gravity;

import org.apache.commons.math3.util.FastMath;
import org.electronauts.mathutil.MathUtil;
import org.electronauts.mathutil.PolarPoint;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;

public class Mass {
	public static final double DENSITY_CONSTANT = 5;
	public static final double EXPLOSION_CONSTANT = FastMath.pow(10, -11);
	public static final double GRAVITATIONAL_CONSTANT = 6.673848 * FastMath.pow(10, -11);
	public static final double TIME_SHIFT = 100;
	public static double maxMass = 0;
	public static double timeStep = 1 / 500d;
	private double mass;
	private double xCenter, yCenter;
	private double xV, yV;

	public Mass(final double xCenter, final double yCenter, final double mass) {
		this.xCenter = xCenter;
		this.yCenter = yCenter;
		this.mass = mass;
		this.xV = 0;
		this.yV = 0;

		if (mass == 0)
			this.mass = 1;
	}

	public static void setTimeStep(final double timeStep) {
		Mass.timeStep = timeStep;
	}

	public void attractAll(final ArrayList<Mass> masses) {
		for (final Mass mass : masses)
			if (!this.equals(mass))
				this.attract(mass);
	}

	public synchronized void attract(final Mass mass) {
		final double force = Mass.GRAVITATIONAL_CONSTANT * (this.getMass() * mass.getMass() / this.distanceToSquared(mass));
		final double angle = this.angleTo(mass);

		mass.setxV(mass.getxV() + ((force * FastMath.cos(angle)) / mass.getMass()) * Mass.timeStep);
		mass.setyV(mass.getyV() + ((force * FastMath.sin(angle)) / mass.getMass()) * Mass.timeStep);

		if (!(MathUtil.isFinite(mass.getyV()) || MathUtil.isFinite(mass.getxV()) || MathUtil.isFinite(mass.getxCenter()) || MathUtil.isFinite(mass.getyCenter()))) {
			System.out.println(this.toString() + " " + mass.toString());
			mass.reset();
		}
	}

	public double distanceToSquared(Mass m) {
		return FastMath.pow(this.getxCenter() - m.getxCenter(), 2) + FastMath.pow(this.getyCenter() - m.getyCenter(), 2);
	}

	public double getMass() {
		return this.mass;
	}

	public double angleTo(final Mass mass) {
		return FastMath.atan2(this.getyCenter() - mass.getyCenter(), this.getxCenter() - mass.getxCenter());
	}

	public double getxV() {
		return this.xV;
	}

	public double getyV() {
		return this.yV;
	}

	public double getxCenter() {
		return this.xCenter;
	}

	public double getyCenter() {
		return this.yCenter;
	}

	public void reset() {
		this.mass = 100;
		this.xCenter = 100;
		this.yCenter = 100;
		this.xV = 0;
		this.yV = 0;
	}

	public void setyCenter(final double yCenter) {
		this.yCenter = yCenter;
	}

	public void setxCenter(final double xCenter) {
		this.xCenter = xCenter;
	}

	public void setyV(final double yV) {
		this.yV = yV;
	}

	public void setxV(final double xV) {
		this.xV = xV;
	}

	public void setMass(final double mass) {
		this.mass = mass;
	}

	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
		/*
		final Mass m = new Mass(this.xCenter, this.yCenter, this.mass);
		m.setxV(this.xV);
		m.setyV(this.yV);
		return m;*/
	}

	@Override
	public String toString() {
		return "Mass: " + this.mass + " Location: (" + this.xCenter + ", " + this.yCenter + ") ";
	}

	public void collideAll(final ArrayList<Mass> masses) {
		final Iterator<Mass> i = masses.iterator();

		while (i.hasNext()) {
			final Mass mass = i.next();
			if (this != mass && this.collides(mass))
				if (this.getMass() >= mass.getMass()) {
					this.setxV((this.getxV() * this.getMass() + mass.getxV() * mass.getMass()) / (this.getMass() + mass.getMass()));
					this.setyV((this.getyV() * this.getMass() + mass.getyV() * mass.getMass()) / (this.getMass() + mass.getMass()));
					final double strength = FastMath.sqrt(FastMath.pow(this.getMass(), 2) + FastMath.pow(mass.getMass(), 2)) * FastMath.sqrt(FastMath.pow(this.getVelocity(), 2) + FastMath.pow(mass.getVelocity(), 2));
					this.setMass(this.getMass() + mass.getMass() - FastMath.random() * 3 * mass.getMass() / 10);
					i.remove();
					this.repellAll(masses, strength);
				}
		}
	}

	public boolean collides(final Mass mass) {
		return this.distanceToSquared(mass) < FastMath.pow(this.getRadius() + mass.getRadius(), 2);
	}

	public double getVelocity() {
		return FastMath.sqrt(FastMath.pow(this.xV, 2) + FastMath.pow(this.yV, 2));
	}

	public void repellAll(final ArrayList<Mass> masses, final double strength) {
		for (final Mass mass : masses)
			if (!this.equals(mass))
				this.repell(mass, strength);
	}

	public double getRadius() {
		// return FastMath.sqrt(this.mass / (FastMath.PI * Mass.DENSITY_CONSTANT)) * (100 / (100 + FastMath.pow(FastMath.E, this.mass / 1000))) + this.mass
		// / 250000 + 1;
		return Mass.DENSITY_CONSTANT * FastMath.log1p(this.mass) / 6;
	}

	public void repell(final Mass mass, final double strength) {
		// F = G * M1 * M2 / r^2
		final double force = -Mass.EXPLOSION_CONSTANT * strength * (this.getMass() * mass.getMass() / FastMath.pow(this.distanceTo(mass), 3 / 2d));
		final double angle = this.angleTo(mass);

		final double xForce = force * FastMath.cos(angle);
		final double yForce = force * FastMath.sin(angle);

		// F = m * a

		final double xAcceleration = xForce / mass.getMass();
		final double yAcceleration = yForce / mass.getMass();

		final double deltaXVelocity = xAcceleration * Mass.timeStep;
		final double deltaYVelocity = yAcceleration * Mass.timeStep;

		mass.setxV(mass.getxV() + deltaXVelocity);
		mass.setyV(mass.getyV() + deltaYVelocity);

		if (!(MathUtil.isFinite(mass.getyV()) || MathUtil.isFinite(mass.getxV()) || MathUtil.isFinite(mass.getxCenter()) || MathUtil.isFinite(mass.getyCenter()))) {
			System.out.println(this.toString() + " " + mass.toString());
			mass.reset();
		}
	}

	public double distanceTo(final Mass mass) {
		return FastMath.sqrt(FastMath.pow(this.getxCenter() - mass.getxCenter(), 2) + FastMath.pow(this.getyCenter() - mass.getyCenter(), 2));
	}

	public void paint(final Graphics2D g) {
		final double radius = this.getRadius();
		final boolean hold = false;
		if (this.getMass() >= Mass.maxMass) {
			Mass.maxMass = this.getMass();
			if (hold) {
				this.setxV(0);
				this.setyV(0);
			}
		}

		g.setColor(new Color(191 - (int) (191 * (this.getMass() / Mass.maxMass)), 191 - (int) (191 * (this.getMass() / Mass.maxMass)), 191 - (int) (191 * (this.getMass() / Mass.maxMass))));
		g.fillOval((int) (this.xCenter - radius), (int) (this.yCenter - radius), (int) (2 * radius), (int) (2 * radius));

		final PolarPoint p = new PolarPoint(100000 * FastMath.sqrt(FastMath.pow(this.getxV(), 2) + FastMath.pow(this.getyV(), 2)), FastMath.atan2(this.getyV(), this.getxV()));
		g.setColor(Color.RED);
		g.drawLine((int) this.getxCenter(), (int) this.getyCenter(), (int) (p.getX() + this.getxCenter()), (int) (p.getY() + this.getyCenter()));

		if (this.getMass() > 6500)
			this.paintTarget(String.format("Mass: %06.0f", this.mass), g);
	}

	public void paintTarget(final String name, final Graphics2D g) {
		final double radius = 10;
		final double scale = FastMath.sqrt(2) / 2;
		g.drawOval((int) (this.getxCenter() - radius), (int) (this.getyCenter() - radius), (int) (radius * 2), (int) (radius * 2));
		g.drawLine((int) this.getxCenter(), (int) (this.getyCenter() + radius / 2), (int) this.getxCenter(), (int) (this.getyCenter() + 3 * radius / 2));
		g.drawLine((int) this.getxCenter(), (int) (this.getyCenter() - radius / 2), (int) this.getxCenter(), (int) (this.getyCenter() - 3 * radius / 2));
		g.drawLine((int) (this.getxCenter() + radius / 2), (int) this.getyCenter(), (int) (this.getxCenter() + 3 * radius / 2), (int) this.getyCenter());
		g.drawLine((int) (this.getxCenter() - radius / 2), (int) this.getyCenter(), (int) (this.getxCenter() - 3 * radius / 2), (int) this.getyCenter());
		g.drawLine((int) (this.getxCenter() + scale * radius), (int) (this.getyCenter() + scale * radius), (int) (this.getxCenter() + scale * 3 * radius / 2), (int) (this.getyCenter() + scale * 3 * radius / 2));
		final Font restore = g.getFont();
		final Font f = new Font("Sans-Serif", Font.PLAIN, -15);
		g.setFont(f);

		g.translate(g.getClipBounds().getWidth() / 2, 0);
		g.scale(-1, 1);
		g.translate(-g.getClipBounds().getWidth() / 2, 0);
		g.drawString(name, (int) (g.getClipBounds().getWidth() - (this.getxCenter() + scale * 3 * radius / 2)), (int) (this.getyCenter() + scale * 3 * radius / 2));
		g.translate(g.getClipBounds().getWidth() / 2, 0);
		g.scale(-1, 1);
		g.translate(-g.getClipBounds().getWidth() / 2, 0);

		g.setFont(restore);
	}

	public void stepTime() {
		this.setxCenter(this.getxCenter() + this.getxV() * Mass.timeStep);
		this.setyCenter(this.getyCenter() + this.getyV() * Mass.timeStep);
	}
}
