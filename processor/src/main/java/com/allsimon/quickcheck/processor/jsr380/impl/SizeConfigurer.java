package com.allsimon.quickcheck.processor.jsr380.impl;

import static com.squareup.javapoet.TypeName.get;
import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.API.Seq;
import static io.vavr.API.Tuple;

import com.allsimon.quickcheck.processor.Utils;
import com.allsimon.quickcheck.processor.jsr380.WrappingTypeName;
import io.vavr.API;
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
    String configuration = annotations == null ? "" :
        annotations.stream()
            .map(a -> Match(a.getAnnotationType().toString())
                .of(
                    Utils.caseOf(Size.class, a,
                        Seq(Tuple("min", "min", Integer.class, Utils.DONT_CAST),
                            Tuple("max", "max", Integer.class, Utils.DONT_CAST))),
                    Case($(), () -> null))
            )
            .filter(Objects::nonNull)
            .collect(Collectors.joining(", "));

    return configuration.isEmpty() ? Seq() :
        API.Seq(Tuple("confAnnotations(%s, $T.class, " + configuration + ")",
            WrappingTypeName.after(get(com.pholser.junit.quickcheck.generator.Size.class))));
  }
}
