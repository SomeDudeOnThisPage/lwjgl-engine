package engine.gfx.uniform;

import org.joml.*;

public class BufferedUniform<T>
{
  private static final int N = 4;

  protected String uniform;
  protected int alignment;
  private T data;

  public static int alignment(BufferedUniform<?> uniform)
  {
    if (uniform.data instanceof Integer || uniform.data instanceof Float)
    {
      return N;
    }
    else if (uniform.data instanceof Vector2f || uniform.data instanceof Vector2i)
    {
      return 2 * N;
    }
    else if (uniform.data instanceof Vector3f || uniform.data instanceof Vector3i || uniform.data instanceof Vector4f || uniform.data instanceof Vector4i)
    {
      return 4 * N;
    }
    else if (uniform.data instanceof Matrix3f || uniform.data instanceof Matrix4f)
    {
      // are 3-column/row matrices stored with a base alignment of 16N? Test this!
      return 4 * 4 * N;
    }
    throw new UnsupportedOperationException("could not calculate base uniform buffer alignment for data type '"
      + uniform.data.getClass().getSimpleName() + "'");
  }

  public T get()
  {
    return this.data;
  }

  public void set(T data)
  {
    this.data = data;
  }

  public String name()
  {
    return this.uniform;
  }

  public void name(String name)
  {
    this.uniform = name;
  }

  public int alignment()
  {
    return this.alignment;
  }

  protected BufferedUniform() {}

  public BufferedUniform(String uniform, T data)
  {
    this.data = data;
    this.alignment = BufferedUniform.alignment(this);
    this.uniform = uniform;
  }
}
