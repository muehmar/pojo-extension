package io.github.muehmar.pojoextension.generator.impl.gen.instantiation;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.data.Argument;
import io.github.muehmar.pojoextension.generator.data.MatchingConstructor;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.PojoField;
import io.github.muehmar.pojoextension.generator.data.PojoSettings;
import io.github.muehmar.pojoextension.generator.writer.Writer;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;

public class ConstructorCallGen {
  private ConstructorCallGen() {}

  /**
   * This generator creates a call to the constructor (return the created instance) of the pojo
   * while wrapping optional fields into an {@link Optional} depending on the argument of the
   * constructor.
   */
  public static Generator<Pojo, PojoSettings> constructorCall() {
    return (pojo, settings, writer) -> {
      final MatchingConstructor match =
          pojo.findMatchingConstructor()
              .orElseThrow(
                  () -> new IllegalStateException("No matching constructor found for pojo"));

      final AtomicReference<UnaryOperator<Writer>> addRef =
          new AtomicReference<>(UnaryOperator.identity());

      final PList<String> constructorParameters =
          match
              .getConstructor()
              .getArguments()
              .zip(match.getFields())
              .map(
                  p -> {
                    final Argument argument = p.first();
                    final PojoField field = p.second();
                    final PojoField.OnExactMatch<String> onExactMatch =
                        (f, a) -> f.getName().asString();
                    final PojoField.OnOptionalMatch<String> onOptionalMatch =
                        (f, a) -> {
                          addRef.set(w -> w.ref("java.util.Optional"));
                          return String.format("Optional.ofNullable(%s)", f.getName());
                        };
                    final PojoField.OnNoMatch<String> onNoMatch =
                        (f, a) -> {
                          throw new IllegalStateException(
                              "Field " + f + " does not match argument " + a);
                        };
                    return field.forArgument(argument, onExactMatch, onOptionalMatch, onNoMatch);
                  });

      return addRef
          .get()
          .apply(writer)
          .println(
              "return new %s(%s);",
              match.getConstructor().getName(), constructorParameters.mkString(", "));
    };
  }
}
