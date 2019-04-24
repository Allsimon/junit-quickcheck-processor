package com.allsimon.quickcheck.processor.jsr380.impl;

import static com.allsimon.quickcheck.processor.jsr380.WrappingTypeName.after;
import static com.squareup.javapoet.TypeName.get;
import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.API.Seq;
import static io.vavr.API.Tuple;

import com.allsimon.quickcheck.jsr380.annotation.ValidEmail;
import com.allsimon.quickcheck.processor.Utils;
import com.allsimon.quickcheck.processor.jsr380.WrappingTypeName;
import io.vavr.Tuple2;
import io.vavr.collection.Seq;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.VariableElement;
import javax.validation.constraints.Email;

public class EmailConfigurer implements JSR380Configurer {

  @Override
  public Seq<Tuple2<String, WrappingTypeName>> configure(List<? extends AnnotationMirror> nullableAnnotations,
      VariableElement element) {
    Optional<String> configuration = Optional.ofNullable(nullableAnnotations)
        .flatMap(annotations ->
            annotations.stream()
                .map(a -> Match(a.getAnnotationType().toString())
                    .of(
                        Utils.caseOf(Email.class, () -> ""),
                        Case($(), () -> null))
                )
                .filter(Objects::nonNull)
                .findAny());

    return !configuration.isPresent() ? Seq() :
        Seq(Tuple("confAnnotations(%s, $T.class)", after(get(ValidEmail.class))));
  }
}
