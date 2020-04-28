package engine.util.logging;

public class Logger
{
  public static void warning(String message)
  {
    System.err.println("[WARNING] " + message + ".");
  }

  public static void info(String message)
  {
    System.out.println("[INFO] " + message + ".");
  }

  public static void error(String message)
  {
    System.err.println("[ERROR] " + message + ".");
  }
}