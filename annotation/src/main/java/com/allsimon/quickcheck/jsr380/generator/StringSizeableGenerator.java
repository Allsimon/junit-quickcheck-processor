package com.allsimon.quickcheck.jsr380.generator;

import com.google.auto.service.AutoService;
import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.generator.Size;
import com.pholser.junit.quickcheck.generator.java.lang.StringGenerator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import java.util.Optional;
import java.util.stream.IntStream;

@AutoService(Generator.class)
public class StringSizeableGenerator extends StringGenerator {

  private static final int DEFAULT_MAX_LENGTH = 32;
  private int minLength = 0;
  private Optional<Integer> maxLength = Optional.empty();

  @Override
  public String generate(SourceOfRandomness random, GenerationStatus status) {

    int safeMax = IntStream.of(maxLength.orElse(status.size()), DEFAULT_MAX_LENGTH + minLength)
        .filter(v -> v > minLength)
        .findFirst().getAsInt();

    int size = random.nextInt(minLength, safeMax);
    int[] codePoints = new int[size];

    for (int i = 0; i < codePoints.length; ++i) {
      codePoints[i] = nextCodePoint(random);
    }

    return new String(codePoints, 0, codePoints.length);
  }

  public void configure(Size size) {
    minLength = size.min();
    maxLength = Optional.of(size.max()).filter(max -> max > 0);
  }
}
