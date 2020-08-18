package engine.asset.load;

import engine.Engine;
import engine.gfx.Texture2D;
import engine.gfx.opengl.texture.*;
import engine.util.XMLUtil;
import org.joml.Vector2i;
import org.w3c.dom.Element;

import java.util.HashMap;

import static org.lwjgl.opengl.GL45C.*;

public class TextureFactory implements AssetFactory<Texture2D>
{
  private static final HashMap<String, Integer> gl_formatInternal = new HashMap<>()
  {{
    // XXXX16F
    put("R16F",     GL_R16F     );
    put("RG16F",    GL_RG16F    );
    put("RGB16F",   GL_RGB16F   );
    put("RGBA16F",  GL_RGBA16F  );

    // XXXX32F
    put("R32F",     GL_R32F         );
    put("RG32F",    GL_RG32F        );
    put("RGB32F",   GL_RGB32F       );
    put("RGBA32F",  GL_RGBA32F      );
    put("SRGB8",    GL_SRGB8        );
    put("SRGB8A8",  GL_SRGB8_ALPHA8 );

    // simple
    put("RED",  GL_RED );
    put("RG",   GL_RG  );
    put("RGB",  GL_RGB );
    put("RGBA", GL_RGBA);
    put("SRGB", GL_SRGB);
  }};

  private static final HashMap<String, Integer> gl_format = new HashMap<>()
  {{
    put("RED",  GL_RED );
    put("RG",   GL_RG  );
    put("RGB",  GL_RGB );
    put("RGBA", GL_RGBA);
    put("SRGB", GL_SRGB);
  }};

  private static final HashMap<String, Integer> gl_type = new HashMap<>()
  {{
    put("float",  GL_FLOAT        );
    put("int",    GL_INT          );
    put("uint",   GL_UNSIGNED_INT );
    put("byte",   GL_BYTE         );
    put("ubyte",  GL_UNSIGNED_BYTE);
  }};

  private static final HashMap<String, Integer> gl_wrap = new HashMap<>()
  {{
    put("border", GL_CLAMP_TO_BORDER);
    put("edge",   GL_CLAMP_TO_EDGE);
    put("repeat", GL_REPEAT);
  }};

  private static final HashMap<String, Class<? extends GLTextureFilter>> gl_filter = new HashMap<>()
  {{
    put("linear", GLTextureFilterLinear.class);
    put("bilinear", GLTextureFilterBilinear.class);
  }};

  @Override
  public String tag()
  {
    return "texture2d";
  }

  @Override
  public Texture2D load(Element xml) throws AssetLoadingException
  {
    // get texture slot (default 0)
    int slot = 0;
    try
    {
      Element eslot = XMLUtil.element(xml, "slot");
      slot = XMLUtil.int1(eslot.getTextContent());
    }
    catch (Exception ignored) {}


    // create texture format
    Element eformat = XMLUtil.element(xml, "format");

    GLTextureFormat format = new GLTextureFormat(
      gl_formatInternal.get(eformat.getAttribute("internal")),
      gl_format.get(eformat.getAttribute("format")),
      gl_type.get(eformat.getAttribute("type"))
    );

    // create texture wrap
    Element ewrap = XMLUtil.element(xml, "wrap");

    GLTextureWrap wrap;
    if (ewrap.hasAttribute("wrap"))
    {
      // use unified wrap for s and t
      wrap = new GLTextureWrap(gl_wrap.get(ewrap.getAttribute("wrap")));
    }
    else
    {
      wrap = new GLTextureWrap(gl_wrap.get(ewrap.getAttribute("s")), gl_wrap.get(ewrap.getAttribute("t")));
    }

    // create texture filter
    Element efilter = XMLUtil.element(xml, "filter");

    GLTextureFilter filter;

    try
    {
      // boo!
      filter = gl_filter.get(efilter.getAttribute("type")).getDeclaredConstructor().newInstance();
    }
    catch(Exception e)
    {
      throw new AssetLoadingException("failed to create texture filter - " + e.getMessage());
    }

    Element src = XMLUtil.element(xml, "source");

    if (src.getTextContent().equalsIgnoreCase("null"))
    {
      // create empty texture
      Element esize = (Element) xml.getElementsByTagName("size").item(0);
      String sx = esize.getAttribute("x");
      String sy = esize.getAttribute("y");

      int x = sx.equalsIgnoreCase("auto") ? Engine.Display.size().x : XMLUtil.int1(sx);
      int y = sy.equalsIgnoreCase("auto") ? Engine.Display.size().y : XMLUtil.int1(sy);

      Vector2i size = new Vector2i(x, y);

      Texture2D texture = new GLTexture2D(
        size,
        format,
        filter,
        wrap
      );
      texture.slot(slot);

      return texture;
    }
    else
    {
      // create texture from src tag
      Texture2D texture = new GLTexture2D(
        src.getTextContent(),
        format,
        filter,
        wrap
      );
      texture.slot(slot);

      return texture;
    }
  }
}
