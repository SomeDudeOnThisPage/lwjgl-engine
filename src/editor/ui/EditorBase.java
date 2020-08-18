package editor.ui;

import engine.Console;
import engine.scene.Scene;
import engine.ui.DebugGUI;
import imgui.ImBool;
import imgui.ImGui;

public class EditorBase extends DebugGUI
{
  public void render(Scene scene)
  {
    ImGui.begin("Engine");
    ImGui.beginMenuBar();

    if (ImGui.beginMenu("File"))
    {
      ImGui.menuItem("Load");
      ImGui.menuItem("Save");
      ImGui.menuItem("Save As");
      ImGui.menuItem("Exit");
      ImGui.endMenu();
    }

    if (ImGui.beginMenu("Edit"))
    {
      if (ImGui.beginMenu("New..."))
      {
        ImGui.menuItem("Game Object");
        ImGui.menuItem("Entity (Empty)");
        ImGui.endMenu();
      }
      ImGui.endMenu();
    }

    if (ImGui.beginMenu("Workspace"))
    {
      final ImBool b = new ImBool();
      b.set((Boolean) Console.getConVar("editor_scenegraph").get());
      ImGui.menuItem("Scene Graph", "", b);
      Console.getConVar("editor_scenegraph").set(b.get());

      b.set((Boolean) Console.getConVar("editor_assets").get());
      ImGui.menuItem("Asset Explorer", "", b);
      Console.getConVar("editor_assets").set(b.get());

      b.set((Boolean) Console.getConVar("editor_collections").get());
      ImGui.menuItem("Entity Collections", "", b);
      Console.getConVar("editor_collections").set(b.get());

      b.set((Boolean) Console.getConVar("editor_settings").get());
      ImGui.menuItem("Editor Settings", "", b);
      Console.getConVar("editor_settings").set(b.get());

      ImGui.separator();
      b.set((Boolean) Console.getConVar("editor_ui_warnings").get());
      ImGui.menuItem("UI Warnings", "", b);
      Console.getConVar("editor_ui_warnings").set(b.get());

      ImGui.endMenu();
    }

    ImGui.endMenuBar();
    ImGui.end();
  }
}
