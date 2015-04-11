package com.sandbox.neural;

import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Network
{
	InputValue[]	environment;
	Neuron[][]		neurons;
	String			layout;
	Random			r	= new Random();

	@Override
	public String toString()
	{
		return "Network output: " + Arrays.toString(this.getOutputs());
	}

	public Network(String layout, InputValue[] inputLink)
	// Example "2 2 1" would create a neural net with 2 inputs, two layers of two neurons each, and then a final neuron.
	{
		this.layout = layout;
		this.environment = inputLink;

		Scanner s = new Scanner(layout);
		neurons = new Neuron[(layout.length() + 1) / 2][]; // Makes the array the number of layers there will be.

		int o = 0;
		while (s.hasNext())
		{// For each layer
			int layerSize = Integer.parseInt(s.next()); //Get the size of the layer
			neurons[o] = new Neuron[layerSize]; // And set it
			if (o != 0) // If the layer isn't first, set the neuron to have inputs of the neurons above
				for (int i = 0; i < neurons[o].length; i++)
				{
					neurons[o][i] = new Neuron(Arrays.asList((HasOutput[]) neurons[o - 1]));
					neurons[o][i].randomizeWeights(-2, 2);
					neurons[o][i].randomizeBias(-10, 10);
				}
			else // If the layer is first, set each Neuron's inputs to be the Network's environment
			{
				for (int i = 0; i < neurons[o].length; i++)
				{
					neurons[o][i] = new Neuron(Arrays.asList((HasOutput[]) environment));
					neurons[o][i].randomizeWeights(-2, 2);
					neurons[o][i].randomizeBias(-10, 10);
				}
			}
			o++;
		}
		s.close();

		if (Config.DEBUG)
		{
			System.out.println(neurons.length + " layers created.");
			for (int i = 0; i < neurons.length; i++)
			{
				System.out.println("  " + neurons[i].length + " neurons created on layer " + i);
				for (int j = 0; j < neurons[i].length; j++)
				{
					System.out.println("    Neuron " + neurons[i][j]);
				}
			}
		}
	}

	public void setEnvironment(double[] input) // Sets the environment while preserving the links in the top layer Neurons
	{
		for (int i = 0; i < input.length; i++)
		{
			this.environment[i].setValue(input[i]);
		}
	}

	public void evalNet()
	{
		System.out.println("Input cases: " + neurons[0][0].getInputs());
		System.out.println("Total actual: " + Arrays.toString(this.getOutputs()));
	}

	public void inspectNet()
	{
		for (int layer = 0; layer < this.neurons.length; layer++)
		{
			System.out.println("  Layer " + layer + ":");
			for (int neuron = 0; neuron < this.neurons[layer].length; neuron++)
			{
				System.out.println("    Neuron " + neuron + " has weights of " + this.neurons[layer][neuron].getWeights() + " and an output bias of "
						+ this.neurons[layer][neuron].getBias());
			}
		}
	}

	public double[] getOutputs()
	{
		double[] output = new double[neurons[neurons.length - 1].length]; // Size of last layer

		for (int i = 0; i < neurons[neurons.length - 1].length; i++)
		{
			output[i] = neurons[neurons.length - 1][i].getOutput();
		}

		return output;
	}

	public Network[] breed(Network mate)
	{
		Network[] n = new Network[] { new Network(this.layout, this.environment), new Network(this.layout, this.environment) };

		for (int layer = 0; layer < this.neurons.length; layer++)
		{ // For each layer
			for (int neuron = 0; neuron < this.neurons[layer].length; neuron++)
			{ // For each neuron in each layer
				int crossoverPoint = r.nextInt(this.neurons[layer][neuron].getWeights().size());
				for (int weight = 0; weight < crossoverPoint; weight++)
				{
					n[0].neurons[layer][neuron].setWeight(weight, this.neurons[layer][neuron].getWeight(weight)); // Passed from the father
					n[1].neurons[layer][neuron].setWeight(weight, mate.neurons[layer][neuron].getWeight(weight)); // Passed from the mother
				}
				for (int weight = crossoverPoint; weight < this.neurons[layer][neuron].getWeights().size(); weight++)
				{
					n[0].neurons[layer][neuron].setWeight(weight, mate.neurons[layer][neuron].getWeight(weight)); // Passed from the mother
					n[1].neurons[layer][neuron].setWeight(weight, this.neurons[layer][neuron].getWeight(weight)); // Passed from the father
				}
				double skew = r.nextDouble();

				n[0].neurons[layer][neuron].setWeight(crossoverPoint, (skew) * this.neurons[layer][neuron].getWeight(crossoverPoint) + (1 - skew)
						* mate.neurons[layer][neuron].getWeight(crossoverPoint));
				n[1].neurons[layer][neuron].setWeight(crossoverPoint, (1 - skew) * this.neurons[layer][neuron].getWeight(crossoverPoint) + (skew)
						* mate.neurons[layer][neuron].getWeight(crossoverPoint));

				n[0].neurons[layer][neuron].setBias((skew) * this.neurons[layer][neuron].getBias() + (1 - skew) * mate.neurons[layer][neuron].getBias());
				n[1].neurons[layer][neuron].setBias((1 - skew) * this.neurons[layer][neuron].getBias() + (skew) * mate.neurons[layer][neuron].getBias());
			}
		}

		return n;
	}

	public void randomizeWeights(double min, double max)
	{
		for (int layer = 0; layer < this.neurons.length; layer++)
		{
			for (int neuron = 0; neuron < this.neurons[layer].length; neuron++)
			{
				this.neurons[layer][neuron].randomizeWeights(min, max);
			}
		}
	}

	public void randomizeBiases(double min, double max)
	{
		for (int layer = 0; layer < this.neurons.length; layer++)
		{
			for (int neuron = 0; neuron < this.neurons[layer].length; neuron++)
			{
				this.neurons[layer][neuron].randomizeBias(min, max);
			}
		}
	}
}