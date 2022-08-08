package jmh.benchmark.example.typesbenchmarks;

import org.openjdk.jmh.annotations.*;

public class InitBenchMark {

    //@Benchmark
    @Fork(value = 1, warmups = 2)
    @Warmup( iterations = 2)
    @BenchmarkMode(Mode.Throughput)
    public void init() {
        // Do nothing

    }

}
