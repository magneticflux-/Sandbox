package com.sandbox.neural;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.TreeMap;

public class Population
{
	ArrayList<Network>	networks;
	double[]			actual;
	Random				r;

	public Population(int size, String layout, InputValue[] input, double[] actual, Random r)
	{
		this.r = r;
		networks = new ArrayList<Network>(size);
		this.actual = actual;

		for (int i = 0; i < size; i++)
		{
			networks.add(new Network(layout, input, r));
		}
	}

	public void viewStatus()
	{
		this.updateFitness();
		for (Network n : networks)
		{
			n.evalNet();
			System.out.println("Fitness of network: " + n.getFitness());
			System.out.println();
		}
	}

	public static double compareOutput(double[] output, double[] actual)
	{
		double difference = 0;

		for (int i = 0; i < output.length; i++)
		{
			difference += Math.abs(output[i] - actual[i]);
		}

		return 1 / (0.001 + difference);
	}

	public void updateFitness()
	{
		double[][] cases = new double[Config.FITNESS_SAMPLE_SIZE][2];
		double[][] solutions = new double[Config.FITNESS_SAMPLE_SIZE][1];
		for (int i = 0; i < cases.length; i++)
		{
			cases[i] = new double[] { Config.FITNESS_SAMPLE_RANGE * (r.nextDouble() - 0.5), Config.FITNESS_SAMPLE_RANGE * (r.nextDouble() - 0.5) };
			solutions[i] = new double[] { cases[i][0] * cases[i][1] };
		}

		for (Network n : networks)
		{
			double sum = 0;
			for (int i = 0; i < cases.length; i++)
			{
				n.setEnvironment(cases[i]);
				sum += Population.compareOutput(n.getOutputs(), solutions[i]);
			}
			n.setFitness(sum);
		}
	}

	public Network[] select(int num)
	{
		this.updateFitness();
		Network[] victors = new Network[num];

		double sum = 0;
		TreeMap<Double, Network> wheel = new TreeMap<Double, Network>();

		for (Network n : networks)
		{
			wheel.put(sum, n);
			sum += n.getFitness();
		}

		for (int i = 0; i < victors.length; i++)
		{
			victors[i] = wheel.floorEntry(r.nextDouble() * sum).getValue();
		}

		return victors;
	}

	public Network[] selectBest(int num)
	{
		this.updateFitness();
		Network[] victors = new Network[num];

		ArrayList<Network> sorted = new ArrayList<Network>(networks);
		Collections.sort(sorted, new Comparator<Network>()
		{
			@Override
			public int compare(Network o1, Network o2)
			{
				return -(new Double(o1.getFitness())).compareTo(new Double(o2.getFitness()));
			}
		});

		for (int i = 0; i < victors.length; i++)
		{
			victors[i] = sorted.get(i);
		}

		return victors;
	}
}
