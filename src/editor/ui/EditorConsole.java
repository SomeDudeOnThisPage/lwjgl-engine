package editor.ui;

import engine.Console;
import engine.scene.Scene;
import engine.ui.DebugGUI;
import imgui.ImGui;
import imgui.ImString;
import imgui.enums.*;

import java.util.AbstractMap;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER;

public class EditorConsole extends DebugGUI
{
  private static EditorConsole instance;
  private final ImString input;
  private boolean entering = false;

  private int ySizeLast = 0;

  public static EditorConsole getInstance()
  {
    return instance;
  }

  public void print(String... strings)
  {
    Console.print(strings);
  }

  @Override
  public void render(Scene scene)
  {
    if ((Boolean) Console.getConVar("editor_console").get())
    {
      ImGui.setNextWindowSize(500, 250, ImGuiCond.Once);
      ImGui.begin("Console");

      if (ImGui.beginTabBar("Tabs##TabBar"))
      {
        if (ImGui.beginTabItem("Console##Tab"))
        {
          // print history
          ImGui.pushStyleVar(ImGuiStyleVar.FrameBorderSize, 2.0f);
          ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 4.0f, 1.0f);
          ImGui.beginChildFrame(ImGui.getID("ConsoleContent"), 0, -25);

          for (AbstractMap.SimpleEntry<String, Console.MessageType> history : Console.history())
          {
            switch (history.getValue())
            {
              case DEFAULT -> ImGui.pushStyleColor(
                ImGuiCol.Text,
                Console.COLOR_DEFAULT.x,
                Console.COLOR_DEFAULT.y,
                Console.COLOR_DEFAULT.z,
                1.0f);
              case ERROR -> ImGui.pushStyleColor(
                ImGuiCol.Text,
                Console.COLOR_ERROR.x,
                Console.COLOR_ERROR.y,
                Console.COLOR_ERROR.z,
                1.0f);
              case WARNING -> ImGui.pushStyleColor(
                ImGuiCol.Text,
                Console.COLOR_WARNING.x,
                Console.COLOR_WARNING.y,
                Console.COLOR_WARNING.z,
                1.0f);
            }

            ImGui.text(history.getKey());
            ImGui.popStyleColor();
          }

          if (this.ySizeLast != Console.history().size())
          {
            ImGui.setScrollHereY();
            this.ySizeLast = Console.history().size();
          }

          ImGui.popStyleVar(2);
          ImGui.endChildFrame();

          ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 1, 1);
          ImGui.beginChildFrame(ImGui.getID("aaa#aaa"), 0, 0, ImGuiWindowFlags.NoScrollbar);
          ImGui.popStyleVar();

          ImGui.pushStyleVar(ImGuiStyleVar.FrameBorderSize, 1);
          ImGui.pushItemWidth(-75f);
          ImGui.inputText("", this.input);
          ImGui.popItemWidth();

          ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 1.0f, 0.0f);
          ImGui.sameLine();
          ImGui.pushItemWidth(-0.0000001f);
          if (!entering && (ImGui.button("Submit", 75f, 20.0f) || ImGui.getIO().getKeysDown(GLFW_KEY_ENTER)))
          {
            Console.print("] " + this.input.get());
            Console.parse(this.input.get());
            this.input.set("");
            entering = true;
          }
          else if (entering && !ImGui.getIO().getKeysDown(GLFW_KEY_ENTER))
          {
            ImGui.button("Submit", 75f, 20.0f); // so yea this is for show
            entering = false;
          }
          else
          {
            ImGui.button("Submit", 75f, 20.0f); // so yea this is for show
          }

          ImGui.popItemWidth();

          ImGui.popStyleVar(2);
          ImGui.endChildFrame();
          ImGui.endTabItem();
        }

        if (ImGui.beginTabItem("Log##Tab"))
        {
          // print history
          ImGui.pushStyleVar(ImGuiStyleVar.FrameBorderSize, 2.0f);
          ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 4.0f, 1.0f);
          ImGui.beginChildFrame(ImGui.getID("ConsoleContent"), 0, -25);
          ImGui.popStyleVar(2);
          ImGui.endChildFrame();
          ImGui.endTabItem();
        }

        ImGui.endTabBar();
      }

      ImGui.end();
    }
  }

  public EditorConsole()
  {
    EditorConsole.instance = this;
    this.input = new ImString();
  }
}
