package editor.ui;

import engine.Console;
import engine.Engine;
import engine.asset.Asset;

import engine.gfx.ShaderProgram;
import engine.gfx.Texture2D;
import imgui.ImGui;

import java.util.HashMap;

public interface EditorAssetDisplay
{
  HashMap<Class<? extends Asset>, EditorAssetDisplay> renderers = new HashMap<>()
  {{
    put(Texture2D.class, (asset) ->
    {
      Texture2D texture = (Texture2D) asset;

      float x = ImGui.getWindowWidth();

      float sx = Math.min(texture.size().x, x);
      float sy = Math.min(texture.size().y, x * (texture.size().y / (float) texture.size().x));

      ImGui.text("Source: " + texture.source());
      ImGui.text("Dimensions:  " + texture.size().x + "x" + texture.size().y + "px");
      ImGui.image(texture.id(), sx, sy);
    });

    put(ShaderProgram.class, (asset) ->
    {
      ShaderProgram shader = (ShaderProgram) asset;
      ImGui.text(shader.source());
    });
  }};

  static <T extends Asset> void render(Class<? extends Asset> family, T asset)
  {
    try
    {
      if (!EditorAssetDisplay.renderers.containsKey(family))
      {
        throw new UnsupportedOperationException("cannot render display for asset of type '" + family + "' - " +
          "no renderer found for family '" + family.getSimpleName() + "'");
      }

      EditorAssetDisplay.renderers.get(family).display(family.cast(asset));
    }
    catch (Exception e)
    {
      Console.error("cannot render display for asset of type '" + family + "' - " +
        "rendering failed");
      Engine.Log.error(e.getMessage());
    }
  }

  void display(Asset asset) throws Exception;
}
