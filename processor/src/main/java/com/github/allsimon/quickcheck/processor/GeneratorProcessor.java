package com.github.allsimon.quickcheck.processor;

import static com.squareup.javapoet.ClassName.get;
import static java.util.stream.Collectors.toSet;

import com.github.allsimon.quickcheck.Generator;
import com.github.allsimon.quickcheck.Generator.List;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
public class GeneratorProcessor extends AbstractProcessor {

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    roundEnv.getElementsAnnotatedWith(Generator.List.class)
        .forEach(element -> Arrays.stream(element.getAnnotationsByType(Generator.List.class))
            .map(List::value)
            .flatMap(Arrays::stream)
            .forEach(annotation -> handle(annotation, element)));

    roundEnv.getElementsAnnotatedWith(Generator.class)
        .forEach(element -> Arrays.stream(element.getAnnotationsByType(Generator.class))
            .forEach(annotation -> handle(annotation, element)));
    return false;
  }

  private void handle(Generator annotation, Element element) {
    TypeElement annotatedType = (TypeElement) element;

    TypeMirror classToGenerate = Utils.getMirroredThing(t -> annotation.value());

    TypeSpec generatorClass = new GeneratorBuilder((ClassName) get(classToGenerate)).generate();

    JavaFile file = JavaFile.builder(get(annotatedType).packageName(), generatorClass).build();

    try {
      file.writeTo(processingEnv.getFiler());
    } catch (IOException e) {
      processingEnv.getMessager()
          .printMessage(Diagnostic.Kind.ERROR, "Failed to write file for element", element);
    }
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return Stream.of(Generator.class, Generator.List.class).map(Class::getCanonicalName).collect(toSet());
  }
}
