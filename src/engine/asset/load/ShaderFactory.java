package engine.asset.load;

import engine.gfx.ShaderProgram;
import engine.gfx.opengl.shader.GLShaderProgram;
import engine.util.XMLUtil;
import org.w3c.dom.Element;

public class ShaderFactory implements AssetFactory<ShaderProgram>
{
  public String tag()
  {
    return "program";
  }

  @Override
  public ShaderProgram load(Element xml) throws AssetLoadingException
  {
    String missing = XMLUtil.validate(xml, "vs", "fs");
    if (missing != null)
    {
      throw new AssetLoadingException("failed to parse xml - missing tag '" + missing + "'");
    }

    String vs = xml.getElementsByTagName("vs").item(0).getTextContent();
    String fs = xml.getElementsByTagName("fs").item(0).getTextContent();

    // load ShaderProgram implementation depending on the used graphics API
    // todo: for now only OpenGL
    return new GLShaderProgram(vs, fs);
  }
}