package com.github.allsimon.quickcheck.processor.jsr380;

import static io.vavr.API.Seq;

import com.squareup.javapoet.TypeName;
import io.vavr.collection.Seq;
import lombok.Value;

@Value
public class WrappingTypeName {

  Seq<TypeName> before;
  Seq<TypeName> after;

  public static WrappingTypeName after(TypeName after) {
    return new WrappingTypeName(Seq(), Seq(after));
  }

  static WrappingTypeName empty() {
    return new WrappingTypeName(Seq(), Seq());
  }

  static Seq<TypeName> fold(Seq<TypeName> prev, WrappingTypeName curr) {
    return curr.getBefore().appendAll(prev).appendAll(curr.getAfter());
  }
}
