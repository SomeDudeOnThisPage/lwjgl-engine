package engine.util.settings;

import java.util.HashMap;

public class Settings
{
  /**
   * The maximum size of the {@link EngineSetting} {@link engine.core.gfx.UniformBuffer}.
   */
  private static final int SETTINGS_UNIFORM_BUFFER_MAX_SIZE = Short.MAX_VALUE;

  /**
   * The binding point of the {@link EngineSetting} {@link engine.core.gfx.UniformBuffer}.
   */
  private static final int SETTINGS_UNIFORM_BUFFER_BINDING = 5;

  private static HashMap<String, Integer> settings_i = new HashMap<>();
  private static HashMap<String, Float> settings_f = new HashMap<>();
  private static HashMap<String, Boolean> settings_b = new HashMap<>();

  /**
   * Initializes the {@link engine.core.gfx.UniformBuffer} used to hold all {@link EngineSetting}s used in
   * {@link engine.core.gfx.Shader}s.
   * <p>This method needs to be called after the OpenGL context has been initialized.</p>
   */
  private static void initialize()
  {

  }

  public static int geti(String setting)
  {
    return settings_i.get(setting);
  }

  public static float getf(String setting)
  {
    return settings_f.get(setting);
  }

  public static boolean getb(String setting)
  {
    return settings_b.get(setting);
  }

  public static synchronized void load()
  {

  }
}
