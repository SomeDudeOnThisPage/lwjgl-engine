package engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.utils.GdxNativesLoader;
import engine.core.*;
import engine.core.gfx.batching.AssetManager;
import engine.core.gfx.filter.Filter;
import engine.core.gfx.FrameBuffer;
import engine.core.gfx.Shader;
import engine.core.gfx.batching.DeferredMeshBatcher;
import engine.core.gui.GUI;
import engine.core.gui.Label;
import engine.core.scene.Scene;
import engine.core.scene.SceneManager;
import engine.core.Window;
import engine.util.Assimp;
import engine.util.ExceptionDialog;
import engine.util.settings.EngineInitialization;
import engine.util.settings.Settings;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class Engine
{
  /** Total thread counter. */
  public static int THREADS = 1;

  public static boolean FXAA = false;
  public static boolean WIREFRAME = false;
  public static int GLSL_VERSION = 420;

  /** GLFW {@code Window} wrapper object. */
  public static Window window;

  /** Engine {@code SceneManager}. */
  public static SceneManager scene_manager;

  /** Total available CPU cores. */
  private static int cores;

  /** FPS count of last second. */
  public static int FPS;

  /** UPS count of last run of update thread. */
  public static int UPS;

  /** Duration of the last update cycle in NS */
  public static long UT;

  public static boolean canRender = false;

  /**
   * Returns the current time since the start of the {@link Engine} in {@link java.util.concurrent.TimeUnit#SECONDS}.
   * @return The time since the start of the {@link Engine} in {@link java.util.concurrent.TimeUnit#SECONDS}.
   */
  public static double time()
  {
    if (Settings.getb("WindowTime"))
    {
      return glfwGetTime();
    }
    return System.currentTimeMillis();
  }

  /**
   * Initializes Engine Resources.
   */
  private static void initialize()
  {
    EngineInitialization.initialize();
    Engine.window = new Window();
    Input.initialize(Engine.window);
    AssetManager.initialize();
    GUI.initialize();

    Engine.scene_manager = new SceneManager();

    Engine.FPS = 0;
    Engine.UPS = 0;

    Engine.cores = Runtime.getRuntime().availableProcessors();
    if (Engine.cores <= 0) { System.err.println("how did you even manage to do this"); }

    try
    {
      Engine.loop();
    }
    catch(Exception e)
    {
      // why did I make this
      ExceptionDialog dialog = new ExceptionDialog(
        "Fatal Error",
        "OOPSIE WOOPSIE!! Uwu We make a fucky wucky!! A wittle fucko boingo! The code monkeys at our headquarters are working VEWY HAWD to fix this!",
        e
      );
      dialog.setVisible(true);
    }
  }

  /**
   * Internal method.
   * Cleans up engine resources.
   */
  private static void terminate()
  {
    // terminate the loaded scenes' resources
    Engine.scene_manager.terminate();

    // cleanup OpenGL resources
    Shader.terminate();
    Filter.terminate();
    FrameBuffer.terminate();
    DeferredMeshBatcher.terminate();

    // cleanup Bullet resources
    Assimp.terminate();
  }

  /**
   * Internal Method.
   * Main Engine Loop.
   * Measures FPS, as the main thread is sued for rendering.
   */
  private static void loop()
  {
    Scene scene = new GameScene();
    Engine.scene_manager.setScene(scene);

    double lfps = Engine.time();
    int fps = 0;
    double last = Engine.time();

    while (!window.shouldClose())
    {
      double time = Engine.time();

      if (time - lfps > 1000)
      {
        Engine.FPS = fps;
        ((Label) GUI.getElement("fps-label")).text("FPS:\t" + Engine.FPS);
        lfps = Engine.time();
        fps = 0;
      }

      Engine.canRender = true;

      double ft = time - last;

      Input.reset();
      Input.update();

      // main action loop
      Engine.window.clear();

      Engine.scene_manager.integrate(ft);
      Engine.scene_manager.update(ft);
      Engine.scene_manager.render();

      GUI.render();

      Engine.window.update();

      last = time;
      // end of main action loop

      fps++;

      Engine.canRender = false;

      try { Thread.sleep(1); }
      catch (InterruptedException e) { e.printStackTrace(); }
    }
  }

  /**
   * Starts and terminates the {@link Engine}.
   * @param args Command-Line arguments.
   */
  public static void main(String[] args)
  {
    GdxNativesLoader.load();
    Bullet.init();

    Engine.initialize();
    Engine.terminate();
  }
}