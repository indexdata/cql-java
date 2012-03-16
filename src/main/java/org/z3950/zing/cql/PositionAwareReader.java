/*
 * Copyright (c) 1995-2012, Index Data
 * All rights reserved.
 * See the file LICENSE for details.
 */
package org.z3950.zing.cql;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;

/**
 * Reader proxy to count how many characters has been read so far.
 * @author jakub
 */
public class PositionAwareReader extends Reader {
  protected Reader reader;
  protected int pos = -1;
  
  public PositionAwareReader(Reader reader) {
    this.reader = reader;
  }

  /*
   * Position of the last read character or -1 if either reading from an empty 
   * stream or no 'read' has been invoked for this reader.
   */
  public int getPosition() {
    return pos;
  }

  @Override
  public void mark(int readAheadLimit) throws IOException {
    reader.mark(readAheadLimit);
  }

  @Override
  public boolean markSupported() {
    return reader.markSupported();
  }

  @Override
  public int read() throws IOException {
    int c = reader.read();
    if (c != -1) pos++;
    return c;
  }

  @Override
  public int read(char[] cbuf) throws IOException {
    int c = reader.read(cbuf);
    if (c != -1) pos+=c;
    return c;
  }

  @Override
  public int read(CharBuffer target) throws IOException {
    int c = reader.read(target);
    if (c != -1) pos+=c;
    return c;
  }
  
  @Override
  public int read(char[] cbuf, int off, int len) throws IOException {
    int c = reader.read(cbuf, off, len);
    if (c != -1) pos+=c;
    return c;
  }

  @Override
  public boolean ready() throws IOException {
    return reader.ready();
  }

  @Override
  public long skip(long n) throws IOException {
    return reader.skip(n);
  }
  
  @Override
  public void close() throws IOException {
    reader.close();
  }

  @Override
  public void reset() throws IOException {
    reader.reset();
  }
  
  //override object methods, to be on the safe-side

  @Override
  public boolean equals(Object obj) {
    return reader.equals(obj);
  }

  @Override
  public String toString() {
    return reader.toString();
  }

  @Override
  public int hashCode() {
    return reader.hashCode();
  }
  
}
