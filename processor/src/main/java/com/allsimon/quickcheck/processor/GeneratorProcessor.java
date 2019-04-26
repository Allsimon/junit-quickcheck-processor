package com.allsimon.quickcheck.processor;

import static com.squareup.javapoet.ClassName.get;
import static java.util.stream.Collectors.toSet;

import com.allsimon.quickcheck.AutoGenerator;
import com.allsimon.quickcheck.AutoGenerator.List;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
public class GeneratorProcessor extends AbstractProcessor {

  static ProcessingEnvironment processingEnv;

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    GeneratorProcessor.processingEnv = processingEnv;
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    roundEnv.getElementsAnnotatedWith(AutoGenerator.List.class)
        .forEach(element -> Arrays.stream(element.getAnnotationsByType(AutoGenerator.List.class))
            .map(List::value)
            .flatMap(Arrays::stream)
            .forEach(annotation -> handle(annotation, element)));

    roundEnv.getElementsAnnotatedWith(AutoGenerator.class)
        .forEach(element -> Arrays.stream(element.getAnnotationsByType(AutoGenerator.class))
            .forEach(annotation -> handle(annotation, element)));
    return false;
  }

  private void handle(AutoGenerator annotation, Element element) {
    TypeMirror classToGenerate = Utils.getMirroredThing(t -> annotation.value());

    Set<JavaFile.Builder> javaFiles = new HashSet<>();
    javaFiles.add(new GeneratorBuilder(get(classToGenerate)).javaFileBuilder());

    javaFiles.forEach(fileBuilder -> {
      try {
        fileBuilder.build().writeTo(processingEnv.getFiler());
      } catch (IOException e) {
        processingEnv.getMessager()
            .printMessage(Diagnostic.Kind.ERROR, "Failed to write file for element: ", element);
      }
    });
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return Stream.of(AutoGenerator.class, AutoGenerator.List.class).map(Class::getCanonicalName).collect(toSet());
  }
}
