package com.rman.engine;

import java.util.Calendar;

public class Log {
	private static long startTime;
	
	public static void start() {
		startTime = System.nanoTime();
		System.out.println(getTime() + "Started application\n");
	}
	
	public static void stop() {
		long now = System.nanoTime();
		long duration = (int) Math.round((double) (now - startTime) / 1000000000.0);
		System.out.printf("\n" + getTime() + "Stopped application (Duration: %1$02d:%2$02d:%3$02d)", duration / 3600, (duration % 3600) / 60, (duration % 60));
		System.out.println();
	}
	
	public static void log(String ... text) {
		System.out.print(getTime());
		System.out.print(text[0]);
		for (int i = 1; i < text.length; i++) {
			System.out.print(" " + text[i]);
		}
		System.out.println();
	}
	
	public static void log(int ... numbers) {
		System.out.print(getTime());
		System.out.print(numbers[0]);
		for (int i = 1; i < numbers.length; i++) {
			System.out.print(" " + numbers[i]);
		}
		System.out.println();
	}
	
	public static void logError(String text) {
		System.err.println(getTime() + text);
	}
	
	public static void logError(String text, Exception e) {
		System.err.println(getTime() + text);
		e.printStackTrace();
	}
	
	private static String getTime() {
		Calendar calendar = Calendar.getInstance();
		return "[" + calendar.getTime().toString() + "] ";
	}
}
