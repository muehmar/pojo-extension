package io.github.muehmar.pojoextension.generator.impl;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.Writer;
import java.util.Comparator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;

public final class WriterImpl implements Writer {
  private static final int DEFAULT_SPACES_PER_TAB = 2;
  private static final String NEWLINE_STRING = "\n";

  private final PList<String> refs;
  private final int refsLineNumber;
  private final NonEmptyList<UnaryOperator<StringBuilder>> lines;

  private final String tab;

  private final int tabs;
  private final boolean newline;

  private WriterImpl(
      PList<String> refs,
      int refsLineNumber,
      NonEmptyList<UnaryOperator<StringBuilder>> lines,
      String tab,
      int tabs,
      boolean newline) {
    this.refs = refs;
    this.refsLineNumber = refsLineNumber;
    this.lines = lines;
    this.tab = tab;
    this.tabs = tabs;
    this.newline = newline;
  }

  public static WriterImpl createDefault() {
    return ofSpacesPerIndent(DEFAULT_SPACES_PER_TAB);
  }

  public static WriterImpl ofSpacesPerIndent(int spacesPerIndent) {
    final String tab = new String(new char[spacesPerIndent]).replace("\0", " ");
    return new WriterImpl(
        PList.empty(), -1, NonEmptyList.single(UnaryOperator.identity()), tab, 0, true);
  }

  private WriterImpl appendToLastLine(Consumer<StringBuilder> c) {
    final UnaryOperator<StringBuilder> newLastLine =
        sb -> {
          lines.head().apply(sb);
          if (newline) {
            printTabs(tabs, sb);
          }
          c.accept(sb);
          return sb;
        };
    final NonEmptyList<UnaryOperator<StringBuilder>> newLines =
        new NonEmptyList<>(newLastLine, this.lines.tail());
    return new WriterImpl(refs, refsLineNumber, newLines, tab, tabs, false);
  }

  @Override
  public Writer print(String string, Object... args) {
    return appendToLastLine(sb -> sb.append(String.format(string, args)));
  }

  @Override
  public Writer println() {
    return new WriterImpl(refs, refsLineNumber, lines.cons(UnaryOperator.identity()), tab, 0, true);
  }

  @Override
  public Writer tab(int tabs) {
    return new WriterImpl(refs, refsLineNumber, lines, tab, tabs, newline);
  }

  @Override
  public Writer append(int tabs, Writer other) {
    final int usedRefsLineNumber =
        this.refsLineNumber > 0 ? this.refsLineNumber : other.getRefsLineNumber();

    final UnaryOperator<UnaryOperator<StringBuilder>> indentLines =
        uo ->
            sb -> {
              printTabs(tabs, sb);
              return uo.apply(sb);
            };

    final NonEmptyList<UnaryOperator<StringBuilder>> newLines =
        NonEmptyList.fromIter(other.getLines().map(indentLines).concat(getLines()))
            .orElse(NonEmptyList.single(UnaryOperator.identity()));

    return new WriterImpl(
        this.refs.concat(other.getRefs()),
        usedRefsLineNumber,
        newLines.cons(UnaryOperator.identity()),
        tab,
        0,
        true);
  }

  private void printTabs(int tabs, StringBuilder sb) {
    IntStream.range(0, tabs).forEach(i -> sb.append(tab));
  }

  @Override
  public PList<UnaryOperator<StringBuilder>> getLines() {
    return newline ? lines.toPList().drop(1) : lines.toPList();
  }

  @Override
  public Writer empty() {
    return new WriterImpl(
        PList.empty(), -1, NonEmptyList.single(UnaryOperator.identity()), tab, 0, true);
  }

  @Override
  public Writer ref(String ref) {
    return new WriterImpl(refs.cons(ref), refsLineNumber, lines, tab, tabs, newline);
  }

  @Override
  public PList<String> getRefs() {
    return refs;
  }

  @Override
  public Writer printRefs() {
    return new WriterImpl(refs, lines.size() - (newline ? 1 : 0), lines, tab, tabs, newline);
  }

  @Override
  public int getRefsLineNumber() {
    return refsLineNumber;
  }

  @Override
  public String asString() {
    final StringBuilder sb = new StringBuilder();
    final Consumer<UnaryOperator<StringBuilder>> applyStringBuilder =
        line -> {
          final StringBuilder tempSb = new StringBuilder();
          final String lineOutput = line.apply(tempSb).toString();
          if (lineOutput.trim().length() > 0) {
            sb.append(lineOutput);
          }
          sb.append(NEWLINE_STRING);
        };

    final PList<UnaryOperator<StringBuilder>> reversedLines = getLines().reverse();
    reversedLines.take(Math.max(refsLineNumber, 0)).forEach(applyStringBuilder);

    if (refsLineNumber >= 0) {
      refs.distinct(Function.identity())
          .sort(Comparator.comparing(Function.identity()))
          .filter(ref -> !ref.startsWith("java.lang"))
          .map(ref -> String.format("import %s;", ref))
          .forEach(ref -> sb.append(ref).append(NEWLINE_STRING));
    }

    reversedLines.drop(Math.max(refsLineNumber, 0)).forEach(applyStringBuilder);
    return sb.toString();
  }
}
