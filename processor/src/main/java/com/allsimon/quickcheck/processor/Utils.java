package com.allsimon.quickcheck.processor;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Seq;
import static io.vavr.API.Tuple;
import static java.lang.String.format;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

import io.vavr.API.Match.Case;
import io.vavr.Tuple4;
import io.vavr.collection.Seq;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;

public class Utils {

  public static final boolean DONT_CAST = false;
  public static final boolean CAST_TO_STRING = true;

  public static TypeMirror getMirroredThing(UnaryOperator<?> operator) {
    try {
      operator.apply(null);
    } catch (MirroredTypeException mte) {
      return mte.getTypeMirror();
    }
    throw new RuntimeException("Wat ?");
  }

  public static Optional<? extends Element> findBiggestArgsConstructor(Element el) {
    return el.getEnclosedElements().stream()
        .filter(subElement -> subElement.getKind() == ElementKind.CONSTRUCTOR)
        .filter(subElement -> subElement.getModifiers().contains(Modifier.PUBLIC))
        .filter(subElement -> subElement.asType() instanceof ExecutableType)
        .max(comparing(e -> ((ExecutableType) e.asType()).getParameterTypes().size()));
  }

  public static boolean hasFieldAnnotatedWithJSR380(Element el) {
    return el.getEnclosedElements().stream()
        .filter(sub -> sub.getKind() == ElementKind.FIELD)
        .anyMatch(Utils::isAnnotatedWithJSR380);
  }

  private static boolean isAnnotatedWithJSR380(Element el) {
    return el.getAnnotationMirrors().stream().anyMatch(e -> e.toString().startsWith("@javax.validation"));
  }

  public static List<? extends Element> getJSR380Annotations(Element el) {
    return el.getEnclosedElements().stream()
        .filter(sub -> sub.getKind() == ElementKind.FIELD)
        .filter(Utils::isAnnotatedWithJSR380)
        .collect(toList());
  }

  public static <T> T extractValue(AnnotationMirror annotationMirror,
      String valueName, Class<T> expectedType) {
    Map<ExecutableElement, AnnotationValue> elementValues = new HashMap<>(
        annotationMirror.getElementValues());
    for (Entry<ExecutableElement, AnnotationValue> entry : elementValues
        .entrySet()) {
      if (entry.getKey().getSimpleName().contentEquals(valueName)) {
        Object value = entry.getValue().getValue();
        return expectedType.cast(value);
      }
    }
    return null;
  }

  public static <A extends Annotation> Case<String, String> caseOf(Class<A> clazz, AnnotationMirror annotation,
      Seq<Tuple4<String, String, Class<?>, Boolean>> tuples) {
    return Case($(clazz.getCanonicalName()), extractFromAnnotation(annotation, tuples));
  }

  private static Supplier<String> extractFromAnnotation(AnnotationMirror annotation,
      Seq<Tuple4<String, String, Class<?>, Boolean>> tuples) {
    return () -> tuples.map(tuple -> extractFromAnnotation(annotation, tuple))
        .filter(Objects::nonNull)
        .mkString(", ");
  }

  private static String extractFromAnnotation(AnnotationMirror annotation,
      Tuple4<String, String, Class<?>, Boolean> tuple) {

    boolean expectAString = tuple._4();

    Object value = extractValue(annotation, tuple._2(), tuple._3());

    if (value == null) {
      return null;
    }
    return format(expectAString ? "\"%s\", \"%s\"" : "\"%s\", %s", tuple._1(),
        value);
  }

  public static <A extends Annotation> Case<String, String> caseOf(Class<A> clazz, AnnotationMirror annotation,
      String parameter, String annotationFieldName, Class<?> annotationFieldClass) {
    return caseOf(clazz, annotation, Seq(Tuple(parameter, annotationFieldName, annotationFieldClass, true)));
  }

  public static <A extends Annotation> Case<String, String> caseOf(Class<A> clazz,
      String parameter,
      String value) {
    return Case($(clazz.getCanonicalName()), () -> format("\"%s\", \"%s\"", parameter, value));
  }
}
