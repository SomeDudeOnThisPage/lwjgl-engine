package engine.core.gfx.shadow;

import engine.core.gfx.FrameBuffer;
import org.lwjgl.BufferUtils;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL30C.*;

public class ShadowMapBuffer extends FrameBuffer
{
  private IntBuffer buffers;

  @Override
  public void clear()
  {
    this.bind();
    glDrawBuffers(this.buffers);
    glClear(GL_DEPTH_BUFFER_BIT);
  }

  public void bind(int map)
  {
    this.bind();
    glDrawBuffers(map);
  }

  public ShadowMapBuffer(int resolution, int max)
  {
    super(resolution, resolution);

    this.buffers = BufferUtils.createIntBuffer(max);
    for (int i = 0; i < max; i++)
    {
      this.buffers.put(GL_COLOR_ATTACHMENT0 + max);
    }
    this.buffers.flip();
  }
}