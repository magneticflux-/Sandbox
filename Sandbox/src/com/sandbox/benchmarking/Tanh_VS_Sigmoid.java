package com.sandbox.benchmarking;

import org.apache.commons.math3.util.FastMath;

import com.sandbox.neural.NeuronNode;

public class Tanh_VS_Sigmoid
{
	public static void main(String[] args)
	{
		final double increment = 0.1;
		long startTime = System.nanoTime();

		for (double d = -50; d < 50; d += increment)
		{
			for (int i = 0; i < 1000; i++)
				FastMath.tanh(d);
		}

		long tanhTime = System.nanoTime() - startTime;
		System.out.println("Tanh time: " + tanhTime);
		startTime = System.nanoTime();

		for (double d = -50; d < 50; d += increment)
		{
			for (int i = 0; i < 1000; i++)
				NeuronNode.sigmoid(d);
		}

		long sigmoidTime = System.nanoTime() - startTime;
		System.out.println("Sigmoid time: " + sigmoidTime);

		System.out.println("Sigmoid is " + ((double) tanhTime / sigmoidTime) + "x faster.");

		long total = 0;
		long failed = 0;

		for (float f = -20; f < 20; f = FastMath.nextUp(f))
		{
			total++;
			if ((NeuronNode.sigmoid(f) - FastMath.tanh(f)) != 0)
			{
				failed++;
				// System.out.println("Difference at " + f + ": " + (NeuronNode.sigmoid(f) - FastMath.tanh(f)));
				System.out.println((100d * failed / total) + "% at " + f);
			}
		}
	}
}
