package engine.core.gui;

import engine.util.Resource;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;

import static org.lwjgl.nanovg.NanoVG.nvgCreateFontMem;
import static org.lwjgl.nanovg.NanoVG.nvgFontFace;

public class Font
{
  public static final String FONT_IDENTIFIER_DEFAULT = "system";
  public static final String FONT_DEFAULT = "resources/fonts/RobotoMono-Medium.ttf";

  /**
   * Maps {@link Font} identifier {@link String}s to the individual {@link Font} objects.
   */
  private static final HashMap<String, Font> instances = new HashMap<>();

  /**
   * Maps {@link Font} .tff {@link java.io.File} paths to the {@link ByteBuffer}s containing the .ttf data.
   */
  private static final HashMap<String, ByteBuffer> buffers = new HashMap<>();

  private String handle;
  private long id;

  public static Font getDefault()
  {
    return Font.instances.get(FONT_IDENTIFIER_DEFAULT);
  }

  public static Font getInstance(String identifier)
  {
    if (Font.instances.containsKey(identifier))
    {
      return Font.instances.get(identifier);
    }
    return Font.instances.get(FONT_IDENTIFIER_DEFAULT);
  }

  public static Font load(String identifier, String path)
  {
    try
    {
      ByteBuffer buffer = Resource.load(path);
      long font = nvgCreateFontMem(GUI.vg(), identifier, buffer, 0);
      Font.instances.put(identifier, new Font(identifier, font));
      Font.buffers.put(path, buffer);
    }
    catch (IOException e)
    {
      System.err.println("could not load font .ttf definitions for font '" + path + "' - file not found");
      System.exit(-1);
    }

    return Font.instances.get(identifier);
  }

  public void bind()
  {
    nvgFontFace(GUI.vg(), this.handle);
  }

  public Font(String handle, long id)
  {
    this.handle = handle;
    this.id = id;
  }
}
