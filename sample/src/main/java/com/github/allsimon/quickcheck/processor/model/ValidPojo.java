package com.github.allsimon.quickcheck.processor.model;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import lombok.Value;

@Value
public class ValidPojo {

  @Min(0)
  @Max(10)
  Integer foo;

  @Min(-13)
  @Max(5)
  Long bar;
}
