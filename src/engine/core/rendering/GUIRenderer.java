package engine.core.rendering;

import engine.core.gui.GUIRoot;
import engine.core.scene.Scene;

public class GUIRenderer extends Renderer
{
  @Override
  public void render(Scene scene)
  {
    GUIRoot gui = scene.gui();

    // traverse GUI tree and render elements
  }
}