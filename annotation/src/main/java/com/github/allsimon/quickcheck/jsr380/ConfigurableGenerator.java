package com.github.allsimon.quickcheck.jsr380;

import com.pholser.junit.quickcheck.generator.Generator;
import io.leangen.geantyref.AnnotationFormatException;
import io.leangen.geantyref.GenericTypeReflector;
import io.leangen.geantyref.TypeFactory;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ConfigurableGenerator {

  public static <T, A extends Annotation> Generator<T> configure(Generator<T> generator, Class<A> annotationClazz,
      String... parameters) {
    generator.configure(buildAnnotation(annotationClazz, parameters));
    return generator;
  }

  private static <A extends Annotation> AnnotatedType buildAnnotation(Class<A> clazz, String... parameters) {
    assert parameters.length % 2 == 0 : "Wrong numbers of parameters: got " + Arrays.toString(parameters);
    Map<String, Object> annotationParameters = new HashMap<>();
    for (int i = 0; i < parameters.length; i = i + 2) {
      annotationParameters.put(parameters[i], parameters[i + 1]);
    }

    try {
      Annotation[] myAnnotation = {TypeFactory.annotation(clazz, annotationParameters)};

      return GenericTypeReflector.annotate(Integer.class, myAnnotation);
    } catch (AnnotationFormatException e) {
      throw new RuntimeException(e);
    }
  }
}
