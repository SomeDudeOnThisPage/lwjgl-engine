package engine.core.gfx;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL33C.*;

public class UniformBuffer
{
  public static final int OFFSET_MAT4X4F = 16 * Float.BYTES;
  public static final int OFFSET_VEC4F = 4 * Float.BYTES;
  public static final int N_BYTES = 4;

  public static final int OFFSET_PROJECTION_MATRIX = 0;

  public static final int OFFSET_VIEW_MATRIX = UniformBuffer.OFFSET_MAT4X4F;

  /** Offset to the view position uniform. */
  public static final int OFFSET_VIEW_POSITION = 2 * UniformBuffer.OFFSET_MAT4X4F;

  public static final int OFFSET_SCREEN_SIZE = 2 * UniformBuffer.OFFSET_MAT4X4F + OFFSET_VEC4F;

  private static int current = -1;

  private FloatBuffer buffer4f;
  private FloatBuffer buffer16f;

  private int id;
  private int size;
  private int binding;

  public void bind()
  {
    glBindBufferBase(GL_UNIFORM_BUFFER, this.binding, this.id);
    glBindBuffer(GL_UNIFORM_BUFFER, this.id);
  }

  public void unbind()
  {
    glBindBuffer(GL_UNIFORM_BUFFER, 0);
  }

  public void setUniform(int uniform, int position)
  {
    this.bind();

    if (position - 4 > this.size)
    {
      System.err.println("illegal uniform buffer operation");
      System.exit(1);
    }

    glBufferSubData(GL_UNIFORM_BUFFER, position, new int[] {uniform});
  }

  public void setUniform(Matrix4f uniform, int position)
  {
    this.bind();

    if (position - 16 * 4 > this.size)
    {
      System.err.println("illegal uniform buffer operation");
      System.exit(1);
    }

    glBufferSubData(GL_UNIFORM_BUFFER, position, uniform.get(new float[16]));
  }

  public void setUniform(Vector4f uniform, int position)
  {
    this.bind();

    if (position - 16 * 4 > this.size)
    {
      System.err.println("illegal uniform buffer operation");
      System.exit(1);
    }

    uniform.get(this.buffer4f);

    glBufferSubData(GL_UNIFORM_BUFFER, position, this.buffer4f);
  }

  public void setUniform(Vector3f uniform, int position)
  {
    this.bind();

    if (position - 16 * Float.BYTES > this.size)
    {
      System.err.println("illegal uniform buffer operation");
      System.exit(1);
    }

    uniform.get(this.buffer4f);
    this.buffer4f.put(3, 0.0f); // pad buffer

    glBufferSubData(GL_UNIFORM_BUFFER, position, this.buffer4f);
  }

  public UniformBuffer(int size, int binding)
  {
    this.id = glGenBuffers();
    this.size = size;
    this.binding = binding;
    this.buffer16f = BufferUtils.createFloatBuffer(16);
    this.buffer4f = BufferUtils.createFloatBuffer(4);

    this.bind();
    glBufferData(GL_UNIFORM_BUFFER, this.size, GL_STREAM_DRAW);
    this.unbind();

    glBindBufferBase(GL_UNIFORM_BUFFER, binding, this.id);
  }
}