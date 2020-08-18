package editor.ui;

import editor.entity.component.Camera3DMovement;
import engine.Console;
import engine.scene.Scene;
import engine.ui.DebugGUI;
import imgui.ImBool;
import imgui.ImFloat;
import imgui.ImGui;
import imgui.enums.ImGuiCond;

public class EditorConfig extends DebugGUI
{
  private final ImBool physics = new ImBool(true);

  private final ImFloat cameraSpeed = new ImFloat(1);
  private final ImFloat cameraSensitivity = new ImFloat(1);

  @Override
  public void render(Scene scene)
  {
    if ((Boolean) Console.getConVar("editor_settings").get())
    {
      ImGui.setNextWindowSize(600, 150, ImGuiCond.Once);
      ImGui.setNextWindowPos(10, 10, ImGuiCond.Once);

      ImGui.begin("Editor");

      ImGui.beginTabBar("EditorTabs");

      if (ImGui.beginTabItem("Camera"))
      {
        ImGui.inputFloat("Speed", cameraSpeed);
        if (scene.hasEntity("editor-camera"))
        {
          scene.getEntity("editor-camera").getComponent(Camera3DMovement.class).speed = cameraSpeed.get();
        }

        ImGui.inputFloat("Sensitivity", cameraSensitivity);
        if (scene.hasEntity("editor-camera"))
        {
          scene.getEntity("editor-camera").getComponent(Camera3DMovement.class).sensitivity = cameraSensitivity.get();
        }

        ImGui.endTabItem();
      }

      if (ImGui.beginTabItem("Physics"))
      {
        ImBool enabled = new ImBool((Boolean) Console.getConVar("phys_enable").get());
        ImGui.checkbox("Enable Physics", enabled);
        Console.getConVar("phys_enable").set(enabled.get());

        ImGui.endTabItem();
      }

      ImGui.endTabBar();

      ImGui.end();
    }
  }
}
