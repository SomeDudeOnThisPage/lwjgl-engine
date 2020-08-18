package engine.gfx.opengl.buffer;

import engine.exception.GLException;
import engine.gfx.*;
import engine.gfx.buffer.IndexBuffer;
import engine.gfx.buffer.VertexArray;
import engine.gfx.buffer.VertexBuffer;
import engine.gfx.buffer.VertexBufferLayout;
import engine.gfx.opengl.GLState;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;

import static org.lwjgl.opengl.GL42C.*;

public class GLVertexArray extends BaseAssetBindable implements VertexArray
{
  private static int bound = -1;

  private final HashSet<Integer> attributes;
  private final ArrayList<VertexBuffer> buffers;
  private IndexBuffer indices;

  /**
   * Returns true if this {@link GLVertexArray} uses indexing.
   * <p>
   *   An indexed {@link GLVertexArray} should be rendered by using
   *   {@link org.lwjgl.opengl.GL42C#glDrawElements(int, int, int, long)}.
   * </p>
   * @return True if this {@link GLVertexArray} is indexed.
   */
  @Override
  public boolean indexed()
  {
    return this.indices != null;
  }

  /**
   * In case that this {@link VertexArray} uses indexing, this method returns the amount of indices present in the
   * {@link IndexBuffer} buffer. In other cases, it returns the amount of elements inside the {@link VertexBuffer} at
   * location {@code 0} ({@code v_position}).
   * @return Count of elements / elements inside the {@link VertexBuffer} at location {@code 0}.
   */
  @Override
  public int count()
  {
    if (this.indexed())
    {
      return this.indices.size();
    }
    return this.buffers.get(0).size();
  }

  /**
   * Adds a {@link VertexBuffer} to this {@link VertexArray}, binding it to the locations set in the {@link VertexBuffer}s'
   * {@link VertexBufferLayout}.
   * @param buffer The {@link VertexBuffer} to be added to this {@link VertexArray}.
   */
  @Override
  public void addVertexBuffer(@NotNull VertexBuffer buffer)
  {
    this.bind();
    buffer.bind();

    VertexBufferLayout layout = buffer.layout();

    for (VertexBufferLayout.BufferElement element : layout.elements())
    {
      GLState.attribute(element.location(), true);
      glVertexAttribPointer(
        element.location(),
        element.type().components(),
        element.type().gltype(),
        false, // todo: this should probably be stored in the BufferElement...
        layout.stride(),
        element.offset()
      );
      GLState.attribute(element.location(), false);

      if (this.attributes.contains(element.location()))
      {
        throw new GLException(this, "glVertexAttribPointer", "attribute at location '" + element.location() + "' is already defined");
      }

      this.attributes.add(element.location());
    }

    this.buffers.add(buffer);

    this.unbind();
    buffer.unbind();
  }

  @Override
  public void setIndexBuffer(@NotNull IndexBuffer buffer)
  {
    if (!(buffer instanceof GLIndexBuffer))
    {
      throw new UnsupportedOperationException("cannot bind a non-OpenGL IndexBuffer to an OpenGL VertexArray");
    }

    this.bind();
    buffer.bind();

    // aaand that's it, binding an element buffer to a vertex array is weird...

    buffer.unbind();
    this.unbind();

    this.indices = buffer;
  }

  @Override
  public void bind()
  {
    if (GLVertexArray.bound != this.id)
    {
      GLVertexArray.bound = this.id;
      glBindVertexArray(this.id);

      for (Integer location : this.attributes)
      {
        GLState.attribute(location, true);
      }

      if (this.indexed())
      {
        this.indices.unbind();
        this.indices.bind();
      }
    }
  }

  @Override
  public void unbind()
  {
    GLVertexArray.bound = -1;
    glBindVertexArray(GL_NONE);

    for (Integer location : this.attributes)
    {
      GLState.attribute(location, false);
    }

    if (this.indexed())
    {
      this.indices.unbind();
    }
  }

  @Override
  public void dispose()
  {
    for (VertexBuffer buffer : this.buffers)
    {
      buffer.dispose();
    }

    for (Integer location : this.attributes)
    {
      GLState.attribute(location, false);
    }

    if (this.indexed())
    {
      this.indices.dispose();
    }

    glDeleteVertexArrays(this.id);
  }

  public GLVertexArray()
  {
    this.id = glGenVertexArrays();
    this.buffers = new ArrayList<>();
    this.attributes = new HashSet<>();
  }
}
