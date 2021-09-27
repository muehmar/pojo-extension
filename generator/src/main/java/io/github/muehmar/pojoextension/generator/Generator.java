package io.github.muehmar.pojoextension.generator;

import ch.bluecare.commons.data.PList;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public interface Generator<A, B> {
  Writer generate(A data, B settings, Writer writer);

  static <A, B> Generator<A, B> ofWriterFunction(UnaryOperator<Writer> f) {
    return (data, settings, writer) -> f.apply(writer);
  }

  default Generator<A, B> append(Generator<A, B> next) {
    final Generator<A, B> self = this;
    return (data, settings, writer) ->
        next.generate(data, settings, self.generate(data, settings, writer));
  }

  default Generator<A, B> append(int tabs, Generator<A, B> next) {
    final Generator<A, B> self = this;
    return (data, settings, writer) ->
        self.generate(data, settings, writer)
            .append(tabs, next.generate(data, settings, writer.empty()));
  }

  default Generator<A, B> append(UnaryOperator<Writer> append) {
    final Generator<A, B> self = this;
    return (data, settings, writer) -> append.apply(self.generate(data, settings, writer));
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
}
