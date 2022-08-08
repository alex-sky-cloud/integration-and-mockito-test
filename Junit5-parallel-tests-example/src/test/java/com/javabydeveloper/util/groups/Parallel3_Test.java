package com.javabydeveloper.util.groups;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

//@Execution(ExecutionMode.CONCURRENT)
@Tag("group3")
public class Parallel3_Test {

	private int time_out = 2000;
	
	@Test
	void test3A() throws InterruptedException {
		Thread.sleep(time_out);
		System.out.println(Thread.currentThread().getName()+" => test3A");
	}
	
	@Test
	void test3B() throws InterruptedException {
		Thread.sleep(time_out);
		System.out.println(Thread.currentThread().getName()+" => test3B");
	}

	@Test
	void test3C() throws InterruptedException {
		Thread.sleep(time_out);
		System.out.println(Thread.currentThread().getName()+" => test3C");
	}
}
