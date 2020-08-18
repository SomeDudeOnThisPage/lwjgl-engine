package engine.gfx.opengl.buffer;

import engine.Engine;
import engine.gfx.BaseAssetBindable;
import engine.gfx.buffer.DataBuffer;
import engine.gfx.opengl.GLState;
import engine.util.ArrayUtil;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL42C.*;

public class GLDataBuffer<T extends Number> extends BaseAssetBindable implements DataBuffer
{
  protected final int target;
  protected final int usage;
  protected int size;

  protected ByteBuffer data;

  @Override
  public int size()
  {
    return this.size;
  }

  public void data(@NotNull T[] data)
  {
    ByteBuffer buffer = BufferUtils.createByteBuffer(data.length * 4);

    if (data instanceof Integer[])
    {
      buffer.asIntBuffer().put(ArrayUtil.toPrimitiveI((Integer[]) data));
      buffer.asIntBuffer().flip();
      this.size = buffer.asIntBuffer().capacity();
    }
    else if (data instanceof Float[])
    {
      buffer.asFloatBuffer().put(ArrayUtil.toPrimitiveF((Float[]) data));
      buffer.asFloatBuffer().flip();
      this.size = buffer.asFloatBuffer().capacity();
    }
    else
    {
      throw new UnsupportedOperationException("cannot create data buffer with data type '" + data.getClass() + "'");
    }

    this.data = buffer;

    this.bind();
    glBufferData(this.target, this.data, this.usage);
    this.unbind();
  }

  @Override
  public void bind()
  {
    GLState.buffer(this.target, this.id);
  }

  @Override
  public void unbind()
  {
    GLState.buffer(this.target, GL_NONE);
  }

  @Override
  public void dispose()
  {
    if (GLState.buffer(this.target) == this.id)
    {
      this.unbind();
    }

    Engine.Log.info("destroyed GLDataBuffer '" + this.id + "'");
    glDeleteBuffers(this.id);
  }

  public GLDataBuffer(int target, int type, int usage)
  {
    this.id = glGenBuffers();

    if (target != GL_ARRAY_BUFFER && target != GL_ELEMENT_ARRAY_BUFFER && target != GL_DRAW_INDIRECT_BUFFER && target != GL_UNIFORM_BUFFER)
    {
      throw new IllegalArgumentException("target must be either GL_ARRAY_BUFFER, GL_ELEMENT_ARRAY_BUFFER or GL_DRAW_INDIRECT_BUFFER");
    }
    this.target = target;

    if (type != GL_FLOAT && type != GL_UNSIGNED_BYTE && type != GL_UNSIGNED_INT)
    {
      throw new IllegalArgumentException("type must be either GL_FLOAT, GL_UNSIGNED_BYTE or GL_UNSIGNED_INT");
    }

    if (usage != GL_STATIC_DRAW && usage != GL_DYNAMIC_DRAW && usage != GL_STREAM_DRAW)
    {
      throw new IllegalArgumentException("usage must be either GL_STATIC_DRAW, GL_DYNAMIC_DRAW or GL_STREAM_DRAW");
    }
    this.usage = usage;
  }
}
