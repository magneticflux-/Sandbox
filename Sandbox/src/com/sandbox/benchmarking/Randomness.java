package com.sandbox.benchmarking;

import org.apache.commons.math3.fraction.BigFraction;
import org.apache.commons.math3.util.FastMath;
import org.uncommons.maths.random.MersenneTwisterRNG;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;

public class Randomness {
	public static void main(String[] args) {
		int numColumns = 1000;
		int numIterations = 10000000;

		List<Random> rand = Arrays.asList(new Random(), new SecureRandom(), new MersenneTwisterRNG());

		TreeMap<Double, Integer> map = new TreeMap<Double, Integer>();
		for (BigFraction i = BigFraction.ZERO; i.compareTo(BigFraction.ONE) < 0; i = i.add(new BigFraction(1, numColumns))) {
			map.put(i.doubleValue(), 0);
		}

		for (int i = 0; i < rand.size(); i++) {
			System.out.println(rand.get(i).getClass().getName());

			long startTime = System.nanoTime();
			for (int iter = 0; iter < numIterations; iter++) {
				Double val = rand.get(i).nextDouble();
				map.put(map.floorEntry(val).getKey(), map.floorEntry(val).getValue() + 1);
			}
			long totalTime = System.nanoTime() - startTime;

			System.out.println("Average nanoseconds per action: " + (totalTime / numIterations));

			double error = 0;
			for (Entry<Double, Integer> e : map.entrySet()) {
				error += FastMath.abs(numColumns * e.getValue() / (double) numIterations - 1);
				// System.out.println(e.getKey() + " : " + (numColumns * e.getValue() / (float) numIterations - 1));
				map.put(e.getKey(), 0);
			}
			System.out.println("Sum error: " + (error) + "\n----------------");
		}
	}
}
