package engine;

import engine.asset.AssetManager;
import engine.exception.GLException;
import engine.physics.BulletPhysics;
import engine.platform.input.Input;
import engine.platform.window.Window;
import engine.platform.window.glfw.GLFWWindow;
import engine.platform.logging.ConsoleLogger;
import engine.platform.logging.Logger;
import engine.scene.Scene;
import engine.ui.DebugGUI;
import editor.DevelopmentScene3D;
import engine.util.EngineINI;
import org.joml.Vector2i;
import soundwav.SoundWav;

import java.util.concurrent.TimeUnit;

import static org.lwjgl.glfw.GLFW.*;

public final class Engine
{
  public static Logger Log;
  public static Window Display;
  public static Console console;
  public static AssetManager AssetManager;

  // placeholder
  private static final boolean useWindowTime = true;

  private static boolean renderDebug = true;
  private static boolean debugToggle = false;

  public static double time()
  {
    if (useWindowTime)
    {
      return glfwGetTime();
    }
    else
    {
      return System.currentTimeMillis() / 1000.0f;
    }
  }

  public static void main(String[] params)
  {
    // load ini
    EngineINI.getInstance();
    Engine.console = Console.getInstance();

    // initialize console
    Console.addConVar("imgui_enabled", new Console.ConVar<>(false));
    Console.addConVar("debug_lights", new Console.ConVar<>(true));

    Console.addCommand("imgui_add", args ->
    {
      try
      {
        Class<? extends DebugGUI> clazz = (Class<? extends DebugGUI>) Class.forName(args[0]);
        DebugGUI.add(clazz.getDeclaredConstructor().newInstance());
      }
      catch (ClassNotFoundException e)
      {
        Console.error("could not find DebugUI class '" + args[0] + "'");
      }
    });
    Console.addCommand("exit", args -> Engine.Display.exit(true));
    // execute startup configs
    for (String config : EngineINI.getInstance().config.exec)
    {
      Console.exec(config);
    }

    Engine.Log = new ConsoleLogger();
    Engine.Display = new GLFWWindow(new Vector2i(1600, 900), GLFWWindow.VSYNC_ENABLED);
    Engine.AssetManager = engine.asset.AssetManager.getInstance();
    DebugGUI.initialize(Engine.Display.handle());

    Input.initialize(Engine.Display);
    BulletPhysics.initialize();

    try // erromr hamdelimg
    {
      Scene main = new DevelopmentScene3D();
      main.onInit();
      main.onEnter();

      Scene next;

      double last = Engine.time();

      while (!Engine.Display.exit())
      {
        double time = Engine.time();
        double ft = time - last;

        Engine.Display.clear();
        Input.reset();
        Engine.Display.poll();

        if (Input.keyDown(GLFW_KEY_KP_DIVIDE))
        {
          if (!Engine.debugToggle)
          {
            Engine.renderDebug = !Engine.renderDebug;
            Engine.debugToggle = true;
          }
        }
        else
        {
          Engine.debugToggle = false;
        }

        DebugGUI.preFrame((float) ft, main.viewport());
        next = main.tick((float) ft);
        main.render();
        if (Engine.renderDebug) { DebugGUI.renderGUIS(next); }
        DebugGUI.postFrame();

        if (!main.equals(next))
        {
          main.onExit();
          main = next;
          main.onEnter();
        }

        Engine.Display.swap();
        Engine.AssetManager.update();

        last = time;
        TimeUnit.NANOSECONDS.sleep(1);
      }
      main.onExit();
      main.onDispose();
    }
    catch(Exception e)
    {
      if (e instanceof GLException)
      {
        Log.error("[OPENGL] " + e);
      }
      else
      {
        Log.error(e.getMessage());
        Engine.Display.exit(true);
      }

      e.printStackTrace();
    }

    Engine.AssetManager.dispose();
    DebugGUI.dispose();
  }
}
