package com.allsimon.quickcheck;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Repeatable(AutoGenerator.List.class)
@Retention(RetentionPolicy.SOURCE)
public @interface AutoGenerator {

  Class value();

  @Target(ElementType.TYPE)
  @Retention(RetentionPolicy.SOURCE)
  @interface List {

    AutoGenerator[] value();
  }
}
