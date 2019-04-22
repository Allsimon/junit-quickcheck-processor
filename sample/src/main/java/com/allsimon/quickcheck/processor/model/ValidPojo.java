package com.allsimon.quickcheck.processor.model;

import java.math.BigInteger;
import java.util.List;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Negative;
import javax.validation.constraints.NegativeOrZero;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import lombok.Value;

@Value
public class ValidPojo {

  @Min(17)
  @Max(26)
  int foo;

  @Min(-13)
  @Max(5)
  Long bar;

  @DecimalMin("15")
  BigInteger decimalMin;

  @DecimalMax("3")
  int decimalMax;

  @Negative
  short negative;

  @NegativeOrZero
  float negativeOrZero;

  @Positive
  int positive;

  @PositiveOrZero
  double positiveOrZero;

  @Size(min = 3, max = 5)
  List<String> sizeList;

  @Size(max = 10)
  int[] sizeArray;

  String notValidatedField;

//  @Null
//  String nullString;

//  @Size(min = 2, max = 4)
//  String stringSize;

//  TODO:
//  @Future
//  Instant future1;
//  @Future
//  LocalDateTime future2;
//  @Future
//  LocalDate future3;
//  @Future
//  LocalTime future4;

// TODO:
//  Digits
//  Email
//  Future
//  FutureOrPresent
//  Negative
//  NegativeOrZero
//  NotBlank
//  NotEmpty
//  NotNull
//  Null
//  Past
//  PastOrPresent
//  Pattern
//  Size
}
