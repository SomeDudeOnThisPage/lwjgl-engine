package editor.ui;

import editor.DevelopmentScene3D;
import engine.Console;
import engine.entity.Entity;
import engine.entity.EntityCollection;
import engine.entity.EntitySystem;
import engine.scene.Scene;
import engine.ui.DebugGUI;
import imgui.ImGui;
import imgui.enums.ImGuiCond;

public class EditorSystems extends DebugGUI
{
  private Class<? extends EntityCollection> selectedCollection = null;

  @Override
  public void render(Scene scene)
  {
    if ((Boolean) Console.getConVar("editor_collections").get())
    {
      ImGui.setNextWindowSize(600, 600, ImGuiCond.Once);
      ImGui.setNextWindowPos(5, 25, ImGuiCond.Once);
      ImGui.begin("Systems & Collections");

      String current = "Collections...";
      if (this.selectedCollection != null)
      {
        current = this.selectedCollection.getSimpleName();
      }

      ImGui.setNextItemWidth(-0.01f);
      if (ImGui.beginCombo("##collections", current))
      {
        for (EntityCollection collection : scene.ecs().collections().values())
        {
          if (ImGui.selectable(collection.getClass().getSimpleName()))
          {
            this.selectedCollection = collection.getClass();
          }
        }
        ImGui.endCombo();
      }

      // collection editor
      if (this.selectedCollection != null)
      {
        EntityCollection selected = scene.ecs().collections().get(this.selectedCollection);
        ImGui.separator();
        ImGui.text("Assigned Entities");

        if (ImGui.beginChildFrame(ImGui.getID("Systems & Collections"), -0.01f, 250f))
        {
          for (Entity entity : selected.getEntities())
          {
            if (ImGui.selectable(entity.name()))
            {
              // select entity in scene graph, if the window exists
              if (DebugGUI.guis.containsKey(EntityList.class))
              {
                ((DevelopmentScene3D) scene).setSelectedEntity(entity);
              }
            }
          }
          ImGui.endChildFrame();
        }

        // (update) system-specific
        if (selected instanceof EntitySystem)
        {
          ImGui.separator();
          ImGui.text("Priority: " + ((EntitySystem) selected).priority());
        }
      }

      ImGui.end();
    }
  }
}
