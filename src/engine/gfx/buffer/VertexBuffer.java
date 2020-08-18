package engine.gfx.buffer;

public interface VertexBuffer extends DataBuffer
{
  void layout(VertexBufferLayout layout);
  VertexBufferLayout layout();
}
