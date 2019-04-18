package com.github.allsimon.quickcheck.processor.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

import com.github.allsimon.quickcheck.Valid;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import javax.validation.Validation;
import javax.validation.Validator;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

@RunWith(JUnitQuickcheck.class)
public class GeneratorTest {

  private static Validator validator;

  @BeforeClass
  public static void setUpValidator() {
    validator = Validation.buildDefaultValidatorFactory().getValidator();
  }

  @Property
  public void sampleTest(SimplePojo pojo) {
    assertNotNull(pojo);
  }

  @Property
  public void pojoTuple(PojoTuple tuple) {
    assertNotNull(tuple);
  }

  @Property
  public void validPojo(@Valid ValidPojo validPojo) {
    assertThat(validator.validate(validPojo)).isEmpty();
    assertThat(validPojo.getFoo()).isBetween(0, 10);
  }
}
