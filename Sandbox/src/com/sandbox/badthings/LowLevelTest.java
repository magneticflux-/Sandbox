package com.sandbox.badthings;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Random;

import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public class LowLevelTest
{
	public static void main(final String[] args) throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException,
	IllegalAccessException, InvocationTargetException
	{
		final Constructor<Unsafe> unsafeConstructor = Unsafe.class.getDeclaredConstructor();
		unsafeConstructor.setAccessible(true);
		final Unsafe unsafe = unsafeConstructor.newInstance();

		final long start = unsafe.allocateMemory(1);
		System.out.println("Start = " + start);
		final Random r = new Random();
		for (long i = start; i > 0; i += 1) // 27500000
		{
			System.out.println("Memory at location " + i + " contains " + unsafe.getBoolean(null, i));
			unsafe.putBoolean(null, i, r.nextBoolean());
		}
	}
}
