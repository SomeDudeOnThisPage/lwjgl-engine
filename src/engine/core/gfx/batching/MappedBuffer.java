package engine.core.gfx.batching;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL44C.*;

public class MappedBuffer
{
  private int vbo;
  private int target;

  private int capacity;

  private ByteBuffer buffer;

  public void unmap()
  {
    glBindBuffer(this.target, this.vbo);
    glUnmapBuffer(this.target);
  }

  /**
   * Sets the capacity of the internal ByteBuffer, and remaps it if necessary.
   * @param capacity desired capacity
   */
  public void setCapacity(int capacity)
  {
    this.capacity = capacity * 2;

    if (this.buffer != null)
    {
      glUnmapBuffer(this.target);
      glDeleteBuffers(this.vbo);
    }

    this.vbo = glGenBuffers();
    glBindBuffer(this.target, this.vbo);
    glBufferData(this.target, this.capacity, GL_DYNAMIC_DRAW);
    //glBufferStorage(this.target, this.capacity, GL_MAP_WRITE_BIT | GL_MAP_PERSISTENT_BIT | GL_MAP_COHERENT_BIT);
    this.buffer = glMapBufferRange(this.target, 0, this.capacity, GL_MAP_WRITE_BIT | GL_MAP_UNSYNCHRONIZED_BIT, this.buffer);
  }

  public FloatBuffer mapf(int capacity)
  {
    return this.map(capacity).asFloatBuffer();
  }

  public IntBuffer mapi(int capacity)
  {
    return  this.map(capacity).asIntBuffer();
  }

  public ByteBuffer map(int capacity)
  {
    glBindBuffer(this.target, this.vbo);
    if(capacity > this.capacity)
    {
      this.setCapacity(capacity);
    }

    this.buffer.clear();
    return this.buffer;
  }

  public void bind()
  {
    glBindBuffer(this.target, this.vbo);
  }

  public MappedBuffer(int target)
  {
    this.target = target;
    this.vbo = glGenBuffers();
    this.capacity = 0;
  }
}