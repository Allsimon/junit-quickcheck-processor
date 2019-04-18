package com.github.allsimon.quickcheck.processor.jsr380.impl;

import static com.github.allsimon.quickcheck.processor.Utils.caseOf;
import static com.github.allsimon.quickcheck.processor.jsr380.WrappingTypeName.wrap;
import static com.squareup.javapoet.TypeName.get;
import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.API.Seq;
import static io.vavr.API.Tuple;

import com.github.allsimon.quickcheck.jsr380.ConfigurableGenerator;
import com.github.allsimon.quickcheck.processor.jsr380.JSR380Configurer;
import com.github.allsimon.quickcheck.processor.jsr380.WrappingTypeName;
import com.pholser.junit.quickcheck.generator.InRange;
import io.vavr.Tuple2;
import io.vavr.collection.Seq;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.VariableElement;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

public class InRangeConfigurer implements JSR380Configurer {

  @Override
  public Seq<Tuple2<String, WrappingTypeName>> configure(List<? extends AnnotationMirror> annotations,
      VariableElement element) {
    String configuration = annotations.stream()
        .map(a -> Match(a.getAnnotationType().toString())
            .of(
                caseOf(a, Min.class, "min", "value", Long.class),
                caseOf(a, Max.class, "max", "value", Long.class),
                Case($(), () -> null))
        )
        .filter(Objects::nonNull)
        .collect(Collectors.joining(", "));

    return configuration.isEmpty() ? Seq() :
        Seq(Tuple("$T.configure(%s, $T.class, " + configuration + ")",
            wrap(get(ConfigurableGenerator.class), get(InRange.class))));
  }
}
