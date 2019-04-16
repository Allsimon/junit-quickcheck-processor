package com.github.allsimon.quickcheck.processor.model;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ValidPojo {

  @NotNull
  String foo;

  @Size(min = 2, max = 14)
  String bar;

  @Min(0)
  @Max(10)
  int bazz;
}
