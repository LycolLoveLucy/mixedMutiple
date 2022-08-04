
package com.jielu.log;

public interface Log {

  boolean isDebugEnabled();

  boolean isTraceEnabled();

  void error(String s, Throwable e);

  void error(String s);

  void error(String format,Object...params);

  void debug(String s);

  void trace(String s);

  void trace(String format,Object...params);


  void warn(String s);

  void warn(String format,Object...params);


  void info(String s);

  void info(String format,Object...params);

}
