package engine.core.gfx;

import engine.Engine;
import engine.core.gfx.material.MaterialArchetype;
import engine.util.settings.Settings;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import static org.lwjgl.opengl.GL33C.*;

public class Shader
{
  private static int recursion = 0;
  private static ArrayList<String> included = new ArrayList<>();
  protected static final String regex = "\\.(?=.*\\.)";

  /**
   * List of all currently loaded {@link Shader} instances to avoid loading a {@link Shader} multiple times.
   */
  private static HashMap<String, Shader> loaded = new HashMap<>();

  protected static int current = 0;

  protected float time;

  /**
   * ID of the program managed by this instance as given by OpenGL.
   */
  protected int program;

  private MaterialArchetype material;

  /**
   * 16-Slot {@link FloatBuffer} used for setting 4x4 {@link Matrix4f} uniforms and any other float uniforms with
   * lower-than-16 elements.
   * <br>
   * The contents of this buffer are assumed to be undefined for any other values than
   * the size of the last set uniform.
   */
  private static FloatBuffer buffer16f = BufferUtils.createFloatBuffer(16);
  private static FloatBuffer buffer4f = BufferUtils.createFloatBuffer(4);
  private static FloatBuffer buffer3f = BufferUtils.createFloatBuffer(3);
  private static FloatBuffer buffer2f = BufferUtils.createFloatBuffer(2);

  public static Shader getInstance(String vertex, String fragment)
  {
    return Shader.getInstance(vertex, null, fragment);
  }

  public static Shader getInstance(String name)
  {
    return Shader.getInstance(name, name, name);
  }

  public static Shader getInstance(String vertex, String geometry, String fragment)
  {
    vertex = vertex.replaceAll(Shader.regex, "/");

    if (geometry != null)
    {
      geometry = geometry.replaceAll(Shader.regex, "/");
    }

    fragment = fragment.replaceAll(Shader.regex, "/");

    if (!Shader.loaded.containsKey(fragment))
    {
      return new Shader(vertex, geometry, fragment);
    }

    return Shader.loaded.get(fragment);
  }

  private static synchronized String include(String file) throws Exception
  {
    file = file.replaceAll(Shader.regex, "/");

    String program = new String(Files.readAllBytes(Paths.get("resources/shaders/" + file)));
    StringBuilder output = new StringBuilder();

    // scan for includes
    Scanner scanner = new Scanner(program);
    int i = 1;
    while (scanner.hasNextLine())
    {
      String line = scanner.nextLine();

      if (line.startsWith("#include"))
      {
        String inc = line.substring(10, line.length() - 1);

        if (!new File("resources/shaders/" + inc).exists())
        {
          throw new IOException("0(" + i + ") : could not find included shader file \'" + inc + "\'");
        }

        // do not include same include twice
        if (!Shader.included.contains(inc))
        {
          Shader.recursion+=1;
          Shader.included.add(inc);
          output.append(Shader.include(inc));
        }
      }
      else if (line.startsWith("#import"))
      {
        // todo: import static engine variable system
        String imp = line.substring(9, line.length() - 1);
      }
      else
      {
        if (Shader.recursion > 0 && line.contains("void main()") || line.contains("void main(void)"))
        {
          Shader.recursion = 0;
          throw new Exception("(" + i + ") : declaration of main() in included shader file '" + file + "'");
        }

        if (line.contains("#version"))
        {
          Shader.recursion = 0;
          throw new Exception("(" + i + ") : declaration of '#version' in shader file '" + file + "'\nGLSL version is" +
            "injected by the engine automatically, remove any '#version' statements in the shader code.");
        }

        if (line.contains("ENGINE_IMPORT_VALUE_INTEGER"))
        {
          String variable = line.substring(line.indexOf("<") + 1, line.indexOf(">"));
          line = line.replace("ENGINE_IMPORT_VALUE_INTEGER <" + variable + ">", String.valueOf(Settings.geti(variable)));
        }

        output.append(line).append("\n");
        i++;
      }
    }

    if (Shader.recursion == 0)
    {
      output.append("\0");
      // construct and prepend version string
      output.insert(0, "#version " + Settings.gets("GLSLVersion") + (Settings.getb("GLSLCoreProfile") ? " core" : "") + "\n");
      Shader.included.clear();
    }
    else
    {
      Shader.recursion--;
    }

    scanner.close();
    return output.toString();
  }

