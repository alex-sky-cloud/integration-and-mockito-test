package com.javabydeveloper.util.groups;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

//@Execution(ExecutionMode.CONCURRENT)
@Tag("group2")
public class Parallel2_Test {

	private int time_out = 2000;
	
	@Test
	void test2A() throws InterruptedException {
		Thread.sleep(time_out);
		System.out.println(Thread.currentThread().getName()+" => test2A");
	}
	
	@Test
	void test2B() throws InterruptedException {
		Thread.sleep(time_out);
		System.out.println(Thread.currentThread().getName()+" => test2B");
	}

	@Test
	void test2C() throws InterruptedException {
		Thread.sleep(time_out);
		System.out.println(Thread.currentThread().getName()+" => test2C");
	}
}
