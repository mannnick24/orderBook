package com.cfbenchmarks.interview;

public class Validation {
  public static void validateArg( String arg, String message )
  {
    if ( arg == null || arg.trim().isEmpty() )
    {
      throw new IllegalArgumentException( message );
    }
  }
  
  public static void validateArg( Object arg, String message )
  {
    if ( arg == null )
    {
      throw new IllegalArgumentException( message );
    }
  }
}
