package com.sandbox.gravity;

import java.awt.Paint;
import java.awt.geom.Point2D;

import org.apache.commons.math3.util.FastMath;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.electronauts.mathutil.MathUtil;

public class Body
{
	private double		mass;
	private double		x, y;
	private double		xV, yV;
	private Universe	universe;
	private XYSeries	speedLog;
	private XYSeries	positionLog;
	private Paint		paint;

	public Body(double x, double y, double xV, double yV, XYSeriesCollection logs, XYItemRenderer renderer)
	{
		this.x = x;
		this.y = y;
		this.xV = xV;
		this.yV = yV;
		this.mass = 16;
		this.speedLog = new XYSeries("Body speed: " + this.toString());
		this.positionLog = new XYSeries("Body location: " + this.toString());
		logs.addSeries(this.speedLog);
		this.paint = ((AbstractRenderer) renderer).lookupSeriesPaint(logs.getSeriesCount() - 1);
		// logs.addSeries(this.positionLog);
	}

	public Paint getPaint()
	{
		return this.paint;
	}

	public synchronized void reactTo(Body b)
	{
		double angle = MathUtil.angleTo(this.getPoint(), b.getPoint());
		double force = this.universe.getGravConst() * this.getMass() * b.getMass() / MathUtil.distanceSquared(this.getPoint(), b.getPoint());
		this.xV += FastMath.cos(angle) * force * this.universe.getDeltaTime() / this.mass;
		this.yV += FastMath.sin(angle) * force * this.universe.getDeltaTime() / this.mass;
	}

	public double getMass()
	{
		return this.mass;
	}

	public void setUniverse(Universe u)
	{
		this.universe = u;
	}

	public void move()
	{
		this.x += this.xV * this.universe.getDeltaTime();
		this.y += this.yV * this.universe.getDeltaTime();
		if (this.universe.getTick() % 100 == 0)
		{
			this.speedLog.add(this.universe.getTick(), FastMath.sqrt(FastMath.pow(this.xV, 2) + FastMath.pow(this.yV, 2)));
			// this.positionLog.add(this.x, this.y);
		}
	}

	public Point2D getPoint()
	{
		return new Point2D.Double(x, y);
	}
}
