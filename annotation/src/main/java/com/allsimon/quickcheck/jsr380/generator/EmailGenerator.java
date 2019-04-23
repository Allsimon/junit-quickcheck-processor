package com.allsimon.quickcheck.jsr380.generator;

import static java.lang.Math.min;

import com.google.auto.service.AutoService;
import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.generator.java.lang.StringGenerator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;

@AutoService(Generator.class)
public class EmailGenerator extends StringGenerator {

  @Override
  public String generate(SourceOfRandomness random, GenerationStatus status) {
    String localPart = super.generate(random, status);
    localPart = localPart.substring(0, min(localPart.length(), 15)) + nextCodePoint(random);
    return localPart + "@example.com";
  }

  @Override
  protected int nextCodePoint(SourceOfRandomness random) {
    return random.nextInt(97, 122);
  }

  @Override
  protected boolean codePointInRange(int i) {
    return false;
  }
}
