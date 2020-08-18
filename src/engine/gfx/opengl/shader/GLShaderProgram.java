package engine.gfx.opengl.shader;

import engine.Console;
import engine.Engine;
import engine.asset.Reloadable;
import engine.exception.GLException;
import engine.gfx.BaseAssetBindable;
import engine.gfx.ShaderProgram;
import engine.gfx.opengl.GLState;
import engine.util.Memory;
import org.jetbrains.annotations.NotNull;
import org.joml.*;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

import static org.lwjgl.opengl.GL42C.*;

public class GLShaderProgram extends BaseAssetBindable implements ShaderProgram, Reloadable<ShaderProgram>
{
  /**
   * This {@link HashMap} caches all previously queried uniform locations to reduce the number of API calls required
   * each frame.
   */
  protected HashMap<String, Integer> uniforms;

  private ArrayList<GLShader> shaders;

  /**
   * Defines whether a shader in this program includes /shared/time.glsl.
   */
  private boolean timed;

  private int uniform(String key)
  {
    if (!this.uniforms.containsKey(key))
    {
      int location = glGetUniformLocation(this.id, key);
      if (location == -1)
      {
        return -1;
        //throw new GLException(this, "glGetUniformLocation", "could not find uniform location for uniform '" + key + "'");
      }

      this.uniforms.put(key, location);
    }

    return this.uniforms.get(key);
  }

  @Override
  public void reload(ShaderProgram program)
  {
    // invalidate old ShaderProgram
    this.dispose();

    // copy stuff of new ShaderProgram into this
    GLShaderProgram shader = (GLShaderProgram) program;

    this.id = shader.id;
    this.uniforms = shader.uniforms;
    this.timed = shader.timed;
    this.shaders = shader.shaders;

    Console.print("reloaded shader program - old ID: '" + this.id + "' - new ID: '" + program.id() + "'");
  }

  @Override
  public boolean hasUniform(String... uniform)
  {
    for (String u : uniform)
    {
      if (!this.uniforms.containsKey(u))
      {
        return false;
      }
    }
    return true;
  }

  /**
   * Binds a boolean uniform to this {@link GLShaderProgram}.
   * @param uniform {@link String} uniform location.
   * @param data Uniform data.
   */
  @SuppressWarnings("unused")
  @Override
  public final void setUniform(@NotNull String uniform, boolean data)
  {
    this.bind();
    glUniform1i(this.uniform(uniform), data ? 1 : 0); // 0 is converted to FALSE, != 0 is converted to TRUE
  }

  /**
   * Binds an int uniform to this {@link GLShaderProgram}.
   * @param uniform {@link String} uniform location.
   * @param data Uniform data.
   */
  @SuppressWarnings("unused")
  @Override
  public final void setUniform(@NotNull String uniform, int data)
  {
    this.bind();
    glUniform1i(this.uniform(uniform), data);
  }

  /**
   * Binds a two-component int {@link Vector2i} to this {@link GLShaderProgram}.
   * @param uniform {@link String} uniform location.
   * @param data Two-component int {@link Vector2i} uniform data.
   */
  @SuppressWarnings("unused")
  @Override
  public final void setUniform(@NotNull String uniform, @NotNull Vector2ic data)
  {
    this.bind();
    glUniform2iv(this.uniform(uniform), data.get(Memory.Buffer.buffer2i));
  }

  /**
   * Binds a three-component int {@link Vector3i} to this {@link GLShaderProgram}.
   * @param uniform {@link String} uniform location.
   * @param data Three-component int {@link Vector3i} uniform data.
   */
  @SuppressWarnings("unused")
  @Override
  public final void setUniform(@NotNull String uniform, @NotNull Vector3ic data)
  {
    this.bind();
    glUniform3iv(this.uniform(uniform), data.get(Memory.Buffer.buffer3i));
  }

  /**
   * Binds a four-component int {@link Vector4i} to this {@link GLShaderProgram}.
   * @param uniform {@link String} uniform location.
   * @param data Four-component int {@link Vector4i} uniform data.
   */
  @SuppressWarnings("unused")
  @Override
  public final void setUniform(@NotNull String uniform, @NotNull Vector4ic data)
  {
    this.bind();
    glUniform4iv(this.uniform(uniform), data.get(Memory.Buffer.buffer4i));
  }

  /**
   * Binds a float uniform to this {@link GLShaderProgram}.
   * @param uniform {@link String} uniform location.
   * @param data Uniform data.
   */
  @SuppressWarnings("unused")
  @Override
  public final void setUniform(@NotNull String uniform, float data)
  {
    this.bind();
    glUniform1f(this.uniform(uniform), data);
  }

  /**
   * Binds a two-component float {@link Vector2f} to this {@link GLShaderProgram}.
   * @param uniform {@link String} uniform location.
   * @param data Two-component float {@link Vector2f} uniform data.
   */
  @SuppressWarnings("unused")
  @Override
  public final void setUniform(@NotNull String uniform, @NotNull Vector2fc data)
  {
    this.bind();
    glUniform2fv(this.uniform(uniform), data.get(Memory.Buffer.buffer2f));
  }

  /**
   * Binds a three-component float {@link Vector3f} to this {@link GLShaderProgram}.
   * @param uniform {@link String} uniform location.
   * @param data Three-component float {@link Vector3f} uniform data.
   */
  @SuppressWarnings("unused")
  @Override
  public final void setUniform(@NotNull String uniform, @NotNull Vector3fc data)
  {
    this.bind();
    glUniform3fv(this.uniform(uniform), data.get(Memory.Buffer.buffer3f));
  }

