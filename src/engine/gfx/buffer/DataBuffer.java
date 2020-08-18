package engine.gfx.buffer;

import engine.asset.Asset;
import engine.gfx.Bindable;

public interface DataBuffer extends Asset, Bindable
{
  int size();
}
