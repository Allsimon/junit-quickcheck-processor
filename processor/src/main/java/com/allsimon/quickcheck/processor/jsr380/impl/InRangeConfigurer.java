package com.allsimon.quickcheck.processor.jsr380.impl;

import static com.squareup.javapoet.TypeName.get;
import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.API.Seq;
import static io.vavr.API.Tuple;

import com.allsimon.quickcheck.processor.Utils;
import com.allsimon.quickcheck.processor.jsr380.WrappingTypeName;
import com.pholser.junit.quickcheck.generator.InRange;
import io.vavr.API;
import io.vavr.Tuple2;
import io.vavr.collection.Seq;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.VariableElement;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Negative;
import javax.validation.constraints.NegativeOrZero;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

public class InRangeConfigurer implements JSR380Configurer {

  @Override
  public Seq<Tuple2<String, WrappingTypeName>> configure(List<? extends AnnotationMirror> annotations,
      VariableElement element) {
    String configuration = annotations == null ? "" :
        annotations.stream()
            .map(a -> Match(a.getAnnotationType().toString())
                .of(
                    Utils.caseOf(DecimalMin.class, a, "min", "value", String.class),
                    Utils.caseOf(DecimalMax.class, a, "max", "value", String.class),
                    Utils.caseOf(Min.class, a, "min", "value", Long.class),
                    Utils.caseOf(Max.class, a, "max", "value", Long.class),
                    Utils.caseOf(Negative.class, "max", "-1"),
                    Utils.caseOf(NegativeOrZero.class, "max", "0"),
                    Utils.caseOf(Positive.class, "min", "1"),
                    Utils.caseOf(PositiveOrZero.class, "min", "0"),
                    Case($(), () -> null))
            )
            .filter(Objects::nonNull)
            .collect(Collectors.joining(", "));

    return configuration.isEmpty() ? Seq() :
        API.Seq(
            Tuple("confAnnotations(%s, $T.class, " + configuration + ")", WrappingTypeName.after(get(InRange.class))));
  }
}
