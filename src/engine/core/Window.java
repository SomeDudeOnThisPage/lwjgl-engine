package engine.core;

import engine.Engine;
import engine.EngineSettings;
import engine.core.gui.GUI;
import engine.util.settings.Settings;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GLUtil;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL30C.*;
import static org.lwjgl.opengl.GL31C.GL_MAX_UNIFORM_BLOCK_SIZE;
import static org.lwjgl.opengl.GL31C.GL_MAX_UNIFORM_BUFFER_BINDINGS;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window
{
  public enum MODE
  {
    WINDOWED,
    BORDERLESS,
    FULLSCREEN,
    NONE
  }

  public enum CURSOR
  {
    NORMAL,
    HIDDEN,
    DISABLED
  }

  private long window;
  private int width;
  private int height;

  private int width_windowed;
  private int height_windowed;

  private float csx = 1;
  private float csy = 1;

  private float fbx = 1;
  private float fby = 1;

  private MODE mode;
  private CURSOR cursor;

  public long getWindow()
  {
    return window;
  }

  public Vector2f getContentScale()
  {
    return new Vector2f(csx, csy);
  }

  public Vector2f getFrameBufferSize()
  {
    return new Vector2f(fbx, fby);
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
    glViewport(0, 0, this.width, this.height);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
  }

  public void setCursorMode(CURSOR cursor)
  {
    switch (cursor)
    {
      case NORMAL:
        this.cursor = CURSOR.NORMAL;
        glfwSetInputMode(this.window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        break;
      case HIDDEN:
        this.cursor = CURSOR.HIDDEN;
        glfwSetInputMode(this.window, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
        break;
      case DISABLED:
        this.cursor = CURSOR.DISABLED;
        glfwSetInputMode(this.window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        break;
    }
  }

  public CURSOR getCursorMode()
  {
    return this.cursor;
  }

  public void windowed()
  {
    if (this.mode != MODE.WINDOWED)
    {
      GLFWVidMode mode = glfwGetVideoMode(glfwGetPrimaryMonitor());
      if (mode != null)
      {
        this.mode = MODE.WINDOWED;

        glfwWindowHint(GLFW_RED_BITS, mode.redBits());
        glfwWindowHint(GLFW_GREEN_BITS, mode.greenBits());
        glfwWindowHint(GLFW_BLUE_BITS, mode.blueBits());
        glfwWindowHint(GLFW_REFRESH_RATE, mode.refreshRate());

        int x = (int) (mode.width() / 2.0f - this.width_windowed / 2.0f) + 100;
        int y = (int) (mode.height() / 2.0f - this.height_windowed / 2.0f) + 100;

        glfwSetWindowMonitor(window, NULL, x, y, width_windowed, height_windowed, mode.refreshRate());
      }
    }
  }

  public void borderless()
  {
    if (this.mode != MODE.BORDERLESS)
    {
      GLFWVidMode mode = glfwGetVideoMode(glfwGetPrimaryMonitor());
      if (mode != null)
      {
        glfwSetWindowMonitor(window, glfwGetPrimaryMonitor(), 0, 0, mode.width(), mode.height(), mode.refreshRate());
        this.mode = MODE.BORDERLESS;
      }
    }
  }

  public Window()
  {
    if (!glfwInit())
    {
      System.err.println("[FATAL] Could not initialize GLFW.");
      System.exit(1);
    }

    this.width = Settings.geti("DisplayWidth");
    this.height = Settings.geti("DisplayHeight");
    this.width_windowed = this.width;
    this.height_windowed = this.height;

    glfwWindowHint(GLFW_RESIZABLE, Settings.getb("DisplayResize") ? GLFW_TRUE : GLFW_FALSE);
    glfwWindowHint(GLFW_STENCIL_BITS, Settings.geti("StencilBits"));

    this.window = glfwCreateWindow(this.width, this.height, "OpenGL Testing", NULL, NULL);

    this.mode = MODE.NONE;
    this.cursor = CURSOR.NORMAL;

    switch (Settings.geti("DisplayMode"))
    {
      case 0:
        this.windowed();
        break;
      case 1:
        this.borderless();
        break;
      case 2:
        System.err.println("fullscreen is currently unsupported, switching to windowed");
        this.windowed();
        break;
      default:
        System.err.println("no window mode set, switching to windowed");
        this.windowed();
        break;
    }

    glfwSetWindowSizeCallback(this.window, (window, w, h) ->
    {
      this.width = w;
      this.height = h;

      glfwSetWindowSize(window, w, h);
      glViewport(0, 0, w, h);

      if (this.mode == MODE.WINDOWED)
      {
        this.width_windowed = w;
        this.height_windowed = h;
      }

      if (Engine.canRender)
      {
        GUI.onScreenSizeChanged(w, h);
        Engine.scene_manager.getScene().getPlayer().getCamera().onScreenSizeChanged(w, h);
        Engine.scene_manager.getScene().getRenderer().onScreenSizeChanged(w, h);
        Engine.scene_manager.render();
        GUI.render();
      }

      glfwSwapBuffers(window);
    });

    glfwSetFramebufferSizeCallback(window, (handle, w, h) ->
    {
      this.fbx = w;
      this.fby = h;
    });

    glfwSetWindowContentScaleCallback(window, (handle, xscale, yscale) ->
    {
      this.csx = xscale;
      this.csy = yscale;
    });

    glfwMakeContextCurrent(window);
    glfwSwapInterval(Settings.geti("VerticalSync"));
    createCapabilities();

    int[] fx = new int[] {0};
    int[] fy = new int[] {0};

    glfwGetFramebufferSize(window, fx, fy);
    this.fbx = fx[0];
    this.fby = fy[0];

    System.out.println("Graphics Card: " + glGetString(GL_VENDOR) + " " + glGetString(GL_RENDERER));
    System.out.println("max. multisample levels: " + glGetInteger(GL_MAX_SAMPLES));
    System.out.println("max. uniform buffers: " + glGetInteger(GL_MAX_UNIFORM_BUFFER_BINDINGS));
    System.out.println("max. uniform buffer size: " + glGetInteger(GL_MAX_UNIFORM_BLOCK_SIZE));
    System.out.println("max. vertex uniform components: " + glGetInteger(GL_MAX_VERTEX_UNIFORM_COMPONENTS));
    System.out.println("max. fragment uniform components: " + glGetInteger(GL_MAX_FRAGMENT_UNIFORM_COMPONENTS));

    System.out.println("max. array texture layers: " + glGetInteger(GL_MAX_ARRAY_TEXTURE_LAYERS));

    if (Settings.getb("Multisample"))
    {
      glEnable(GL_MULTISAMPLE);
      if (Settings.getb("MultisampleUseMaxSamples"))
      {
        glfwWindowHint(GLFW_SAMPLES, glGetInteger(GL_MAX_SAMPLES));
      }
      else
      {
        glfwWindowHint(GLFW_SAMPLES, Settings.geti("MultisampleSamples"));
      }
    }

    glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

    glEnable(GL_CULL_FACE);
    glCullFace(GL_BACK);
    glEnable(GL_STENCIL_TEST);
  }
}