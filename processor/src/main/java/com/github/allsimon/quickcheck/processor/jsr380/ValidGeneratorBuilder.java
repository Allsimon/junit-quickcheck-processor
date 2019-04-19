package com.github.allsimon.quickcheck.processor.jsr380;

import static com.github.allsimon.quickcheck.processor.jsr380.WrappingTypeName.empty;
import static com.squareup.javapoet.ClassName.get;
import static io.vavr.API.List;
import static io.vavr.API.Seq;
import static io.vavr.API.Tuple;
import static java.util.function.Function.identity;
import static javax.lang.model.element.Modifier.PUBLIC;

import com.github.allsimon.quickcheck.Valid;
import com.github.allsimon.quickcheck.processor.GeneratorBuilder;
import com.github.allsimon.quickcheck.processor.TypeUtils;
import com.github.allsimon.quickcheck.processor.jsr380.impl.InRangeConfigurer;
import com.github.allsimon.quickcheck.processor.jsr380.impl.SizeConfigurer;
import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import io.vavr.Tuple2;
import io.vavr.collection.Seq;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;

public class ValidGeneratorBuilder extends GeneratorBuilder {

  private static final Seq<JSR380Configurer> CONFIGURERS = Seq(new InRangeConfigurer(), new SizeConfigurer());

  private final List<? extends VariableElement> constructorArgs;
  private final Map<Name, List<? extends AnnotationMirror>> jsr380Annotations;

  public ValidGeneratorBuilder(ClassName annotatedClass, List<? extends VariableElement> constructorArgs,
      List<? extends Element> jsr380Annotations) {
    super(annotatedClass);
    this.constructorArgs = constructorArgs;
    this.jsr380Annotations = jsr380Annotations.stream()
        .map(annotation -> (VariableElement) annotation)
        .collect(Collectors.toMap(VariableElement::getSimpleName, VariableElement::getAnnotationMirrors));
  }

  @Override
  public String generatedClassName() {
    return annotatedClass.simpleName() + "ValidGen";
  }

  @Override
  protected MethodSpec generateMethod() {
    return MethodSpec.methodBuilder("generate")
        .addModifiers(PUBLIC)
        .addParameter(get(SourceOfRandomness.class), "random")
        .addParameter(get(GenerationStatus.class), "status")
        .addCode(constructor())
        .returns(annotatedClass)
        .addAnnotation(Override.class)
        .build();
  }

  private CodeBlock constructor() {
    Tuple2<String, Seq<TypeName>> args = io.vavr.collection.List
        .ofAll(constructorArgs)
        .map(this::constructorField)
        .unzip(identity())
        .map1(s -> s.mkString(",\n"))
        .map2(s -> s.flatMap(identity()));

    Object[] objects = args._2().prepend(annotatedClass).toJavaArray(TypeName.class);

    return CodeBlock.builder()
        .addStatement(String.format("return new $T(\n%s\n)", args._1()), objects)
        .build();
  }

  @Override
  public List<MethodSpec> additionalMethods() {
    return List(configureMethod()).toJavaList();
  }

  /**
   * Output when:
   * - no validation needed:
   * ```
   * gen().type($T.class).generate(random, status)
   * ```
   * - one validation needed:
   * ```
   * ConfigurableGenerator.configure(gen().type(Integer.class), InRange.class, "min", "0")).generate(random, status)
   * ```
   * - two validations needed:
   * ```
   * ConfigurableGenerator.configure(
   * ConfigurableGenerator.configure(gen().type(Integer.class), InRange.class, "min", "0"),
   * Precision.class, "scale", "1").generate(random, status));
   * ```
   * and so on.
   */
  private Tuple2<String, Seq<TypeName>> constructorField(VariableElement element) {
    Name name = element.getSimpleName();
    List<? extends AnnotationMirror> annotations = jsr380Annotations.get(name);
    Tuple2<String, Seq<TypeName>> elementType = TypeUtils.getType(element);

    String genString = String.format("gen().type(%s)", elementType._1());
    return CONFIGURERS
        .flatMap(configurer -> configurer.configure(annotations, element))
        .append(Tuple(genString, empty()))
        .unzip(Function.identity())
        .map1(s -> s.foldLeft("%s", String::format) + ".generate(random, status)")
        .map2(s -> s.foldLeft(elementType._2(), WrappingTypeName::fold));
  }

  /**
   * Outputs
   * ```
   * public void configure(Valid valid) {
   * }
   * ```
   */
  private MethodSpec configureMethod() {
    return MethodSpec.methodBuilder("configure")
        .addModifiers(PUBLIC)
        .addParameter(get(Valid.class), "valid")
        .returns(void.class)
        .build();
  }
}
