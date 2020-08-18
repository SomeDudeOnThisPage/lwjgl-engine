package engine.gfx.opengl.buffer;

import engine.gfx.buffer.IndexBuffer;
import org.jetbrains.annotations.NotNull;

import static engine.Engine.Log;
import static org.lwjgl.opengl.GL42C.*;

public class GLIndexBuffer extends GLDataBuffer<Integer> implements IndexBuffer
{
  @Override
  public void dispose()
  {
    super.dispose();
    Log.info("disposed of IndexBuffer '" + this + "'");
  }

  public GLIndexBuffer(int usage, @NotNull Integer[] data)
  {
    this(usage);
    this.data(data);
  }

  public GLIndexBuffer(int usage)
  {
    super(GL_ELEMENT_ARRAY_BUFFER, GL_UNSIGNED_INT, usage);
  }
}
