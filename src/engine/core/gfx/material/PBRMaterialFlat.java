package engine.core.gfx.material;

import engine.core.assetmanager.AssetManager;
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

    // read color data from string array
    Vector3f color = new Vector3f(
      Float.valueOf(colors[0]), // red
      Float.valueOf(colors[1]), // green
      Float.valueOf(colors[2])  // blue
    );

    // read material data from XML elements
    float metallic  = Float.valueOf(xml.getElementsByTagName("metallic")  .item(0).getTextContent().trim());
    float roughness = Float.valueOf(xml.getElementsByTagName("roughness") .item(0).getTextContent().trim());
    float ao        = Float.valueOf(xml.getElementsByTagName("ao")        .item(0).getTextContent().trim());
    float emissive  = Float.valueOf(xml.getElementsByTagName("emissive")  .item(0).getTextContent().trim());

    // set material data of this material
    this.color.set(color);
    this.ao         = ao;
    this.metallic   = metallic;
    this.roughness  = roughness;
    this.emissive   = emissive;

    // don't forget to return this
    return this;
  }

  @Override
  public void bind()
  {
    // bind the shader this material uses to write its' values into the gbuffer
    this.shader.bind();

    // set the required material uniforms of the shader
    this.shader.setUniform("u_material.color",      this.color);
    this.shader.setUniform("u_material.ao",         this.ao);
    this.shader.setUniform("u_material.roughness",  this.roughness);
    this.shader.setUniform("u_material.metallic",   this.metallic);
    this.shader.setUniform("u_material.emissive",   this.emissive);
  }

  public PBRMaterialFlat()
  {
    // get (or load, if it is not yet loaded) the shader instance this material uses
    this.shader = AssetManager.getShader("deferred/pbrflat");

    // set default values to avoid null pointer exceptions
    this.color      = new Vector3f();
    this.roughness  = 1.0f;
    this.ao         = 0.0f;
    this.metallic   = 0.0f;
    this.emissive   = 0.0f;
  }
}