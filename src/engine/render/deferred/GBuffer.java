package engine.render.deferred;

import engine.gfx.opengl.buffer.GLFrameBuffer;
import org.joml.Vector2i;

public class GBuffer extends GLFrameBuffer
{
  public GBuffer(Vector2i size)
  {
    super(size);
  }
}
