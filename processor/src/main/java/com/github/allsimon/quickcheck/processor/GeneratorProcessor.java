package com.github.allsimon.quickcheck.processor;

import static com.github.allsimon.quickcheck.processor.Utils.hasFieldAnnotatedWithJSR380;
import static com.squareup.javapoet.ClassName.get;
import static java.util.stream.Collectors.toSet;

import com.github.allsimon.quickcheck.Generator;
import com.github.allsimon.quickcheck.Generator.List;
import com.github.allsimon.quickcheck.processor.jsr380.ValidGeneratorBuilder;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;

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
    TypeMirror classToGenerate = Utils.getMirroredThing(t -> annotation.value());
    Element elementToGenerate = processingEnv.getTypeUtils().asElement(classToGenerate);

    Set<JavaFile.Builder> javaFiles = new HashSet<>();
    javaFiles.add(new GeneratorBuilder(get(classToGenerate)).javaFileBuilder());

    if (hasFieldAnnotatedWithJSR380(elementToGenerate)) {
      Optional<java.util.List<
          ? extends VariableElement>> constructorArgs = Utils.findBiggestArgsConstructor(elementToGenerate)
          .map(t -> (ExecutableElement) t)
          .map(ExecutableElement::getParameters);
      if (!constructorArgs.isPresent()) {
        processingEnv.getMessager()
            .printMessage(Kind.ERROR, "No public constructor found for: " + element.getSimpleName());
      } else {
        javaFiles.add(new ValidGeneratorBuilder(get(classToGenerate),
            constructorArgs.get(),
            Utils.getJSR380Annotations(elementToGenerate)).javaFileBuilder());
      }
    }

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
    return Stream.of(Generator.class, Generator.List.class).map(Class::getCanonicalName).collect(toSet());
  }
}
