package engine.gfx.opengl;

import engine.gfx.ShaderProgram;

import java.util.HashMap;
import java.util.HashSet;

import static org.lwjgl.opengl.GL42C.*;

public class GLState
{
  /**
   * Contains all currently enabled vertex attribute array indices, using a {@link HashSet} for {@code O(1)} lookup times.
   */
  private static final HashSet<Integer> attributes = new HashSet<>();

  /**
   * Contains all currently bound data buffer addresses for each type of buffer used in the engine.
   */
  private static final HashMap<Integer, Integer> buffers = new HashMap<>();
  static
  {
    GLState.buffers.put(GL_ARRAY_BUFFER, GL_NONE);
    GLState.buffers.put(GL_ELEMENT_ARRAY_BUFFER, GL_NONE);
    GLState.buffers.put(GL_DRAW_INDIRECT_BUFFER, GL_NONE);
    GLState.buffers.put(GL_UNIFORM_BUFFER, GL_NONE);
    GLState.buffers.put(GL_FRAMEBUFFER, GL_NONE);
  }

  private static ShaderProgram program;

  public static void program(ShaderProgram program)
  {
    if (program == null)
    {
      if (GLState.program != null)
      {
        glUseProgram(GL_NONE);
        GLState.program = null;
      }
      return;
    }

    if (GLState.program == null || GLState.program != program)
    {
      glUseProgram(program.id());
      GLState.program = program;
    }
  }

  /**
   * Enables or disables a vertex attribute at a given position, if it was not bound / unbound prior to the call.
   * <p>
   *   Utilizing this method over {@code glEnableVertexAttribArray} may help reduce the number of API calls.
   * </p>
   * @param index The {@code index} of the vertex attribute array.
   * @param state The desired state of the vertex attribute array.
   */
  public static void attribute(int index, boolean state)
  {
    if (!GLState.attributes.contains(index) && state)
    {
      glEnableVertexAttribArray(index);
      GLState.attributes.add(index);
    }

    if (GLState.attributes.contains(index) && !state)
    {
      glDisableVertexAttribArray(index);
      GLState.attributes.remove(index);
    }
  }

  public static int buffer(int target)
  {
    return GLState.buffers.get(target);
  }

  public static boolean framebuffer(int target, int index)
  {
    if (GLState.buffers.get(target) != index)
    {
      glBindFramebuffer(target, index);
      GLState.buffers.put(target, index);
      return true;
    }
    return false;
  }

  public static boolean buffer(int target, int index)
  {
    if (GLState.buffers.get(target) != index)
    {
      glBindBuffer(target, index);
      GLState.buffers.put(target, index);
      return true;
    }
    return false;
  }
}
