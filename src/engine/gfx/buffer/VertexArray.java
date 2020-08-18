package engine.gfx.buffer;

import engine.asset.Asset;
import engine.gfx.Bindable;

public interface VertexArray extends Asset, Bindable
{
  boolean indexed();
  int count();
  void addVertexBuffer(VertexBuffer buffer);
  void setIndexBuffer(IndexBuffer buffer);
}
