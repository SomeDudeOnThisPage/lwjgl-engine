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
import engine.core.scene.Scene;
import engine.core.scene.SceneManager;
import engine.core.Window;
import engine.util.ExceptionDialog;

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
  private static int FPS;

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
    return glfwGetTime();
  }

  /**
   * Initializes Engine Resources.
   */
  private static void initialize()
  {
    Engine.window = new Window();
    Input.initialize(Engine.window);
    EngineSettings.initialize();
    AssetManager.initialize();

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

    // delete shaders
    Shader.terminate();

    // delete filters (shaders with a fancy name)
    Filter.terminate();

    // delete framebuffers
    FrameBuffer.terminate();

    // delete batch buffers
    DeferredMeshBatcher.terminate();
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

      if (time - lfps > 1000000000)
      {
        Engine.FPS = fps;
        System.out.println("FPS: " + Engine.FPS + " UPS: " + Engine.UPS + " UT " + (Engine.UT / 1000000.0));

        lfps = System.nanoTime();
        fps = 0;
      }

      Engine.canRender = true;

      Input.reset();
      Input.update();

      // main action loop
      Engine.window.clear();
      Engine.scene_manager.update(time - last);
      Engine.scene_manager.render();
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