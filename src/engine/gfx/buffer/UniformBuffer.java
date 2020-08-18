package engine.gfx.buffer;

import engine.asset.Asset;
import engine.gfx.Bindable;
import engine.gfx.uniform.BufferedUniform;

public interface UniformBuffer extends DataBuffer, Asset, Bindable
{
  BufferedUniform<?> getUniform(String uniform);
  void update(String index);
}
