package engine.platform.window.glfw;

import engine.platform.window.Window;
import engine.util.EngineINI;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.Callback;

import static org.lwjgl.glfw.GLFW.*;
import static engine.Engine.*;
import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL13C.GL_MULTISAMPLE;
import static org.lwjgl.system.MemoryUtil.NULL;

public final class GLFWWindow implements Window
{
  public static final int VSYNC_DISABLED = 0;
  public static final int VSYNC_ENABLED = 1;

  private long handle;

  private Vector2i size;
  private Window.State state;

  public long handle()
  {
    return this.handle;
  }

  @Override
  public void poll()
  {
    glfwPollEvents();
  }

  @Override
  public void swap()
  {
    glfwSwapBuffers(this.handle);
  }

  @Override
  public boolean exit()
  {
    return glfwWindowShouldClose(this.handle);
  }

  @Override
  public void exit(boolean exit)
  {
    glfwSetWindowShouldClose(this.handle, exit);
  }

  @Override @NotNull
  public Vector2i size()
  {
    return this.size;
  }

  @Override @NotNull
  public Window.State mode()
  {
    return this.state;
  }

  public void clear()
  {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
  }

  public GLFWWindow(Vector2i size, int vsync)
  {
    if (!glfwInit())
    {
      Log.error("[FATAL] Could not initialize GLFW.");
      System.exit(-1);
    }

    this.size = new Vector2i(size); // deep copy // why
    this.state = Window.State.WINDOWED;

    glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);
    glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
    glfwWindowHint(GLFW_SAMPLES, 8);
    this.handle = glfwCreateWindow(this.size.x, this.size.y, EngineINI.getInstance().title, NULL, NULL);

    glfwMakeContextCurrent(this.handle);
    glfwSwapInterval(vsync);

    GL.createCapabilities();
    glEnable(GL_MULTISAMPLE);
    glEnable(GL_TEXTURE_2D);
    glfwSetWindowSizeCallback(this.handle, (window, x, y) ->
    {
      this.size.set(x, y);
    });
  }
}
