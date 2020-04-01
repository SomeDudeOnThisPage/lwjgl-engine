package engine.core;

import engine.Engine;
import org.lwjgl.opengl.GLUtil;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL30C.*;
import static org.lwjgl.opengl.GL31C.GL_MAX_UNIFORM_BLOCK_SIZE;
import static org.lwjgl.opengl.GL31C.GL_MAX_UNIFORM_BUFFER_BINDINGS;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window
{
  private long window;
  private int width = 1600 / 3;
  private int height = 900 / 3;

  public long getWindow()
  {
    return window;
  }

  public int getWidth()
  {
    return width;
  }

  public int getHeight()
  {
    return height;
  }

  public boolean shouldClose()
  {
    return glfwWindowShouldClose(window);
  }

  public void update()
  {
    glfwSwapBuffers(window);
  }

  public void clear()
  {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
  }

  public Window()
  {
    if (!glfwInit())
    {
      System.err.println("[FATAL] Could not initialize GLFW.");
      System.exit(1);
    }

    glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
    glfwWindowHint(GLFW_STENCIL_BITS, 8);

    this.window = glfwCreateWindow(width, height, "OpenGL Testing", NULL, NULL);

    glfwSetWindowSizeCallback(window, (window, w, h) ->
    {
      width = w;
      height = h;

      glfwSetWindowSize(window, w, h);
      glViewport(0, 0, w, h);

      if (Engine.canRender)
      {
        Engine.scene_manager.render();
        Engine.scene_manager.getScene().getPlayer().getCamera().onScreenSizeChanged(w, h);
        Engine.scene_manager.getScene().getRenderer().onScreenSizeChanged(w, h);
      }
      glfwSwapBuffers(window);
    });

    glfwMakeContextCurrent(window);
    glfwSwapInterval(1);
    createCapabilities();
    // GLUtil.setupDebugMessageCallback();

    System.out.println("Graphics Card: " + glGetString(GL_VENDOR) + " " + glGetString(GL_RENDERER));
    System.out.println("max. multisample levels: " + glGetInteger(GL_MAX_SAMPLES));
    System.out.println("max. uniform buffers: " + glGetInteger(GL_MAX_UNIFORM_BUFFER_BINDINGS));
    System.out.println("max. uniform buffer size: " + glGetInteger(GL_MAX_UNIFORM_BLOCK_SIZE));

    glEnable(GL_MULTISAMPLE);
    glfwWindowHint(GLFW_SAMPLES, 0);

    glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

    glEnable(GL_CULL_FACE);
    glCullFace(GL_BACK);
  }
}