package engine.core.gfx.material;

import engine.core.gfx.batching.AssetManager;
import org.joml.Vector3f;
import org.w3c.dom.Element;

public class PBRMaterialFlat extends MaterialArchetype
{
  public static final int UNIFORM_MATERIAL_INSTANCE_BYTES = 32;

  private Vector3f color;
  private float ao;
  private float roughness;
  private float metallic;
  private float emissive;

  @Override
  public MaterialArchetype load(Element xml)
  {
    // get color values from color tag as string array
    String[] colors = xml.getElementsByTagName("color")
      .item(0)
      .getTextContent()
      .trim()
      .split("\\s+");

    Vector3f color = new Vector3f(
      Float.valueOf(colors[0]), // red
      Float.valueOf(colors[1]), // green
      Float.valueOf(colors[2])  // blue
    );

    float metallic =  Float.valueOf(xml.getElementsByTagName("metallic")  .item(0).getTextContent());
    float roughness = Float.valueOf(xml.getElementsByTagName("roughness") .item(0).getTextContent());
    float ao =        Float.valueOf(xml.getElementsByTagName("ao")        .item(0).getTextContent());
    float emissive =  Float.valueOf(xml.getElementsByTagName("emissive")  .item(0).getTextContent());

    this.color.set(color);
    this.ao         = ao;
    this.metallic   = metallic;
    this.roughness  = roughness;
    this.emissive   = emissive;

    return this;
  }

  @Override
  public void bind()
  {
    this.shader.bind();
    this.shader.setUniform("u_material.color",      this.color);
    this.shader.setUniform("u_material.ao",         this.ao);
    this.shader.setUniform("u_material.roughness",  this.roughness);
    this.shader.setUniform("u_material.metallic",   this.metallic);
    this.shader.setUniform("u_material.emissive",   this.emissive);
  }

  public PBRMaterialFlat()
  {
    this(
      new Vector3f(1.0f),
      0.5f,
      1.0f,
      0.0f,
      0.0f
    );
  }

  public PBRMaterialFlat(Vector3f color, float ao, float roughness, float metallic, float emissive)
  {
    this.shader = AssetManager.getShader("deferred/pbrflat");

    this.color = color;
    this.ao = ao;
    this.roughness = roughness;
    this.metallic = metallic;
    this.emissive = emissive;
  }
}