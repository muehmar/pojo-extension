package io.github.muehmar.pojoextension.generator;

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
}