  protected static int load(int type, String name)
  {
    int shader = glCreateShader(type);

    try
    {
      glShaderSource(shader, Shader.include(name));
      glCompileShader(shader);

      if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE)
      {
        throw new Exception(glGetShaderInfoLog(shader));
      }

      return shader;
    }
    catch(Exception e)
    {
      System.err.println("[ERROR] failed to load shader program '" + name + "':\n" + e.getMessage());
      return -1;
    }
  }

  public MaterialArchetype getCurrentMaterial()
  {
    return this.material;
  }

  public void setCurrentMaterial(MaterialArchetype material)
  {
    this.material = material;
  }

  public static void terminate()
  {
    for (Shader shader : Shader.loaded.values())
    {
      glDeleteProgram(shader.program);
    }
  }

  public void setUniform(String name, Matrix4f data)
  {
    bind();

    data.get(Shader.buffer16f);

    int location = glGetUniformLocation(this.program, name);
    glUniformMatrix4fv(location, false, Shader.buffer16f);
  }

  public void setUniform(String name, Vector3f data)
  {
    bind();

    data.get(Shader.buffer3f);

    int location = glGetUniformLocation(this.program, name);
    glUniform3fv(location, Shader.buffer3f);
  }

  public void setUniform(String name, Vector2f data)
  {
    bind();

    data.get(Shader.buffer2f);

    int location = glGetUniformLocation(this.program, name);
    glUniform2fv(location, Shader.buffer2f);
  }

  public void setUniform(String name, int data)
  {
    bind();

    int location = glGetUniformLocation(this.program, name);
    glUniform1i(location, data);
  }

  public void setUniform(String name, float data)
  {
    bind();

    int location = glGetUniformLocation(this.program, name);
    glUniform1f(location, data);
  }

  public void setUniform(String name, Vector4f data)
  {
    bind();

    data.get(Shader.buffer4f);

    int location = glGetUniformLocation(this.program, name);
    glUniform4fv(location, Shader.buffer4f);
  }

  public void bind()
  {
    if (Shader.current != this.program)
    {
      this.time += Engine.scene_manager.getScene().getDeltaTime();

      glUseProgram(this.program);

      int location = glGetUniformLocation(this.program, "u_time");
      glUniform1f(location, this.time);

      Shader.current = program;
    }
  }

  public void unbind()
  {
    Shader.current = 0;
    glUseProgram(0);
  }

  protected Shader(String vertex, String geometry, String fragment)
  {
    this.program = glCreateProgram();

    if (new File("resources/shaders/" + vertex + ".vs").exists())
    {
      System.out.println("-------------------------------------------");
      System.out.println(vertex + ".vs");
      System.out.println("-------------------------------------------");
      int vshader = Shader.load(GL_VERTEX_SHADER, vertex + ".vs");
      glAttachShader(this.program, vshader);
      glDeleteShader(vshader);
    }
    else
    {
      System.err.println("error loading shader " + vertex + " (no vertex shader ('.vs') file found)");
    }

    if (geometry != null && new File("resources/shaders/" + geometry + ".gs").exists())
    {
      int gshader = Shader.load(GL_GEOMETRY_SHADER, geometry + ".gs");
      glAttachShader(this.program, gshader);
      glDeleteShader(gshader);
    }

    if (new File("resources/shaders/" + fragment + ".fs").exists())
    {

      System.out.println("-------------------------------------------");
      System.out.println(fragment + ".fs");
      System.out.println("-------------------------------------------");
      int fshader = Shader.load(GL_FRAGMENT_SHADER, fragment + ".fs");
      glAttachShader(this.program, fshader);
      glDeleteShader(fshader);
    }
    else
    {
      System.err.println("error loading shader " + fragment + " (no fragment shader ('.fs') file found)");
    }

    glLinkProgram(this.program);
    Shader.loaded.put(fragment, this);

    this.time = 0;
  }

  protected Shader(String vertex, String fragment)
  {
    this(vertex, null, fragment);
  }

  protected Shader(String name)
  {
    this(name, name, name);
  }

  protected Shader() {}
}