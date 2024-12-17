package ru.bit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.InvalidProtocolBufferException;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import ru.bit.proto.SomeClassicDTO;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Thread)
public class Main {
    private ClassicDTO classicDTO;
    private SomeClassicDTO.protoSomeClassicDTO protoSomeClassicDTO;
    private byte[] byteSomeClassicDTO;
    private byte[] byteProtoSomeClassicDTO;
    private final ObjectMapper objectMapper = new ObjectMapper();
    public static void main(String[] args) throws RunnerException {
        Options options =new OptionsBuilder()
                .include(".*Main.*")
                .forks(1)
                .build();
        new Runner(options).run();
    }
    @Setup
    public void init() throws JsonProcessingException {
        classicDTO = new ClassicDTO();
        classicDTO.setClassicFloatField(12);
        classicDTO.setClassicDoubleField(23);
        classicDTO.setClassicIntField(456);
        classicDTO.setClassicBooleanField(true);
        classicDTO.setClassicStringField("optimization");

        protoSomeClassicDTO = SomeClassicDTO.protoSomeClassicDTO.newBuilder()
                .setSomeClassicBooleanField(true)
                .setSomeClassicDoubleField(42)
                .setSomeClassicFloatField(24)
                .setSomeClassicIntField(4242)
                .setSomeClassicStringField("optimization")
                .build();
        byteSomeClassicDTO = objectMapper.writeValueAsBytes(classicDTO);
        byteProtoSomeClassicDTO = protoSomeClassicDTO.toByteArray();
    }

    @Benchmark
    public void jsonSerialize() throws JsonProcessingException {
        objectMapper.writeValueAsBytes(classicDTO);
    }

    @Benchmark
    public void protobufSerialize() {
        protoSomeClassicDTO.toByteArray();
    }

    @Benchmark
    public void jsonDeserialize() throws IOException {
        objectMapper.readValue(byteSomeClassicDTO, ClassicDTO.class);
    }

    @Benchmark
    public void protobufDeserialize() throws InvalidProtocolBufferException {
        ru.bit.proto.SomeClassicDTO.protoSomeClassicDTO.parseFrom(byteProtoSomeClassicDTO);
    }
}