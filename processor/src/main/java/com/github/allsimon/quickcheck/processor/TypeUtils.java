package com.github.allsimon.quickcheck.processor;

import static com.github.allsimon.quickcheck.processor.GeneratorProcessor.processingEnv;
import static io.vavr.API.Seq;
import static io.vavr.API.Tuple;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import io.vavr.Tuple2;
import io.vavr.collection.Seq;
import java.util.ArrayList;
import java.util.List;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

public class TypeUtils {

  public static Tuple2<String, Seq<TypeName>> getType(VariableElement element) {
    return recursiveGetType(element.asType(), Tuple(Seq(), Seq()))
        .map1(s -> s.mkString(", "));
  }

  private static Tuple2<Seq<String>, Seq<TypeName>> recursiveGetType(TypeMirror typeMirror,
      Tuple2<Seq<String>, Seq<TypeName>> previous) {

    boolean isDeclaredType = typeMirror instanceof DeclaredType;

    List<? extends TypeMirror> typeArguments = isDeclaredType ?
        ((DeclaredType) typeMirror).getTypeArguments() :
        new ArrayList<>();

    Tuple2<Seq<String>, Seq<TypeName>> current = previous
        .map(s -> s.append("$T.class"), s -> s.append(ClassName.get(processingEnv.getTypeUtils().erasure(typeMirror))));
    return typeArguments.isEmpty() ? current : recursiveGetType(typeArguments.get(0), current);
  }
}
