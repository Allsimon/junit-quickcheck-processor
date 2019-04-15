package com.github.allsimon.quickcheck.processor.model;

import static org.junit.Assert.assertNotNull;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.runner.RunWith;

@RunWith(JUnitQuickcheck.class)
public class GeneratorTest {

  @Property
  public void sampleTest(SimplePojo pojo) {
    assertNotNull(pojo);
  }

  @Property
  public void pojoTuple(PojoTuple tuple) {
    assertNotNull(tuple);
  }
}
