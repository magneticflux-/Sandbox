package com.sandbox.neural;

import java.util.Arrays;


public class Run
{
	public static void main(String[] args)
	{
		InputValue var1 = new InputValue(3d);
		InputValue var2 = new InputValue(4d);
		Population p = new Population(100000, "3 3 1", new InputValue[] { var1, var2 }, new double[] { var1.getOutput() * var2.getOutput() });
		//p.viewStatus();
		Network[] victors = p.select(5);
		System.out.println(Arrays.toString(victors));

		if (Config.DEBUG)
		{
			System.out.println("---DEBUG---");

			Network n1 = new Network("2 2 1", new InputValue[] { var1, var2 });
			n1.randomizeWeights(-1, 1);
			n1.randomizeBiases(-2, 2);
			Network n2 = new Network("2 2 1", new InputValue[] { var1, var2 });
			n2.randomizeWeights(-1, 1);
			n2.randomizeBiases(-2, 2);

			n1.evalNet();
			n1.inspectNet();
			n2.evalNet();
			n2.inspectNet();

			System.out.println("Offspring:");
			Network[] offspring = n1.breed(n2);

			offspring[0].evalNet();
			offspring[0].inspectNet();
			offspring[1].evalNet();
			offspring[1].inspectNet();
		}
	}
}
