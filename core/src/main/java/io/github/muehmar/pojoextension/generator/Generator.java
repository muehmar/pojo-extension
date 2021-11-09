package io.github.muehmar.pojoextension.generator;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.writer.Writer;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public interface Generator<A, B> {
  Writer generate(A data, B settings, Writer writer);

  static <A, B> Generator<A, B> ofWriterFunction(UnaryOperator<Writer> f) {
    return (data, settings, writer) -> f.apply(writer);
  }

  static <A, B> Generator<A, B> emptyGen() {
    return (data, settings, writer) -> writer;
  }

  default Generator<A, B> appendNewLine() {
    return append((UnaryOperator<Writer>) Writer::println);
  }

  default Generator<A, B> append(Generator<A, B> next) {
    final Generator<A, B> self = this;
    return (data, settings, writer) ->
        next.generate(data, settings, self.generate(data, settings, writer));
  }

  default Generator<A, B> append(Generator<A, B> next, int tabs) {
    final Generator<A, B> self = this;
    return (data, settings, writer) ->
        self.generate(data, settings, writer)
            .append(tabs, next.generate(data, settings, writer.empty()));
  }

  default Generator<A, B> append(UnaryOperator<Writer> next) {
    return append((data, settings, writer) -> next.apply(writer));
  }

  default <C> Generator<A, B> append(Generator<C, B> gen, Function<A, C> f) {
    final Generator<A, B> self = this;
    return (data, settings, writer) ->
        gen.generate(f.apply(data), settings, self.generate(data, settings, writer));
  }

  default <C> Generator<A, B> appendList(Generator<C, B> gen, Function<A, PList<C>> f) {
    final Generator<A, B> self = this;
    return (data, settings, writer) ->
        f.apply(data)
            .foldLeft(self, (g, e) -> g.append(gen, ignore -> e))
            .generate(data, settings, writer);
  }

  default Generator<A, B> appendConditionally(BiPredicate<A, B> predicate, Generator<A, B> append) {
    final Generator<A, B> self = this;
    return (data, settings, writer) -> {
      if (predicate.negate().test(data, settings)) {
        return self.generate(data, settings, writer);
      }
      return append(append).generate(data, settings, writer);
    };
  }

  default Generator<A, B> appendConditionally(Predicate<A> predicate, Generator<A, B> append) {
    return appendConditionally((data, settings) -> predicate.test(data), append);
  }
}
