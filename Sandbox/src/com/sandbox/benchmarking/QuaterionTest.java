package com.sandbox.benchmarking;

import org.apache.commons.math3.complex.Quaternion;

public class QuaterionTest {
	public static void main(String[] args) {
		Quaternion q1 = new Quaternion(1, 1, 1, 1);

		for (int a = 0; a < 2; a++)
			for (int b = 0; b < 2; b++)
				for (int c = 0; c < 2; c++)
					for (int d = 0; d < 2; d++)
						for (int a1 = 0; a1 < 2; a1++)
							for (int b1 = 0; b1 < 2; b1++)
								for (int c1 = 0; c1 < 2; c1++)
									for (int d1 = 0; d1 < 2; d1++)
										System.out.println(new Quaternion(a, b, c, d) + " * " + new Quaternion(a1, b1, c1, d1) + " = " + new Quaternion(a, b, c, d).multiply(new Quaternion(a1, b1, c1, d1)));
	}
}
