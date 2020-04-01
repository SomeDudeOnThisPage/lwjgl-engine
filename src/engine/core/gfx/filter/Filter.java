package engine.core.gfx.filter;

import engine.core.gfx.Shader;
import engine.core.gfx.VertexArray;

import java.io.File;
import java.util.HashMap;

import static org.lwjgl.opengl.GL11C.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11C.glDrawArrays;
import static org.lwjgl.opengl.GL20C.*;

public class Filter extends Shader
{
  public static final int FILTER_COLOR_BUFFER_BINDING = 0;
  public static final int FILTER_DEPTH_BUFFER_BINDING = 1;

  private static HashMap<String, Filter> loaded = new HashMap<>();

  protected static void pass()
  {
    VertexArray.empty.bind();
    VertexArray.postRenderPass();
    glDrawArrays(GL_TRIANGLES, 0, 6);
    VertexArray.preRenderPass();
  }

  public static void terminate()
  {
    for (Filter filter : Filter.loaded.values())
    {
      glDeleteShader(filter.program);
    }
  }

  @Override
  public /* disable further overriding of the bind method */ final void bind()
  {
    super.bind();
  }

  /**
   * Called when the {@link Filter} should be applied.
   * This method should be overridden in subclasses to e.g. bind custom uniform variables.
   */
  public void apply()
  {
    this.bind();
    Filter.pass();
    this.unbind();
  }

  /**
   * Use {@link Filter#getInstance(String)} and cast it to your desired {@link Filter} subclass.
   * @param name The filename of the {@link Filter}.
   */
  public Filter(String name)
  {
    this.program = glCreateProgram();

    if (new File("resources/shaders/filters/filter.vs").exists())
    {
      System.out.println("-------------------------------------------");
      System.out.println("resources/shaders/filters/filter.vs");
      System.out.println("-------------------------------------------");
      int vshader = Shader.load(GL_VERTEX_SHADER, "filters/filter.vs");
      glAttachShader(this.program, vshader);
      glDeleteShader(vshader);
    }
    else
    {
      System.err.println("error loading shader " + name + " (no vertex shader ('.vs') file found)");
    }

    if (new File("resources/shaders/filters/" + name + ".filter").exists())
    {
      System.out.println("-------------------------------------------");
      System.out.println(name + ".filter");
      System.out.println("-------------------------------------------");
      int fshader = Shader.load(GL_FRAGMENT_SHADER, "filters/" + name + ".filter");
      glAttachShader(this.program, fshader);
      glDeleteShader(fshader);
    }
    else
    {
      System.err.println("error loading shader " + name + " (no filter shader ('.filter') file found)");
    }

    glLinkProgram(this.program);
    Filter.loaded.put(name, this);

    this.time = 0;
  }
}
