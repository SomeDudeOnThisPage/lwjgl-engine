package engine.core.gfx.shadow;

import engine.core.gfx.FrameBuffer;
import engine.core.gfx.Shader;
import engine.core.gfx.filter.GaussianBlurFilter;
import engine.core.gfx.texture.*;
import engine.core.rendering.GBuffer;
import engine.util.settings.Settings;
import org.joml.Vector2i;
import org.lwjgl.BufferUtils;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL30C.*;

public class ShadowMapBuffer extends FrameBuffer
{
  private GaussianBlurFilter filter;

  /**
   * DrawBuffers for directional shadow maps.
   */
  private IntBuffer maps2D;

  /**
   * DrawBuffers for point shadow maps.
   */
  private IntBuffer maps3D;

  /**
   * Directional shadow maps.
   */
  private Texture[] directional;

  /**
   * Clears both the directional, as well as the point light shadow maps.
   */
  @Override
  public void clear()
  {
    this.bind();
    glDrawBuffers(this.maps2D);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
  }

  public void bind_directional()
  {
    this.bind();
    glDrawBuffers(this.maps2D);
  }

  public void bind(Shader shader)
  {
    int i = GBuffer.UNIFORM_LIGHTING_TEXTURE_BUFFER_POSITION + 1;
    for (Texture texture : this.directional)
    {
      texture.bind(i);
      shader.setUniform("u_shadow_map_2D_" + (i - (GBuffer.UNIFORM_LIGHTING_TEXTURE_BUFFER_POSITION + 1)), i);
      i++;
    }
  }

  public void blur(int layer)
  {
    glDrawBuffers(this.maps2D);
    this.filter.applyLayer(
      this.directional[layer],
      GL_COLOR_ATTACHMENT0 + layer,
      this.directional[this.directional.length - 2],
      GL_COLOR_ATTACHMENT0 + this.directional.length - 2);
  }

  public ShadowMapBuffer()
  {
    super(Settings.geti("ShadowMapResolution"), Settings.geti("ShadowMapResolution"));

    this.filter = new GaussianBlurFilter(1.0f);
    float[] borderColor = {1.0f, 1.0f, 1.0f, 1.0f};

    this.bind();

    this.directional = new Texture[Settings.geti("Max2DShadowMaps") + 1];
    this.maps2D = BufferUtils.createIntBuffer(Settings.geti("Max2DShadowMaps") + 1);

    for (int i = 0; i <= Settings.geti("Max2DShadowMaps"); i++)
    {
      this.directional[i] = new Texture(
        new Vector2i(Settings.geti("ShadowMapResolution")),
        new TextureFormat(GL_RGBA32F, GL_RGBA, GL_FLOAT),
        new TextureWrap(GL_CLAMP_TO_BORDER),
        new TextureFilterBilinear()
      );

      this.directional[i].bind();
      glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, borderColor);

      this.addTexture(this.directional[i], GL_COLOR_ATTACHMENT0 + i);
      this.maps2D.put(GL_COLOR_ATTACHMENT0 + i);
    }
    this.maps2D.flip();

    this.addDepthTexture(Settings.geti("ShadowMapResolution"), Settings.geti("ShadowMapResolution"), new TextureFilterBilinear());
  }
}