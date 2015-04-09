package com.sandbox.neural;

import java.util.Arrays;
import java.util.Scanner;

public class Network
{
	InputValue[]	inputs;
	Neuron[][]		neurons;

	public Network(String layout, double[] input)
	// Example "2 2 1" would create a neural net with 2 inputs, two layers of two neurons each, and then a final neuron.
	{
		inputs = new InputValue[input.length];
		for (int i = 0; i < input.length; i++)
		{
			inputs[i] = new InputValue(input[i]);
		}

		Scanner s = new Scanner(layout);
		neurons = new Neuron[(layout.length() + 1) / 2][]; // Makes the array the number of layers there will be.

		int o = 0;
		while (s.hasNext())
		{
			int layerSize = Integer.parseInt(s.next());
			neurons[o] = new Neuron[layerSize];
			for (int i = 0; i < neurons[o].length; i++)
			{
				neurons[o][i] = new Neuron(Arrays.asList(neurons[o - 1]));
			}
			o++;
		}
		s.close();

		System.out.println(neurons.length + " layers.");
		for (int i = 0; i < neurons.length; i++)
		{
			System.out.println("  " + neurons[i].length + " neurons on layer " + i);
		}
	}
}