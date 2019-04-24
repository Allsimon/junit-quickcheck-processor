package com.allsimon.quickcheck.processor.model;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.runner.RunWith;

@RunWith(JUnitQuickcheck.class)
public class GeneratorTest {

  @Property
  public void sampleTest(SimplePojo pojo) {
    assertThat(pojo, notNullValue());
  }

  @Property
  public void pojoTuple(PojoTuple pojo) {
    assertThat(pojo, notNullValue());
  }
}
