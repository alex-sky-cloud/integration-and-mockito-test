package jmh.benchmark.example.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class BenchmarkRunner {

    private static final Logger logger = LoggerFactory.getLogger(BenchmarkRunner.class);

    public static void main(String[] args) throws IOException {
        logger.info("*********************************");
        logger.info("+++++Запуск BenchmarkRunner+++++");
        logger.info("*********************************");
        org.openjdk.jmh.Main.main(args);
    }
}
