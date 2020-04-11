package engine.core.gfx.shadow;

import engine.core.gfx.FrameBuffer;
import engine.core.gfx.Shader;
import engine.core.gfx.texture.*;
import engine.util.settings.Settings;
import org.lwjgl.BufferUtils;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL30C.*;
import static org.lwjgl.opengl.GL32C.glFramebufferTexture;

public class ShadowMapBuffer extends FrameBuffer
{
  private static final int UNIFORM_DIRECTIONAL_SHADOW_BUFFER_TEXTURE_POSITION = 8;

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
  private TextureArray2D directional;

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
    this.directional.bind(ShadowMapBuffer.UNIFORM_DIRECTIONAL_SHADOW_BUFFER_TEXTURE_POSITION);
    shader.setUniform("u_directional_shadows", ShadowMapBuffer.UNIFORM_DIRECTIONAL_SHADOW_BUFFER_TEXTURE_POSITION);
  }

  public ShadowMapBuffer()
  {
    super(Settings.geti("ShadowMapResolution"), Settings.geti("ShadowMapResolution"));

    this.bind();

    this.directional = new TextureArray2D(
      Settings.geti("ShadowMapResolution"),
      Settings.geti("ShadowMapResolution"),
      Settings.geti("Max2DShadowMaps"),
      new TextureFilterLinear(),
      new TextureWrap(GL_CLAMP_TO_BORDER),
      new TextureFormat(GL_RGBA32F, GL_RGBA, GL_FLOAT)
    );

    this.directional.bind();
    float[] borderColor = {0.0f, 0.0f, 0.0f, 1.0f};
    glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, borderColor);

    this.maps2D = BufferUtils.createIntBuffer(Settings.geti("Max2DShadowMaps"));

    //glFramebufferTextureLayer(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, this.directional.getID(), 0, 0);
    //glFramebufferTextureLayer(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT1, this.directional.getID(), 0, 1);

    TextureArray2D depth = new TextureArray2D(
      Settings.geti("ShadowMapResolution"),
      Settings.geti("ShadowMapResolution"),
      Settings.geti("Max2DShadowMaps"),
      new TextureFilterLinear(),
      new TextureWrap(GL_CLAMP_TO_BORDER),
      new TextureFormat(GL_DEPTH_COMPONENT32, GL_DEPTH_COMPONENT, GL_FLOAT)
    );

    depth.bind();
    glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, borderColor);

    for (int i = 0; i < Settings.geti("Max2DShadowMaps"); i++)
    {
      this.maps2D.put(GL_COLOR_ATTACHMENT0 + i);
      this.directional.bind();
      glFramebufferTextureLayer(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0 + i, this.directional.getID(), 0, i);

      depth.bind();
      glFramebufferTexture3D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D_ARRAY, depth.getID(), 0, 0);
      glFramebufferTextureLayer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, depth.getID(),0, i);
    }
    this.maps2D.flip();

    this.directional.unbind();
    depth.unbind();

    System.err.println(glGetError());
    System.err.println(glGetError());

  }
}