package engine.core.gfx.material;

import engine.core.gfx.batching.AssetManager;
import org.joml.Vector3f;

public class PBRMaterialFlat extends Material
{
  public static final int UNIFORM_MATERIAL_INSTANCE_BYTES = 32;

  private Vector3f color;
  private float ao;
  private float roughness;
  private float metallic;
  private float emissive;

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