  /**
   * Binds a four-component float {@link Vector4f} to this {@link GLShaderProgram}.
   * @param uniform {@link String} uniform location.
   * @param data Four-component float {@link Vector4f} uniform data.
   */
  @SuppressWarnings("unused")
  @Override
  public final void setUniform(@NotNull String uniform, @NotNull Vector4fc data)
  {
    this.bind();
    glUniform4fv(this.uniform(uniform), data.get(Memory.Buffer.buffer4f));
  }

  /**
   * Binds a 2x2 float {@link Matrix2f} to this {@link GLShaderProgram}.
   * @param uniform {@link String} uniform location.
   * @param data 2x2 {@link Matrix2f} uniform data.
   */
  @SuppressWarnings("unused")
  @Override
  public final void setUniform(@NotNull String uniform, @NotNull Matrix2fc data)
  {
    this.bind();
    glUniformMatrix2fv(this.uniform(uniform), false, data.get(Memory.Buffer.buffer4f));
  }

  /**
   * Binds a 3x3 float {@link Matrix3f} to this {@link GLShaderProgram}.
   * @param uniform {@link String} uniform location.
   * @param data 3x3 {@link Matrix3f} uniform data.
   */
  @SuppressWarnings("unused")
  @Override
  public final void setUniform(@NotNull String uniform, @NotNull Matrix3fc data)
  {
    this.bind();
    glUniformMatrix3fv(this.uniform(uniform), false, data.get(Memory.Buffer.buffer9f));
  }

  /**
   * Binds a 4x4 float {@link Matrix4f} to this {@link GLShaderProgram}.
   * @param uniform {@link String} uniform location.
   * @param data 4x4 {@link Matrix4f} uniform data.
   */
  @SuppressWarnings("unused")
  @Override
  public final void setUniform(@NotNull String uniform, @NotNull Matrix4fc data)
  {
    this.bind();
    glUniformMatrix4fv(this.uniform(uniform), false, data.get(Memory.Buffer.buffer16f));
  }

  @Override
  public final void bind()
  {
    GLState.program(this);

    if (this.timed)
    {
      glUniform1f(this.uniform("u_time"), (float) Engine.time());
      //this.setUniform("u_time", (float) Engine.time());
    }
  }

  @Override
  public final void unbind()
  {
    GLState.program(null);
  }

  @Override
  public void dispose()
  {
    //if (glIsProgram(this.id))
    //{
      this.unbind();
      glDeleteProgram(this.id);
      Engine.Log.info("disposed of ShaderProgram '" + this + "'");
    //}
  }

  private void attach(File file)
  {
    String extension = "";

    int i = file.getName().lastIndexOf('.');
    if (i > 0)
    {
      extension = file.getName().substring(i+1);
    }

    int type = switch (extension)
      {
        case "vs" -> GL_VERTEX_SHADER;
        case "fs" -> GL_FRAGMENT_SHADER;
        case "gs" -> GL_GEOMETRY_SHADER;
        default -> throw new UnsupportedOperationException("shader type extension '" + extension + "' is not defined as a loadable shader");
      };

    GLShader shader = new GLShader(type, file.getName());
    shaders.add(shader);
    glAttachShader(this.id, shader.id());

    if (shader.timed() && !this.timed)
    {
      this.timed = true;
    }
  }

  public GLShaderProgram(String vs, String fs)
  {
    this.id = glCreateProgram();
    this.uniforms = new HashMap<>();
    this.timed = false;

    //File folder = new File(GLShader.dir);
    //File[] list = folder.listFiles();

    this.shaders = new ArrayList<>();

    File vsf = new File(GLShader.dir + vs);
    File fsf = new File(GLShader.dir + fs);

    this.attach(vsf);
    this.attach(fsf);

    /*for (File file : list)
    {
      if (file.isFile())
      {
        // remove extension (if present), and compare to our name
        if (file.getName().replaceFirst("[.][^.]+$", "").equals(name))
        {
          this.attach(file);
        }
      }
    }*/

    glLinkProgram(this.id);
    if (glGetProgrami(this.id, GL_LINK_STATUS) != GL_TRUE)
    {
      throw new GLException(this, "glLinkProgram", "failed to link shader program '" + this + "'");
    }

    for (GLShader shader : this.shaders)
    {
      glDetachShader(this.id, shader.id());
      shader.dispose(); // for now just dispose the shaders, later with filters they might need to be managed too
    }

    // System.out.println("----------------------------------------");
    // System.out.println(name);
    // System.out.println("----------------------------------------");

    // query uniform locations and cache them
    /*int[] ubuffer = new int[1];
    glGetProgramiv(this.id, GL_ACTIVE_UNIFORMS, ubuffer);

    int[] length  = new int[1];
    int[] size    = new int[1];
    int[] type    = new int[1];
    for (int i = 0; i < ubuffer[0]; i++)
    {
      ByteBuffer uname = ByteBuffer.allocateDirect(256);
      glGetActiveUniform(this.id, i, length, size, type, uname);
      byte[] str = new byte[uname.limit()];
      uname.get(str);

      String uniformName = new String(str, StandardCharsets.UTF_8).trim();
      // System.out.println(uniformName);
      this.uniform(uniformName);
    }*/
  }

  public GLShaderProgram(String name)
  {
    this(name + ".vs", name + ".fs");
  }
}
