package engine.platform.logging;

import editor.ui.EditorConsole;

public final class ConsoleLogger implements Logger
{
  public void warning(String message)
  {
    System.err.println("[WARNING] " + message + ".");
    EditorConsole console = EditorConsole.getInstance();
    if (console != null)
    {
      console.print("[WARNING] " + message + ".");
    }
  }

  public void info(String message)
  {
    System.out.println("[INFO] " + message + ".");
    EditorConsole console = EditorConsole.getInstance();
    if (console != null)
    {
      console.print("[INFO] " + message + ".");
    }
  }

  public void error(String message)
  {
    System.err.println("[ERROR] " + message + ".");
    EditorConsole console = EditorConsole.getInstance();
    if (console != null)
    {
      console.print("[ERROR] " + message + ".");
    }
  }
}
