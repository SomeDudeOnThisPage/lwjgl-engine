package editor.ui;

import engine.Console;
import engine.asset.Asset;
import engine.asset.AssetFamily;
import engine.scene.Scene;
import engine.ui.DebugGUI;
import imgui.ImGui;
import imgui.enums.ImGuiCond;

import java.util.Collection;

import static engine.Engine.*;

public class EditorAssets extends DebugGUI
{
  private Class<? extends Asset> selected = null;
  private Asset asset = null;

  @Override
  public void render(Scene scene)
  {
    if ((Boolean) Console.getConVar("editor_assets").get())
    {
      ImGui.setNextWindowSize(600, 600, ImGuiCond.Once);
      ImGui.setNextWindowPos(5, 25, ImGuiCond.Once);
      ImGui.begin("Assets");

      String current = "Types...";
      if (selected != null)
      {
        current = selected.getSimpleName();
      }

      Collection<Class<? extends Asset>> families = AssetManager.getFamilyClasses();

      ImGui.setNextItemWidth(-0.000001f);
      if (ImGui.beginCombo("##assets", current))
      {
        for (Class<? extends Asset> family : families)
        {
          if (ImGui.selectable(family.getSimpleName()))
          {
            this.selected = family;
          }
        }
        ImGui.endCombo();
      }

      if (this.selected != null)
      {
        AssetFamily<? extends Asset> family = AssetManager.getFamily(this.selected);
        ImGui.text(family.getClass().getSimpleName() + " " + family.family().getSimpleName());
        ImGui.text("Total assets: " + family.get().size());

        ImGui.separator();

        ImGui.columns(3);
        // headers
        ImGui.text("Asset ID");
        ImGui.nextColumn();
        ImGui.text("References");
        ImGui.nextColumn();
        // empty
        ImGui.nextColumn();

        ImGui.columns(1);

        ImGui.separator();

        for (Asset asset : family.get())
        {
          ImGui.columns(3);

          if (ImGui.selectable(asset.key(), this.asset == asset))
          {
            this.asset = asset;
          }

          ImGui.nextColumn();
          ImGui.text(asset.references() + ""); // for fucks sake, when making an API that accepts some print text,
                                               // accept Objects and use toString in your API!!!
          ImGui.nextColumn();

          String src = asset.source();

          if (src != null)
          {
            ImGui.sameLine();
            if (ImGui.button("Reload"))
            {
              System.err.println("saas " + src);
              AssetManager.load(src);
            }
          }

          ImGui.nextColumn();
          ImGui.columns(1);
        }
        ImGui.separator();

        if (this.asset != null)
        {
          ImGui.beginChildFrame(ImGui.getID("ASSETVIEW"), -0.0001f, -0.0001f);
          EditorAssetDisplay.render(family.family(), this.asset);
          ImGui.endChildFrame();
        }
      }

      ImGui.end();
    }
  }
}
