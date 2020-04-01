package engine.core.gfx;

import org.lwjgl.BufferUtils;
import java.nio.*;
import java.util.ArrayList;
import static org.lwjgl.opengl.GL33C.*;

public class VertexArray
{
  public static final int VERTEX_3F_POINTER = 0;
  public static final int TEXTURE_2F_POINTER = 1;
  public static final int NORMAL_3F_POINTER = 2;
  public static final int TANGENT_3F_POINTER = 3;
  public static final int BITANGENT_3F_POINTER = 4;

  public static final VertexArray empty = new VertexArray();

  /**
   * Maximum number of attributes, ranging from 0 to MAX_ATTRIBUTES - 1.
   */
  protected static final int MAX_ATTRIBUTES = 8;

  /**
   * Currently enabled vertex array, so we do not accidentally enable the already bound vertex array again.
   */
  private static int current = 0;

  /**
   * Numerical ID of the vertex array.
   */
  private int id;

  /**
   * Amount of vertices to be rendered. Set when setting attribute 0.
   */
  private int vcount;

  /**
   Enabled attributes for this vertex array.
   */
  protected boolean[] attributes;

  /**
   * Stores the indices of the loaded VertexBufferObjects used by this VertexArray
   */
  private ArrayList<Integer> vbo_loaded = new ArrayList<>();

  /**
   * Numerical ID of the index buffer.<br>If indexing is not used, this will be -1.
   */
  protected int indices;

  public static void preRenderPass()
  {
    for (int i = 0; i < MAX_ATTRIBUTES; i++)
    {
      glEnableVertexAttribArray(i);
    }
  }

  public static void postRenderPass()
  {
    for (int i = 0; i < MAX_ATTRIBUTES; i++)
    {
      glDisableVertexAttribArray(i);
    }
  }

  /**
   * Bind the vertex array and enable all attribute arrays used.
   */
  public void bind()
  {
    // Check if the vertex array is already enabled.
    if (current != id)
    {
      glBindVertexArray(id);

      if (indices != -1)
      {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indices);
      }

      current = id;
    }
  }

  /**
   * Unbind the vertex array and disable all attribute arrays used.
   */
  public void unbind()
  {
    // Do not unbind random vertex arrays, only unbind if we are sure it's the current one.
    if (current == id)
    {
      if (indices != -1)
      {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
      }

      glBindVertexArray(0);

      current = 0;
    }
  }

  /**
   * Creates an index buffer from the given arguments. This will switch the VAO to render with elements.
   * @param indices Indices
   */
  public void addIndices(int[] indices)
  {
    this.vcount = indices.length;

    bind();

    // Store initial data
    IntBuffer buffer = BufferUtils.createIntBuffer(indices.length);
    buffer.put(indices);
    buffer.flip();

    this.indices = glGenBuffers();
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.indices);
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);

    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

    unbind();
  }

  public void setIndices(int[] indices)
  {
    this.vcount = indices.length;

    bind();

    if (this.indices == -1)
    {
      this.indices = glGenBuffers();
    }

    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.indices);
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STREAM_DRAW);

    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

    unbind();
  }

  /**
   * Renders the VAO. Depending on whether an index buffer has been set, this will render using glDrawElements or glDrawArrays.
   */
  public void render()
  {
    bind();

    if (indices != -1)
    {
      glDrawElements(GL_TRIANGLES, this.vcount, GL_UNSIGNED_INT, 0);
    }
    else
    {
      glDrawArrays(GL_TRIANGLES, 0, this.vcount);
    }

    unbind();
  }

  /**
   * Creates a VertexBufferObject and binds it as an attribute at the given position with the given draw-mode (GL_STATIC_DRAW / GL_STREAM_DRAW).
   */
  public void addAttribute(int index, int length, float[] init, int mode)
  {
    if (index < MAX_ATTRIBUTES)
    {
      if (index == 0)
      {
        this.vcount = init.length;
      }

      bind();

      int vbo = glGenBuffers();
      glBindBuffer(GL_ARRAY_BUFFER, vbo);

      // Store initial data
      FloatBuffer buffer = BufferUtils.createFloatBuffer(init.length);
      buffer.put(init);
      buffer.flip();

      glEnableVertexAttribArray(index);
      glBufferData(GL_ARRAY_BUFFER, buffer, mode);
      glVertexAttribPointer(index, length, GL_FLOAT, false, 0, 0);

      attributes[index] = true;
      vbo_loaded.add(vbo);

      this.unbind();
    }
  }

  /**
   * Creates a VertexBufferObject and binds it as a GL_STATIC_DRAW attribute at the given position.
   */
  public void addAttribute(int index, int length, float[] init)
  {
    this.addAttribute(index, length, init, GL_STATIC_DRAW);
  }

  /**
   * Creates a vertex array.
   */
  public VertexArray()
  {
    this.vcount = 6;
    this.attributes = new boolean[MAX_ATTRIBUTES];
    this.id = glGenVertexArrays();
    this.indices = -1;
  }
}