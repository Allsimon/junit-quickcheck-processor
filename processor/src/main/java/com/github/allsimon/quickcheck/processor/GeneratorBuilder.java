package com.github.allsimon.quickcheck.processor;

import static com.squareup.javapoet.ClassName.get;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;

import com.google.auto.service.AutoService;
import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

public class GeneratorBuilder {

  private final AnnotationSpec annotationAutoService = AnnotationSpec.builder(AutoService.class)
      .addMember("value", "$T.class", com.pholser.junit.quickcheck.generator.Generator.class)
      .build();

  protected final ClassName annotatedClass;

  public GeneratorBuilder(TypeName annotatedClass) {
    this.annotatedClass = (ClassName) annotatedClass;
  }

  public JavaFile.Builder javaFileBuilder() {
    return JavaFile.builder(annotatedClass.packageName(), generateTypeSpec().build());
  }

  protected TypeSpec.Builder generateTypeSpec() {
    ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName
        .get(get(com.pholser.junit.quickcheck.generator.Generator.class), annotatedClass);

    FieldSpec.builder(parameterizedTypeName, "generator", PRIVATE, FINAL);

    return TypeSpec.classBuilder(generatedClassName())
        .superclass(parameterizedTypeName)
        .addModifiers(PUBLIC)
        .addAnnotation(annotationAutoService)
        .addMethod(generateMethod())
        .addMethod(constructor());
  }

  protected String generatedClassName() {
    return annotatedClass.simpleName() + "Gen";
  }

  private MethodSpec constructor() {
    return MethodSpec.constructorBuilder()
        .addModifiers(PUBLIC)
        .addStatement("super($T.class)", annotatedClass)
        .build();
  }

  protected MethodSpec generateMethod() {
    return MethodSpec.methodBuilder("generate")
        .addModifiers(PUBLIC)
        .addParameter(get(SourceOfRandomness.class), "random")
        .addParameter(get(GenerationStatus.class), "status")
        .addStatement("return gen().fieldsOf($T.class).generate(random, status)", annotatedClass)
        .returns(annotatedClass)
        .addAnnotation(Override.class)
        .build();
  }
}
