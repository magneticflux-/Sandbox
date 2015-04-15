package com.sandbox.neural_old;

import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Network
{
	InputValue[]	environment;
	double			fitness;
	String			layout;
	Neuron[][]		neurons;
	Random			r;

	public Network(final String layout, final InputValue[] inputLink, final Random r)
	// Example "2 2 1" would create a neural net with 2 inputs, two layers of two neurons each, and then a final neuron.
	{
		this.r = r;
		this.layout = layout;
		this.environment = inputLink;

		final Scanner s = new Scanner(layout);
		this.neurons = new Neuron[(layout.length() + 1) / 2][]; // Makes the array the number of layers there will be.

		int o = 0;
		while (s.hasNext())
		{// For each layer
			final int layerSize = Integer.parseInt(s.next()); // Get the size of the layer
			this.neurons[o] = new Neuron[layerSize]; // And set it
			if (o != 0) // If the layer isn't first, set the neuron to have inputs of the neurons above
				for (int i = 0; i < this.neurons[o].length; i++)
				{
					this.neurons[o][i] = new Neuron(Arrays.asList((HasOutput[]) this.neurons[o - 1]), r);
					this.neurons[o][i].randomizeWeights(-Config.RANDOM_WEIGHT_INIT_RANGE, Config.RANDOM_WEIGHT_INIT_RANGE);
					this.neurons[o][i].randomizeBias(-Config.RANDOM_BIAS_INIT_RANGE, Config.RANDOM_BIAS_INIT_RANGE);
				}
			else
				for (int i = 0; i < this.neurons[o].length; i++)
				{
					this.neurons[o][i] = new Neuron(Arrays.asList((HasOutput[]) this.environment), r);
					this.neurons[o][i].randomizeWeights(-Config.RANDOM_WEIGHT_INIT_RANGE, Config.RANDOM_WEIGHT_INIT_RANGE);
					this.neurons[o][i].randomizeBias(-Config.RANDOM_BIAS_INIT_RANGE, Config.RANDOM_BIAS_INIT_RANGE);
				}
			o++;
		}
		s.close();

		if (Config.DEBUG)
		{
			System.out.println(this.neurons.length + " layers created.");
			for (int i = 0; i < this.neurons.length; i++)
			{
				System.out.println("  " + this.neurons[i].length + " neurons created on layer " + i);
				for (int j = 0; j < this.neurons[i].length; j++)
					System.out.println("    Neuron " + this.neurons[i][j]);
			}
		}
	}

	public Network[] breed(final Network mate)
	{
		final Network[] n = new Network[] { new Network(this.layout, this.environment, this.r), new Network(this.layout, this.environment, this.r) };

		for (int layer = 0; layer < this.neurons.length; layer++)
			for (int neuron = 0; neuron < this.neurons[layer].length; neuron++)
			{ // For each neuron in each layer
				final int crossoverPoint = this.r.nextInt(this.neurons[layer][neuron].getWeights().size());
				for (int weight = 0; weight < crossoverPoint; weight++)
				{
					n[0].neurons[layer][neuron].setWeight(weight, this.neurons[layer][neuron].getWeight(weight)); // Passed from the father
					n[1].neurons[layer][neuron].setWeight(weight, mate.neurons[layer][neuron].getWeight(weight)); // Passed from the mother

					if (this.r.nextDouble() < Config.MUTATION_RATE) n[0].neurons[layer][neuron].mutateWeight(weight);
					if (this.r.nextDouble() < Config.MUTATION_RATE) n[1].neurons[layer][neuron].mutateWeight(weight);
				}
				for (int weight = crossoverPoint; weight < this.neurons[layer][neuron].getWeights().size(); weight++)
				{
					n[0].neurons[layer][neuron].setWeight(weight, mate.neurons[layer][neuron].getWeight(weight)); // Passed from the mother
					n[1].neurons[layer][neuron].setWeight(weight, this.neurons[layer][neuron].getWeight(weight)); // Passed from the father

					if (this.r.nextDouble() < Config.MUTATION_RATE) n[0].neurons[layer][neuron].mutateWeight(weight);
					if (this.r.nextDouble() < Config.MUTATION_RATE) n[1].neurons[layer][neuron].mutateWeight(weight);
				}
				final double skew = this.r.nextDouble();

				n[0].neurons[layer][neuron].setWeight(crossoverPoint, skew * this.neurons[layer][neuron].getWeight(crossoverPoint) + (1 - skew)
						* mate.neurons[layer][neuron].getWeight(crossoverPoint));
				n[1].neurons[layer][neuron].setWeight(crossoverPoint, (1 - skew) * this.neurons[layer][neuron].getWeight(crossoverPoint) + skew
						* mate.neurons[layer][neuron].getWeight(crossoverPoint));

				n[0].neurons[layer][neuron].setBias(skew * this.neurons[layer][neuron].getBias() + (1 - skew) * mate.neurons[layer][neuron].getBias());
				n[1].neurons[layer][neuron].setBias((1 - skew) * this.neurons[layer][neuron].getBias() + skew * mate.neurons[layer][neuron].getBias());
			}

		return n;
	}

	public void evalNet()
	{
		System.out.println("Input cases: " + this.neurons[0][0].getInputs());
		System.out.println("Total actual: " + Arrays.toString(this.getOutputs()));
	}

	public double getFitness()
	{
		return this.fitness;
	}

	public double[] getOutputs()
	{
		final double[] output = new double[this.neurons[this.neurons.length - 1].length]; // Size of last layer

		for (int i = 0; i < this.neurons[this.neurons.length - 1].length; i++)
			output[i] = this.neurons[this.neurons.length - 1][i].getOutput() * Config.NETWORK_OUTPUT_RANGE;

		return output;
	}

	public void inspectNet()
	{
		for (int layer = 0; layer < this.neurons.length; layer++)
		{
			System.out.println("  Layer " + layer + ":");
			for (int neuron = 0; neuron < this.neurons[layer].length; neuron++)
				System.out.println("    Neuron " + neuron + " has weights of " + this.neurons[layer][neuron].getWeights() + " and an output bias of "
						+ this.neurons[layer][neuron].getBias());
		}
	}

	public void randomizeBiases(final double min, final double max)
	{
		for (final Neuron[] neuron2 : this.neurons)
			for (final Neuron element : neuron2)
				element.randomizeBias(min, max);
	}

	public void randomizeWeights(final double min, final double max)
	{
		for (final Neuron[] neuron2 : this.neurons)
			for (final Neuron element : neuron2)
				element.randomizeWeights(min, max);
	}

	public void setEnvironment(final double[] input) // Sets the environment while preserving the links in the top layer Neurons
	{
		for (int i = 0; i < input.length; i++)
			this.environment[i].setValue(input[i]);
	}

	public void setFitness(final double fitness)
	{
		this.fitness = fitness;
	}

	public void setRandom(final Random r)
	{
		this.r = r;
	}

	@Override
	public String toString()
	{
		return "Network output: " + Arrays.toString(this.getOutputs());
	}
}