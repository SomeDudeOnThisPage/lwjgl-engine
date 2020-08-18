package engine.gfx.opengl.shader;

import engine.exception.GLException;
import engine.gfx.BaseAssetBindable;
import engine.gfx.Shader;
import engine.util.Memory;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;

import static org.lwjgl.opengl.GL42C.*;

public class GLShader extends BaseAssetBindable implements Shader
{
  public static final String dir = "platform\\resources\\shaders\\";

  private final ArrayList<String> included;
  private boolean timed = false;

  /**
   * The {@code #include} preprocessor directive is handled separately, as it requires tracking the already
   * included programs, as well as the current inclusion-recursion depth of the {@link GLShader} object to prevent
   * including the same {@link GLShader} twice, or causing an infinite {@link GLShader}-loading loop.
   * @return The {@link GLShader} source with the included source attached in place of the
   *         {@code #include} preprocessing directive.
   */
  private String include(String file)
  {
    String path = file.replaceAll("\\.(?=.*\\.)", "\\" + File.separator);

    ByteBuffer resource = Memory.load(GLShader.dir + path);
    if (resource == null)
    {
      throw new RuntimeException("cannot load shader from file '" + GLShader.dir + path + "' - no file found at the specified location");
    }

    byte[] buffer = new byte[resource.capacity()];
    resource.get(buffer);

    String source = new String(buffer, StandardCharsets.UTF_8);
    StringBuilder output = new StringBuilder();

    Scanner scanner = new Scanner(source);

    // iterate over lines to find preprocessor directives
    String line;
    while (scanner.hasNextLine())
    {
      line = scanner.nextLine();

      // handle include directive separately
      if (line.contains("#include"))
      {
        String inc = line.substring(10, line.length() - 1);
        if (inc.equals("shared.time.glsl"))
        {
          this.timed = true;
        }
        if (inc.equals(file))
        {
          throw new UnsupportedOperationException("attempted to include self in shader file '" + file + "'");
        }

        if (!this.included.contains(inc))
        {
          this.included.add(inc);
          output.append(this.include(inc));
        }
      }
      else
      {
        output.append(line).append("\n");
      }
    }

    return output.toString();
  }

  public boolean timed()
  {
    return this.timed;
  }

  @Override
  public void dispose()
  {
    if (glIsShader(this.id))
    {
      glDeleteShader(this.id);
    }
  }

  public GLShader(int type, String file)
  {
    if (type != GL_VERTEX_SHADER && type != GL_GEOMETRY_SHADER && type != GL_FRAGMENT_SHADER)
    {
      throw new IllegalArgumentException("shader must be of type 'GL_VERTEX_SHADER', 'GL_GEOMETRY_SHADER', or 'GL_FRAGMENT_SHADER'");
    }

    this.included = new ArrayList<>();

    this.id = glCreateShader(type);
    glShaderSource(this.id, this.include(file) /* Append EOF */ + "\0");
    glCompileShader(this.id);

    // check if the program compiled correctly
    if (glGetShaderi(this.id, GL_COMPILE_STATUS) == GL_FALSE)
    {
      throw new GLException(this, "glCompileShader", "could not compile shader '" + file + "':\n\t" + glGetShaderInfoLog(this.id));
    }
  }

  @Override
  public void bind()
  {
    throw new UnsupportedOperationException("cannot bind non-program shader - use ShaderProgram#bind() instead");
  }

  @Override
  public void unbind()
  {
    throw new UnsupportedOperationException("cannot unbind non-program shader - use ShaderProgram#unbind() instead");
  }
}
