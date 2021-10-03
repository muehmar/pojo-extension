package io.github.muehmar.pojoextension.generator.writer;

import ch.bluecare.commons.data.PList;

/** Instances of this writer are immutable. */
public interface Writer {
  Writer empty();

  Writer ref(String ref);

  PList<String> getRefs();

  Writer printRefs();

  int getRefsLineNumber();

  default Writer print(char value) {
    return print("" + value);
  }

  default Writer print(int value) {
    return print("" + value);
  }

  default Writer print(String string) {
    return print(string, new Object[0]);
  }

  Writer print(String string, Object... args);

  Writer println();

  default Writer println(char value) {
    return print(value).println();
  }

  default Writer println(int value) {
    return print(value).println();
  }

  default Writer println(String string) {
    return print(string).println();
  }

  default Writer println(String string, Object... args) {
    return print(string, args).println();
  }

  Writer tab(int tabs);

  default Writer append(Writer other) {
    return append(0, other);
  }

  Writer append(int tabs, Writer other);

  PList<Line> getLines();

  String asString();
}
