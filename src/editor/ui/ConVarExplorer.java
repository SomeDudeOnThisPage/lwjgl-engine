package editor.ui;

import engine.Console;
import engine.scene.Scene;
import engine.ui.DebugGUI;
import imgui.ImGui;

public class ConVarExplorer extends DebugGUI
{
  @Override
  public void render(Scene scene)
  {
    if ((Boolean) Console.getConVar("editor_convars").get())
    {
      int id = ImGui.getID("BASE");
    }
  }
}
