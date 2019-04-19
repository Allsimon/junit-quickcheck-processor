package com.github.allsimon.quickcheck.processor.jsr380.impl;

import com.github.allsimon.quickcheck.processor.jsr380.WrappingTypeName;
import io.vavr.Tuple2;
import io.vavr.collection.Seq;
import java.util.List;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.VariableElement;

public interface JSR380Configurer {

  Seq<Tuple2<String, WrappingTypeName>> configure(List<? extends AnnotationMirror> annotations,
      VariableElement element);
}
