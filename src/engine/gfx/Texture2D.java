package engine.gfx;

import engine.asset.Asset;
import org.joml.Vector2i;

public interface Texture2D extends Asset, Bindable
{
  void bind(int slot);
  void slot(int slot);
  Vector2i size();
  int getTextureSlot();
}
