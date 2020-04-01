package engine.core.gfx;

import engine.core.gfx.batching.AssetManager;
import engine.core.gfx.material.Material;
import engine.core.gfx.texture.*;

import static org.lwjgl.opengl.GL33C.*;

public class PBRMaterial extends Material
{
  private static final int ALBEDO_MAP_SLOT = 0;
  private static final int NORMAL_MAP_SLOT = 1;
  private static final int METALLIC_MAP_SLOT = 2;
  private static final int ROUGHNESS_MAP_SLOT = 3;
  private static final int AMBIENT_OCCLUSION_MAP_SLOT = 4;
  private static final int EMISSION_MAP_SLOT = 5;

  @Override
  public void bind()
  {
    this.shader.bind();
    this.textures[ALBEDO_MAP_SLOT].bind(ALBEDO_MAP_SLOT);
    this.shader.setUniform("u_pbr_material.albedo", ALBEDO_MAP_SLOT);

    this.textures[NORMAL_MAP_SLOT].bind(NORMAL_MAP_SLOT);
    this.shader.setUniform("u_pbr_material.normal", NORMAL_MAP_SLOT);

    this.textures[METALLIC_MAP_SLOT].bind(METALLIC_MAP_SLOT);
    this.shader.setUniform("u_pbr_material.metallic", METALLIC_MAP_SLOT);

    this.textures[ROUGHNESS_MAP_SLOT].bind(ROUGHNESS_MAP_SLOT);
    this.shader.setUniform("u_pbr_material.roughness", ROUGHNESS_MAP_SLOT);

    this.textures[AMBIENT_OCCLUSION_MAP_SLOT].bind(AMBIENT_OCCLUSION_MAP_SLOT);
    this.shader.setUniform("u_pbr_material.ambient_occlusion", AMBIENT_OCCLUSION_MAP_SLOT);

    this.textures[EMISSION_MAP_SLOT].bind(EMISSION_MAP_SLOT);
    this.shader.setUniform("u_pbr_material.emission", EMISSION_MAP_SLOT);
  }

  @Override
  public void bind(Shader shader)
  {
    this.textures[ALBEDO_MAP_SLOT].bind(ALBEDO_MAP_SLOT);
    shader.setUniform("u_pbr_material.albedo", ALBEDO_MAP_SLOT);

    this.textures[NORMAL_MAP_SLOT].bind(NORMAL_MAP_SLOT);
    shader.setUniform("u_pbr_material.normal", NORMAL_MAP_SLOT);

    this.textures[METALLIC_MAP_SLOT].bind(METALLIC_MAP_SLOT);
    shader.setUniform("u_pbr_material.metallic", METALLIC_MAP_SLOT);

    this.textures[ROUGHNESS_MAP_SLOT].bind(ROUGHNESS_MAP_SLOT);
    shader.setUniform("u_pbr_material.roughness", ROUGHNESS_MAP_SLOT);

    this.textures[AMBIENT_OCCLUSION_MAP_SLOT].bind(AMBIENT_OCCLUSION_MAP_SLOT);
    shader.setUniform("u_pbr_material.ambient_occlusion", AMBIENT_OCCLUSION_MAP_SLOT);

    this.textures[EMISSION_MAP_SLOT].bind(EMISSION_MAP_SLOT);
    shader.setUniform("u_pbr_material.emission", EMISSION_MAP_SLOT);

    shader.setCurrentMaterial(this);
  }

  public PBRMaterial(String name, TextureWrap wrap)
  {
    this.shader = AssetManager.getShader("deferred/geometry");
    this.textures = new Texture[6];

    this.textures[ALBEDO_MAP_SLOT] = new Texture(
      name + "_albedo.png",
      new TextureFormat(GL_SRGB, GL_RGBA, GL_UNSIGNED_BYTE),
      wrap,
      new TextureFilterTrilinear()
    );

    this.textures[NORMAL_MAP_SLOT] = new Texture(
      name + "_normal.png",
      new TextureFormat(GL_RGBA, GL_RGBA, GL_UNSIGNED_BYTE),
      wrap,
      new TextureFilterTrilinear()
    );

    this.textures[METALLIC_MAP_SLOT] = new Texture(
      name + "_metallic.png",
      new TextureFormat(GL_RGBA, GL_RGBA, GL_UNSIGNED_BYTE),
      wrap,
      new TextureFilterTrilinear()
    );

    this.textures[ROUGHNESS_MAP_SLOT] = new Texture(
      name + "_roughness.png",
      new TextureFormat(GL_RGBA, GL_RGBA, GL_UNSIGNED_BYTE),
      wrap,
      new TextureFilterTrilinear()
    );

    this.textures[AMBIENT_OCCLUSION_MAP_SLOT] = new Texture(
      name + "_ambient_occlusion.png",
      new TextureFormat(GL_RGBA, GL_RGBA, GL_UNSIGNED_BYTE),
      wrap,
      new TextureFilterTrilinear()
    );

    this.textures[EMISSION_MAP_SLOT] = new Texture(
      name + "_emission.png",
      new TextureFormat(GL_RGB, GL_RGB, GL_UNSIGNED_BYTE),
      wrap,
      new TextureFilterTrilinear()
    );
  }

  public PBRMaterial(String name)
  {
    this(name, new TextureWrap(GL_CLAMP_TO_EDGE));
  }
}
