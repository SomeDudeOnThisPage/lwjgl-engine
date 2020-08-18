package editor.ui;

import editor.DevelopmentScene3D;
import engine.Console;
import engine.entity.Behaviour;
import engine.entity.EditorField;
import engine.entity.Entity;
import engine.entity.EntityComponent;
import engine.scene.Scene;
import engine.ui.DebugGUI;
import imgui.ImBool;
import imgui.ImGui;
import imgui.enums.ImGuiCol;
import imgui.enums.ImGuiCond;

import java.lang.reflect.Field;

public class ComponentEditor extends DebugGUI
{
  private final ImBool be = new ImBool();
  private final ImBool bc = new ImBool();

  @Override
  public void render(Scene scene)
  {
    Entity entity = ((DevelopmentScene3D) scene).getSelectedEntity();

    if ((Boolean) Console.getConVar("component_editor").get())
    {
      String name = (entity == null) ? "Entity Explorer" : entity.name();

      ImGui.setNextWindowSize(350, scene.viewport().size().y - 25, ImGuiCond.Once);
      ImGui.setNextWindowPos(scene.viewport().size().x - 350, 25, ImGuiCond.Once);
      ImGui.begin(name + "###Components");
      if (entity != null)
      {
        if (ImGui.collapsingHeader("Info"))
        {
          ImGui.indent();

          ImGui.text("Name: " + entity.name());
          ImGui.text("UUID: " + entity.id());
          ImGui.text("Total Components: " + entity.components().size());
          ImGui.text("Total Scripts: " + entity.scripts().size());
          ImGui.separator();
          ImGui.text("Parent: " + ((entity.getParent() == null) ? "none" : entity.getParent().name()));
          ImGui.text("Total Children: " + entity.children().size());

          ImGui.unindent();
        }

        if (ImGui.collapsingHeader("Flags##EntityFlags"))
        {
          ImGui.indent();
          if ((Boolean) Console.getConVar("editor_ui_warnings").get())
          {
            ImGui.pushStyleColor(ImGuiCol.Text, 1.0f, 1.0f, 0.0f, 1.0f);
            ImGui.textWrapped("Warning: Flags are meant to be set programmatically, in order to restrict unintended behaviour." +
              "Changing flags in the editor may result in instability / crashing of the program.");
            ImGui.popStyleColor();
          }

          // display flags
          for (Entity.Flags flag : Entity.Flags.values())
          {
            this.be.set(entity.flagged(flag));
            ImGui.checkbox(flag.toString(), this.be);

            if (this.be.get())
            {
              entity.flag(flag);
            }
            else
            {
              entity.unflag(flag);
            }
          }

          ImGui.unindent();
        }
      }

      ImGui.separator();

      if (entity != null)
      {
        // display components
        ImGui.text("Components");
        for (EntityComponent component : entity.components())
        {
          if (ImGui.collapsingHeader(component.getClass().getSimpleName()))
          {
            ImGui.indent();
            if (ImGui.collapsingHeader("Flags##ComponentFlags" + component.toString()))
            {
              ImGui.indent();
              if ((Boolean) Console.getConVar("editor_ui_warnings").get())
              {
                ImGui.pushStyleColor(ImGuiCol.Text, 1.0f, 1.0f, 0.0f, 1.0f);
                ImGui.textWrapped("Warning: Flags are meant to be set programmatically, in order to restrict unintended behaviour." +
                  "Changing flags in the editor may result in instability / crashing of the program.");
                ImGui.popStyleColor();
              }

              // display flags
              for (EntityComponent.Flags flag : EntityComponent.Flags.values())
              {
                this.bc.set(component.flags().contains(flag));

                ImGui.checkbox(flag.toString() + "##" + component, this.bc);

                if (this.bc.get())
                {
                  component.flag(flag);
                }
                else
                {
                  component.unflag(flag);
                }
              }

              ImGui.unindent();
            }

            Class<? extends EntityComponent> clazz = component.getClass();
            for (Field field : clazz.getDeclaredFields())
            {
              if (field.isAnnotationPresent(EditorField.class))
              {
                try
                {
                  EditorFieldDisplay.render(field, component);
                }
                catch (Exception ignored) { /* imagine handling errors haha */ }
              }
            }
            ImGui.unindent();
          }
        }
        if (entity.components().size() == 0)
        {
          ImGui.indent();
          ImGui.text("This entity does not contain any components.");
          ImGui.unindent();
        }
      }

      ImGui.separator();

      if (entity != null)
      {
        // display scripts
        ImGui.text("Behaviours");
        for (Behaviour script : entity.scripts())
        {
          if (ImGui.collapsingHeader(script.getClass().getSimpleName()))
          {
            ImGui.indent();
            if (ImGui.collapsingHeader("Flags##ScriptFlags" + script.toString()))
            {
              ImGui.indent();

              if ((Boolean) Console.getConVar("editor_ui_warnings").get())
              {
                ImGui.pushStyleColor(ImGuiCol.Text, 1.0f, 1.0f, 0.0f, 1.0f);
                ImGui.textWrapped("Warning: Flags are meant to be set programmatically, in order to restrict unintended behaviour." +
                  "Changing flags in the editor may result in instability / crashing of the program.");
                ImGui.popStyleColor();
              }

              // display flags
              for (EntityComponent.Flags flag : EntityComponent.Flags.values())
              {
                this.bc.set(script.flags().contains(flag));

                ImGui.checkbox(flag.toString() + "##" + script, this.bc);

                if (this.bc.get())
                {
                  script.flag(flag);
                }
                else
                {
                  script.unflag(flag);
                }
              }

              ImGui.unindent();
            }

            Class<? extends EntityComponent> clazz = script.getClass();
            for (Field field : clazz.getDeclaredFields())
            {
              if (field.isAnnotationPresent(EditorField.class))
              {
                try
                {
                  EditorFieldDisplay.render(field, script);
                }
                catch (Exception ignored) {}
              }
            }
            ImGui.unindent();
          }
        }
        if (entity.scripts().size() == 0)
        {
          ImGui.indent();
          ImGui.text("This entity does not contain any behaviours.");
          ImGui.unindent();
        }
      }
      ImGui.end();
    }
  }
}
