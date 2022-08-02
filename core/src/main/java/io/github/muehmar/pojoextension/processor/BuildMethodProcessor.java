package io.github.muehmar.pojoextension.processor;

import static io.github.muehmar.pojoextension.Booleans.not;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.exception.PojoExtensionException;
import io.github.muehmar.pojoextension.generator.model.BuildMethod;
import io.github.muehmar.pojoextension.generator.model.Name;
import io.github.muehmar.pojoextension.generator.model.type.Type;
import java.util.List;
import java.util.Optional;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;

public class BuildMethodProcessor {
  private BuildMethodProcessor() {}

  public static Optional<BuildMethod> process(Element pojo) {
    final PList<BuildMethod> buildMethods =
        PList.fromIter(pojo.getEnclosedElements())
            .filter(
                e ->
                    e.getAnnotation(io.github.muehmar.pojoextension.annotations.BuildMethod.class)
                        != null)
            .map(e -> convertToBuildMethod(pojo, e));
    if (buildMethods.size() >= 2) {
      final String message =
          String.format(
              "There is only one custom build method allowed (annotated with @%s) in class %s",
              io.github.muehmar.pojoextension.annotations.BuildMethod.class.getSimpleName(),
              pojo.getSimpleName());
      throw new PojoExtensionException(message);
    }

    return buildMethods.headOption();
  }

  private static BuildMethod convertToBuildMethod(Element pojo, Element element) {
    final ExecutableElement annotatedMethod = assertCorrectMethodSignature(pojo, element);

    final Name methodName = Name.fromString(annotatedMethod.getSimpleName().toString());
    final Type returnType = TypeMirrorMapper.map(annotatedMethod.getReturnType());
    return new BuildMethod(methodName, returnType);
  }

  private static ExecutableElement assertCorrectMethodSignature(
      Element pojo, Element annotatedMethod) {
    assertMethodKind(pojo, annotatedMethod);
    assertNotPrivateMethod(pojo, annotatedMethod);
    assertStaticMethod(pojo, annotatedMethod);
    return assertSinglePojoArgument(pojo, annotatedMethod);
  }

  private static void assertMethodKind(Element pojo, Element annotatedMethod) {
    if (not(annotatedMethod.getKind().equals(ElementKind.METHOD))) {
      final String message =
          String.format(
              "'%s' in class '%s' annotated with @%s is not a method",
              annotatedMethod.getSimpleName(),
              pojo.getSimpleName(),
              io.github.muehmar.pojoextension.annotations.BuildMethod.class.getSimpleName());
      throw new PojoExtensionException(message);
    }
  }

  private static void assertNotPrivateMethod(Element pojo, Element annotatedMethod) {
    if (annotatedMethod.getModifiers().contains(Modifier.PRIVATE)) {
      final String message =
          String.format(
              "The modifier 'private' is not allowed for methods annotated with @%s. Method '%s' in class '%s'.",
              io.github.muehmar.pojoextension.annotations.BuildMethod.class.getSimpleName(),
              annotatedMethod.getSimpleName(),
              pojo.getSimpleName());
      throw new PojoExtensionException(message);
    }
  }

  private static void assertStaticMethod(Element pojo, Element annotatedMethod) {
    if (not(annotatedMethod.getModifiers().contains(Modifier.STATIC))) {
      final String message =
          String.format(
              "Method '%s' in class '%s' annotated with @%s must be static.",
              annotatedMethod.getSimpleName(),
              pojo.getSimpleName(),
              io.github.muehmar.pojoextension.annotations.BuildMethod.class.getSimpleName());
      throw new PojoExtensionException(message);
    }
  }

  private static ExecutableElement assertSinglePojoArgument(Element pojo, Element annotatedMethod) {
    final ExecutableElement annotatedMethod1 = (ExecutableElement) annotatedMethod;
    final List<? extends VariableElement> parameters = annotatedMethod1.getParameters();
    final Type pojoType = TypeMirrorMapper.map(pojo.asType());
    if (parameters.size() != 1) {
      final String message =
          String.format(
              "Method '%s' in class '%s' annotated with @%s must have exactly one argument accepting the built instance of %s.",
              annotatedMethod.getSimpleName(),
              pojo.getSimpleName(),
              io.github.muehmar.pojoextension.annotations.BuildMethod.class.getSimpleName(),
              pojo.getSimpleName());
      throw new PojoExtensionException(message);
    }
    final Type argumentType = TypeMirrorMapper.map(parameters.get(0).asType());
    if (not(pojoType.equals(argumentType))) {
      final String message =
          String.format(
              "Wrong argument type in method '%s' in class '%s' annotated with @%s. It must accept the built instance of %s.",
              annotatedMethod.getSimpleName(),
              pojo.getSimpleName(),
              io.github.muehmar.pojoextension.annotations.BuildMethod.class.getSimpleName(),
              pojo.getSimpleName());
      throw new PojoExtensionException(message);
    }
    return annotatedMethod1;
  }
}
