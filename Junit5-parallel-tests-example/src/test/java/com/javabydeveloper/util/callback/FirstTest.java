package com.javabydeveloper.util.callback;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.core.io.ClassPathResource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

//@SpringBootTest(classes=com.javabydeveloper.util.callback.FirstTest.class)
@ExtendWith(TestSuiteProfilerExtension.class)
@Execution(ExecutionMode.CONCURRENT)
public class FirstTest {

    private static List<String> threadNames = Collections.synchronizedList(new ArrayList<>());


    @AfterAll
    public static void afterAllTest() {

        Assumptions.assumeTrue(FirstTest::isParallelExecutionEnable);

        long count = threadNames.stream()
                .distinct()
                .count();

        System.out.println();
        System.out.println("Количество параллельных методов : " + count);

        assertThat(count).isEqualTo(2);
    }

    private static boolean isParallelExecutionEnable() {

        String ENABLED_PARALLEL_EXECUTION = "junit.jupiter.execution.parallel.enabled";
        String PARALLELISM_FACTOR = "junit.jupiter.execution.parallel.config.fixed.parallelism";

        try {
            Properties junitConfig = new Properties();
            junitConfig.load(new ClassPathResource("junit-platform.properties").getInputStream());

            boolean enabled =
                    Boolean.valueOf(junitConfig.getProperty(ENABLED_PARALLEL_EXECUTION, "false"));

            if (!enabled) return false;

            int parallelismFactor =
                    Integer.valueOf(junitConfig.getProperty(PARALLELISM_FACTOR, "1"));

            int moreThanOneThread = 1;
            return parallelismFactor > moreThanOneThread;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Test
    void helloTest() throws InterruptedException {
        Thread.sleep(1000);
        String name = Thread.currentThread().getName();
        threadNames.add(name);
        System.out.println("Hello! " + name);
    }

    @Test
    void worldTest() throws InterruptedException {
        Thread.sleep(1000);
        String name = Thread.currentThread().getName();
        threadNames.add(name);
        System.out.println("World! " + name);
    }

}
