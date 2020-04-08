package engine.util.settings;

import java.util.HashMap;

public class Settings
{
  private static HashMap<String, EngineSetting> settings = new HashMap<>();

  public static String gets(String index)
  {
    return (String) settings.get(index).get();
  }

  public static float getf(String index)
  {
    return (float) settings.get(index).get();
  }

  public static int geti(String index)
  {
    return (int) Settings.settings.get(index).get();
  }

  public static boolean getb(String index)
  {
    return (boolean) Settings.settings.get(index).get();
  }

  public static synchronized void register(String key, EngineSetting setting)
  {
    Settings.settings.put(key, setting);
  }
}