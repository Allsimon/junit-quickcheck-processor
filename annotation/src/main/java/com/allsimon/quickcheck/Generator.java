package com.allsimon.quickcheck;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Repeatable(Generator.List.class)
@Retention(RetentionPolicy.SOURCE)
public @interface Generator {

  Class value();

  @Target(ElementType.TYPE)
  @Retention(RetentionPolicy.SOURCE)
  @interface List {

    Generator[] value();
  }
}
