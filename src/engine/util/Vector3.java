package engine.util;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class Vector3<T> implements Iterable<T>
{
  public T x;
  public T y;
  public T z;

  private final class VectorIterator implements Iterator<T>
  {
    private final Vector3<T> current;
    private final int limit;
    private int position;

    @Override
    public boolean hasNext()
    {
      return this.position < this.limit - 1;
    }

    @Override
    public T next()
    {
      this.position++;
      return switch (this.position)
      {
        case 0 -> this.current.x;
        case 1 -> this.current.y;
        case 2 -> this.current.z;
        default -> null;
      };
    }

    public VectorIterator(Vector3<T> vector, int limit)
    {
      this.current = vector;
      this.limit = limit;
      this.position = 0;
    }
  }

  @NotNull @Override
  public Iterator<T> iterator()
  {
    return new VectorIterator(this, 3);
  }

  public Vector3()
  {
    this.x = null;
    this.y = null;
    this.z = null;
  }

  public Vector3(T x, T y, T z)
  {
    this.x = x;
    this.y = y;
    this.z = z;
  }
}
