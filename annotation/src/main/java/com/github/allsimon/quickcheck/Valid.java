package com.github.allsimon.quickcheck;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.pholser.junit.quickcheck.generator.GeneratorConfiguration;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@GeneratorConfiguration
@Target({PARAMETER, TYPE_USE})
public @interface Valid {
}
