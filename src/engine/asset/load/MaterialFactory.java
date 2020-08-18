package engine.asset.load;

import engine.gfx.material.Material;
import engine.gfx.material.MaterialPhongColor;
import engine.gfx.material.MaterialPhongTextured;
import engine.util.XMLUtil;
import org.joml.Vector3f;
import org.w3c.dom.Element;

import java.util.Objects;

public class MaterialFactory implements AssetFactory<Material>
{
  @Override
  public String tag()
  {
    return "material";
  }

  @Override
  public Material load(Element xml) throws AssetLoadingException
  {
    String archetype = xml.getAttribute("archetype");
    if (archetype == null || archetype.equals(""))
    {
      throw new AssetLoadingException("failed to load material - attribute 'archetype' is not defined");
    }

    switch (archetype)
    {
      // todo: dynamic archetypes defined by xml
      case "phong_color" ->
      {
        Vector3f ambient = XMLUtil.vec3(xml.getElementsByTagName("ambient").item(0).getTextContent());
        Vector3f diffuse = XMLUtil.vec3(xml.getElementsByTagName("diffuse").item(0).getTextContent());
        Vector3f specular = XMLUtil.vec3(xml.getElementsByTagName("specular").item(0).getTextContent());
        float shininess = XMLUtil.float1(xml.getElementsByTagName("shininess").item(0).getTextContent());

        return new MaterialPhongColor(ambient, diffuse, specular, shininess);
      }
      case "phong_textured" ->
      {
        Vector3f ambient = XMLUtil.vec3(XMLUtil.element(xml, "ambient").getTextContent());
        float shininess = XMLUtil.float1(XMLUtil.element(xml, "shininess").getTextContent());

        String diffuse = XMLUtil.element(xml, "diffuse").getTextContent().trim();
        String specular = XMLUtil.element(xml, "specular").getTextContent().trim();

        return new MaterialPhongTextured(diffuse, specular, ambient, shininess);
      }
      default -> throw new AssetLoadingException("failed to load material - archetype '" + archetype + "' is not defined");
    }
  }
}
