package com.sandbox.gravity;

import org.apache.commons.math3.util.FastMath;
import org.electronauts.mathutil.MathUtil;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.Paint;
import java.awt.geom.Point2D;
import java.util.HashMap;

public class Body {
	private double mass;
	private double x, y;
	private double xV, yV;
	private Universe universe;
	private XYSeries speedLog;
	private XYSeries positionLog;
	private Paint paint;
	private HashMap<Body, Double> potentialEnergy = new HashMap<Body, Double>();

	public Body(double x, double y, double xV, double yV, XYSeriesCollection logs, XYItemRenderer renderer) {
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

	public Paint getPaint() {
		return this.paint;
	}

	public synchronized void reactTo(Body b) {
		double angle = MathUtil.angleTo(this.getPoint(), b.getPoint());
		double acceleration = this.universe.getGravConst() * this.mass * b.getMass() / MathUtil.distanceSquared(this.getPoint(), b.getPoint());
		double xA = FastMath.cos(angle) * acceleration * this.universe.getDeltaTime() / this.mass;
		double yA = FastMath.sin(angle) * acceleration * this.universe.getDeltaTime() / this.mass;
		this.potentialEnergy.put(b, acceleration * MathUtil.distance(this.getPoint(), b.getPoint()));
		this.xV += xA;
		this.yV += yA;
	}

	public Point2D getPoint() {
		return new Point2D.Double(x, y);
	}

	public double getMass() {
		return this.mass;
	}

	public double getTotalPotentialEnergy() {
		double totalEnergy = 0;
		for (double value : this.potentialEnergy.values()) {
			totalEnergy += value;
		}
		System.out.println(totalEnergy);
		return totalEnergy;
	}

	public double getVelocity() {
		return FastMath.sqrt(FastMath.pow(this.xV, 2) + FastMath.pow(this.yV, 2));
	}

	public void setUniverse(Universe u) {
		this.universe = u;
	}

	public void move() {
		this.x += this.xV * this.universe.getDeltaTime();
		this.y += this.yV * this.universe.getDeltaTime();
		if (this.universe.getTick() % 100 == 0) {
			this.speedLog.add(this.universe.getTick(), FastMath.sqrt(FastMath.pow(this.xV, 2) + FastMath.pow(this.yV, 2)));
			// this.positionLog.add(this.x, this.y);
		}
	}
}
