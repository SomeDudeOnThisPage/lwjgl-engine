package engine.gfx.opengl.buffer;

import engine.gfx.buffer.VertexBuffer;
import engine.gfx.buffer.VertexBufferLayout;

public class GLVertexBuffer extends GLDataBuffer<Float> implements VertexBuffer
{
  private VertexBufferLayout layout;

  @Override
  public void layout(VertexBufferLayout layout)
  {
    this.layout = layout;
  }

  @Override
  public VertexBufferLayout layout()
  {
    return this.layout;
  }

  public GLVertexBuffer(int target, int type, int usage)
  {
    this(target, type, usage, null);
  }

  public GLVertexBuffer(int target, int type, int usage, Float[] data)
  {
    super(target, type, usage);

    if (this.data != null)
    {
      this.data(data);
    }
  }
}
