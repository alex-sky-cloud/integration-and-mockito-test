package com.javabydeveloper.util.groups;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

//@Execution(ExecutionMode.CONCURRENT)
@Tag("group1")
public class Parallel1_Test {

	private int time_out = 2000;
	
	@Test
	void test1A() throws InterruptedException {
		Thread.sleep(time_out);
		System.out.println(Thread.currentThread().getName()+" => test1A");
	}
	
	@Test
	void test1B() throws InterruptedException {
		Thread.sleep(time_out);
		System.out.println(Thread.currentThread().getName()+" => test1B");
	}

	@Test
	void testC() throws InterruptedException {
		Thread.sleep(time_out);
		System.out.println(Thread.currentThread().getName()+" => test1C");
	}	
}
