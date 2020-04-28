package engine.core.gfx.material;

import engine.core.assetmanager.AssetManager;
import engine.core.gfx.texture.*;
import engine.util.XMLCoder;
import engine.util.logging.Logger;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Element;

import java.io.IOException;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL12C.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL13C.GL_CLAMP_TO_BORDER;
import static org.lwjgl.opengl.GL21C.GL_SRGB;

public class PBRMaterialTextured extends MaterialArchetype
{
  private static final int ALBEDO_MAP_SLOT = 0;
  private static final int NORMAL_MAP_SLOT = 1;
  private static final int METALLIC_MAP_SLOT = 2;
  private static final int ROUGHNESS_MAP_SLOT = 3;
  private static final int AMBIENT_OCCLUSION_MAP_SLOT = 4;
  private static final int EMISSION_MAP_SLOT = 5;

  private Texture[] textures;

  @Override
  public MaterialArchetype load(@NotNull Element xml) throws IOException
  {
    String albedo     = XMLCoder.gets(xml, "albedo"   );
    String normal     = XMLCoder.gets(xml, "normal"   );
    String metallic   = XMLCoder.gets(xml, "metallic" );
    String roughness  = XMLCoder.gets(xml, "roughness");
    String ao         = XMLCoder.gets(xml, "ao"       );
    String emissive   = XMLCoder.gets(xml, "emissive" );

    TextureWrap wrap;
    ITextureFilter filter;

    switch (XMLCoder.gets(xml, "wrap").toLowerCase())
    {
      case "clamp_to_edge":
        wrap = new TextureWrap(GL_CLAMP_TO_EDGE);
        break;
      case "clamp_to_border":
        wrap = new TextureWrap(GL_CLAMP_TO_BORDER);
        break;
      default:
        wrap = new TextureWrap(GL_REPEAT);
        break;
    }

    switch (XMLCoder.gets(xml, "filter").toLowerCase())
    {
      case "trilinear":
        float bias = Float.valueOf(XMLCoder.geta(xml, "filter", "bias"));
        filter = new TextureFilterTrilinear(bias);
        break;
      case "bilinear":
        filter = new TextureFilterBilinear();
        break;
      default:
        filter = new TextureFilterLinear();
        break;
    }

    this.textures[ALBEDO_MAP_SLOT] = new Texture(
      albedo,
      new TextureFormat(GL_SRGB, GL_RGBA, GL_UNSIGNED_BYTE),
      wrap,
      filter
    );

    this.textures[NORMAL_MAP_SLOT] = new Texture(
      normal,
      new TextureFormat(GL_RGBA, GL_RGBA, GL_UNSIGNED_BYTE),
      wrap,
      filter
    );

    this.textures[METALLIC_MAP_SLOT] = new Texture(
      metallic,
      new TextureFormat(GL_RGBA, GL_RGBA, GL_UNSIGNED_BYTE),
      wrap,
      filter
    );

    this.textures[ROUGHNESS_MAP_SLOT] = new Texture(
      roughness,
      new TextureFormat(GL_RGBA, GL_RGBA, GL_UNSIGNED_BYTE),
      wrap,
      filter
    );

    this.textures[AMBIENT_OCCLUSION_MAP_SLOT] = new Texture(
      ao,
      new TextureFormat(GL_RGBA, GL_RGBA, GL_UNSIGNED_BYTE),
      wrap,
      filter
    );

    this.textures[EMISSION_MAP_SLOT] = new Texture(
      emissive,
      new TextureFormat(GL_RGB, GL_RGB, GL_UNSIGNED_BYTE),
      wrap,
      filter
    );

    return this;
  }

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

  public PBRMaterialTextured()
  {
    this.shader = AssetManager.getShader("deferred/geometry");
    this.textures = new Texture[6];
  }
}