package com.sandbox.evolve;

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashSet;
import java.util.Set;

import com.sandbox.neural.FeedforwardNetwork;

public class Arena
{
	public class Fighter extends Projectile
	{
		private static final double			radius		= 25;
		private double						angle;
		private final FeedforwardNetwork	brain;
		private boolean						isShooting	= false;
		private final Color					team		= new Color((int) (Math.random() * 256), (int) (Math.random() * 256), (int) (Math.random() * 256));

		public Fighter(final FeedforwardNetwork brain, double x, double y)
		{
			this(brain, x, y, 0);
		}

		public Fighter(final FeedforwardNetwork brain, double x, double y, double angle)
		{
			super(x, y);

			if (!(Integer.parseInt(brain.getLayout().substring(0, 1)) >= 0 && Integer.parseInt(brain.getLayout().substring(brain.getLayout().length() - 1,
					brain.getLayout().length())) >= 0)) { throw new IllegalArgumentException("Insufficient brain in/out space."); }

			this.angle = angle;
			this.brain = brain;
		}

		@Override
		public void paint(final Graphics g)
		{
			if (this.isShooting)
			{
				g.setColor(Color.RED);
				g.fillOval((int) (this.x - Fighter.radius - 2), (int) (this.y - Fighter.radius - 2), (int) (2 * Fighter.radius + 4),
						(int) (2 * Fighter.radius + 4));
			}

			g.setColor(this.team);
			g.fillOval((int) (this.x - Fighter.radius), (int) (this.y - Fighter.radius), (int) (2 * Fighter.radius), (int) (2 * Fighter.radius));
		}

		public void reactTo(final double[] enviroment)
		{
			final double[] reaction = this.brain.evaluate(enviroment);
			this.xV = reaction[0] * 5;
			this.yV = reaction[1] * 5;
			this.isShooting = reaction[2] > 0;
		}
	}

	public class Projectile
	{
		private static final double	radius	= 10;
		double						x, y, xV, yV;	// Pixels per second

		public Projectile(double x, double y)
		{
			this(x, y, 0, 0);
		}

		public Projectile(double x, double y, double xV, double yV)
		{
			this.x = x;
			this.y = y;
			this.xV = xV;
			this.yV = yV;
		}

		public Projectile()
		{
			this(0, 0, 0, 0);
		}

		public double getX()
		{
			return this.x;
		}

		public double getY()
		{
			return this.y;
		}

		public void paint(final Graphics g)
		{
			g.setColor(Color.GRAY);
			g.fillOval((int) (this.x - Projectile.radius), (int) (this.y - Projectile.radius), (int) (2 * Projectile.radius), (int) (2 * Projectile.radius));
		}

		public void setLocation(final double x, final double y)
		{
			this.x = x;
			this.y = y;
		}

		public void updatePosition()
		{
			this.x += this.xV;
			this.y += this.yV;
		}
	}

	private final HashSet<Fighter>		fighters;

	private final HashSet<Projectile>	projectiles;

	public Arena()
	{
		this.fighters = new HashSet<Fighter>();
		this.projectiles = new HashSet<Projectile>();
	}

	public Set<Fighter> getFighters()
	{
		return this.fighters;
	}

	public Set<Projectile> getProjectiles()
	{
		return this.projectiles;
	}
}