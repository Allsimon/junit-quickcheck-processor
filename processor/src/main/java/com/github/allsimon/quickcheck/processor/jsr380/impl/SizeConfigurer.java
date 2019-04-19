package com.github.allsimon.quickcheck.processor.jsr380.impl;

import static com.github.allsimon.quickcheck.processor.Utils.DONT_CAST;
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
import io.vavr.Tuple2;
import io.vavr.collection.Seq;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.VariableElement;
import javax.validation.constraints.Size;

public class SizeConfigurer implements JSR380Configurer {

  @Override
  public Seq<Tuple2<String, WrappingTypeName>> configure(List<? extends AnnotationMirror> annotations,
      VariableElement element) {
    String configuration = annotations.stream()
        .map(a -> Match(a.getAnnotationType().toString())
            .of(
                caseOf(Size.class, a,
                    Seq(Tuple("min", "min", Integer.class, DONT_CAST), Tuple("max", "max", Integer.class, DONT_CAST))),
                Case($(), () -> null))
        )
        .filter(Objects::nonNull)
        .collect(Collectors.joining(", "));

    return configuration.isEmpty() ? Seq() :
        Seq(Tuple("$T.configure(%s, $T.class, " + configuration + ")",
            wrap(get(ConfigurableGenerator.class), get(com.pholser.junit.quickcheck.generator.Size.class))));
  }
}
