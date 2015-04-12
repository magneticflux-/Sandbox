package com.sandbox.neural;

public class Config
{
	public static final double	MUTATION_RATE				= 0.01;	// Percent chance
	public static final double	CROSSOVER_RATE				= 0.05;	// Percent chance
	public static final boolean	DEBUG						= false;
	public static final int		FITNESS_SAMPLE_SIZE			= 1000;		// Number of trials to run
	public static final double	FITNESS_SAMPLE_RANGE		= 100;		// Range of trials
	public static final double	MUTATION_SIZE				= 0.25;	// Percent change
	public static final double	RANDOM_WEIGHT_INIT_RANGE	= 2;
	public static final double	RANDOM_BIAS_INIT_RANGE		= 2;

	public static double operation(final double x, final double y)
	{
		return x > y ? 1 : -1;
	}
}
