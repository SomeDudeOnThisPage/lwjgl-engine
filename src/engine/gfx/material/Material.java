package engine.gfx.material;

import engine.asset.Asset;
import engine.gfx.Bindable;
import engine.gfx.ShaderProgram;

public interface Material extends Asset, Bindable
{
  final class Properties
  {
    public boolean transparency;
  }

  ShaderProgram shader();
}
