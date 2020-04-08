package engine;

import engine.core.gfx.UniformBuffer;

import java.util.HashMap;

import static org.lwjgl.opengl.GL33C.*;
import static org.lwjgl.glfw.GLFW.*;

public class EngineSettings
{
  public static int KEY_DEBUG = GLFW_KEY_INSERT;

  private static int ubEngineBinding;
  private static UniformBuffer ubEngine;

  private static HashMap<String, String> settings;

  public static synchronized void initialize()
  {
    EngineSettings.settings = new HashMap<>();

    EngineSettings.ubEngineBinding = Math.min(Math.min(
      glGetInteger(GL_MAX_VERTEX_UNIFORM_BLOCKS),
      glGetInteger(GL_MAX_GEOMETRY_UNIFORM_BLOCKS)),
      glGetInteger(GL_MAX_FRAGMENT_UNIFORM_BLOCKS))
      - 1;

    EngineSettings.ubEngine = new UniformBuffer(glGetInteger(GL_MAX_UNIFORM_BLOCK_SIZE), EngineSettings.ubEngineBinding);

    EngineSettings.settings.put("fGamma", "2.2");
  }

  public static float getf(String setting)
  {
    if (setting.charAt(0) != 'f') { System.err.println("wrong setting type: expected float"); return 0.0f; }
    return Float.parseFloat(EngineSettings.settings.get(setting));
  }

  public static int geti(String setting)
  {
    //if (setting.charAt(0) != 'i') { System.err.println("wrong setting type: expected integer"); return 0; }
    return Integer.parseInt(EngineSettings.settings.get(setting));
  }
}
