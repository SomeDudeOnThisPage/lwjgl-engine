package engine.util;

import com.moandjiezana.toml.Toml;

import java.io.File;
import java.util.ArrayList;

public class EngineINI
{
  private static EngineINI instance;
  public static EngineINI getInstance()
  {
    if (EngineINI.instance == null)
    {
      EngineINI.instance = new Toml().read(new File("platform/engine.toml")).to(EngineINI.class);
    }

    return EngineINI.instance;
  }

  public static final class Path
  {
    public String root;
    public String config;
    public String resources;
    public ArrayList<String> resource_folders;
  }

  public static final class Config
  {
    public ArrayList<String> exec;
  }

  public String title;
  public Path path;
  public Config config;
}
