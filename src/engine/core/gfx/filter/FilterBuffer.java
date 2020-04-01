package engine.core.gfx.filter;

import engine.core.gfx.FrameBuffer;
import engine.core.gfx.texture.Texture;
import org.lwjgl.BufferUtils;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11C.GL_RGB;
import static org.lwjgl.opengl.GL30C.*;

/**
 * A {@link FilterBuffer} is a double-buffered {@link FrameBuffer} implementing a swapchain to apply any number of
 * {@link Filter}s. Note that each {@link Filter} requires a separate (quad + effects-shader) render pass.
 */
public class FilterBuffer extends FrameBuffer
{
  private IntBuffer buffers;

  private Texture t1;
  private Texture t2;

  private boolean current;

  @Override
  public void clear()
  {
    this.bind();
    glDrawBuffers(this.buffers);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
  }

  public void begin(Texture initial)
  {
    this.current = false;
    initial.bind(Filter.FILTER_COLOR_BUFFER_BINDING);
    glDrawBuffers(GL_COLOR_ATTACHMENT0);
  }

  public void apply(Filter filter)
  {
    filter.apply();
    this.swap();
  }

  private void swap()
  {
    if (this.current)
    {
      this.t2.bind(Filter.FILTER_COLOR_BUFFER_BINDING);
      glDrawBuffers(GL_COLOR_ATTACHMENT1);
    }
    else
    {
      this.t1.bind(Filter.FILTER_COLOR_BUFFER_BINDING);
      glDrawBuffers(GL_COLOR_ATTACHMENT0);
    }

    this.current = !this.current;
  }

  public int getCurrentAttachment()
  {
    if (this.current)
    {
      return GL_COLOR_ATTACHMENT1;
    }
    else
    {
      return GL_COLOR_ATTACHMENT0;
    }
  }

  public FilterBuffer(int width, int height)
  {
    super(width, height);

    this.buffers = BufferUtils.createIntBuffer(2);
    this.buffers.put(GL_COLOR_ATTACHMENT0);
    this.buffers.put(GL_COLOR_ATTACHMENT1);
    this.buffers.flip();

    this.bind();

    this.t1 = new Texture(width, height, GL_RGB, GL_RGB, false);
    this.t2 = new Texture(width, height, GL_RGB, GL_RGB, false);

    this.addTexture(this.t1, GL_COLOR_ATTACHMENT0);
    this.addTexture(this.t1, GL_COLOR_ATTACHMENT1);

    this.current = false;
  }
}