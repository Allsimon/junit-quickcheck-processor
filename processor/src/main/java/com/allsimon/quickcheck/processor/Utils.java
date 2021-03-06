package com.allsimon.quickcheck.processor;

import java.util.function.UnaryOperator;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;

public class Utils {

  public static TypeMirror getMirroredThing(UnaryOperator<?> operator) {
    try {
      operator.apply(null);
    } catch (MirroredTypeException mte) {
      return mte.getTypeMirror();
    }
    throw new RuntimeException("Wat ?");
  }
}
