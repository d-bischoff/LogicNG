package org.logicng.io.parsers;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Daniel Bischoff on 14.01.2016. Copyright by Daniel Bischoff
 */
public final class ForbearingParser {

  private final FormulaFactory f;
  private final org.logicng.io.parsers.ForbearingParserLexer lexer;
  private final org.logicng.io.parsers.ForbearingParserParser parser;

  /**
   * Constructs a new parser.
   *
   * @param f the formula factory
   */
  public ForbearingParser(final FormulaFactory f) {
    this.f = f;
    ANTLRInputStream input = new ANTLRInputStream();
    this.lexer = new org.logicng.io.parsers.ForbearingParserLexer(input);
    CommonTokenStream tokens = new CommonTokenStream(this.lexer);
    this.parser = new org.logicng.io.parsers.ForbearingParserParser(tokens);
    this.parser.setFormulaFactory(f);
    this.lexer.removeErrorListeners();
    this.parser.removeErrorListeners();
    this.parser.setErrorHandler(new BailErrorStrategy());
  }

  /**
   * Parses and returns a given string.
   *
   * @param in a string
   * @return the {@link Formula} representation of this string
   * @throws ParserException if the string was not a valid formula
   */
  public Formula parse(final String in) throws ParserException {
    if (in == null || in.isEmpty())
      return f.verum();
    return this.parse(new ByteArrayInputStream(in.getBytes()));
  }

  /**
   * Parses and returns a given input stream.
   *
   * @param inputStream an input stream
   * @return the {@link Formula} representation of this stream
   * @throws ParserException if there was a problem with the input stream
   */
  @SuppressWarnings("WeakerAccess")
  public Formula parse(InputStream inputStream) throws ParserException {
    try {
      ANTLRInputStream input = new ANTLRInputStream(inputStream);
      this.lexer.setInputStream(input);
      CommonTokenStream tokens = new CommonTokenStream(this.lexer);
      this.parser.setInputStream(tokens);
      return this.parser.formula().f;
    } catch (IOException e) {
      throw new ParserException("IO exception when parsing the formula", e);
    } catch (ParseCancellationException e) {
      throw new ParserException("Parse cancellation exception when parsing the formula", e);
    } catch (LexerException e) {
      throw new ParserException("Lexer exception when parsing the formula.", e);
    }
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName();
  }
}


