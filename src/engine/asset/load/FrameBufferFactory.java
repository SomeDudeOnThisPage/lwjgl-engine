package engine.asset.load;

import engine.gfx.Texture2D;
import engine.gfx.buffer.FrameBuffer;
import engine.gfx.opengl.buffer.GLFrameBuffer;
import engine.util.XMLUtil;
import org.joml.Vector2i;
import org.w3c.dom.Element;

import java.util.ArrayList;

import static engine.Engine.AssetManager;

public class FrameBufferFactory implements AssetFactory<FrameBuffer>
{
  @Override
  public String tag()
  {
    return "framebuffer";
  }

  @Override
  public FrameBuffer load(Element xml) throws AssetLoadingException
  {
    int width = XMLUtil.int1(XMLUtil.element(xml, "width").getTextContent().trim());
    int height = XMLUtil.int1(XMLUtil.element(xml, "height").getTextContent().trim());

    // validate all needed textures
    ArrayList<Element> elements = XMLUtil.elements(xml, "texture");
    ArrayList<Texture2D> textures = new ArrayList<>();

    // create a list of texture attachments
    for (Element element : elements)
    {
      String asset = element.getTextContent().trim();
      Texture2D texture = AssetManager.request(asset, Texture2D.class);
      textures.add(texture);
    }

    // base frame buffer size off of texture size if needed
    if (width <= 0) { width = textures.get(0).size().x; }
    if (height <= 0) { height = textures.get(0).size().y; }

    // actually add the textures
    FrameBuffer buffer = new GLFrameBuffer(new Vector2i(width, height));
    for (Texture2D texture : textures)
    {
      buffer.addTexture2D(texture);
    }
    return buffer;
  }
}
















