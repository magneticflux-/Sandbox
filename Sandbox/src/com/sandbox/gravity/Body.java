package com.sandbox.gravity;

import java.awt.geom.Point2D;

import org.apache.commons.math3.util.FastMath;

import com.electronauts.mathutil.MathUtil;

public class Body
{
	public static final double	MASS	= 5;
	private double				x, y;
	private double				xV, yV;

	public synchronized void reactTo(Body b)
	{
		double angle = MathUtil.angleTo(this.getPoint(), b.getPoint());
		double force = FastMath.pow(Body.MASS, 2) / MathUtil.distanceSquared(this.getPoint(), b.getPoint());
		this.xV += FastMath.cos(angle) * force;
		this.yV += FastMath.sin(angle) * force;
	}

	public void move()
	{
		this.x += this.xV;
		this.y += this.yV;
	}

	public Point2D getPoint()
	{
		return new Point2D.Double(x, y);
	}
}
