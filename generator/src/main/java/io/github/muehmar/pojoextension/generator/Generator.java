package io.github.muehmar.pojoextension.generator;

public interface Generator<A, B> {
  Writer generate(A data, B settings, Writer writer);

  default Generator<A, B> append(Generator<A, B> next) {
    final Generator<A, B> self = this;
    return (data, settings, writer) ->
        next.generate(data, settings, self.generate(data, settings, writer));
  }
}
