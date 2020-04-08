package engine.core.rendering;

import engine.core.gfx.FrameBuffer;
import engine.core.gfx.Shader;
import engine.core.gfx.texture.Texture;
import org.lwjgl.BufferUtils;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11C.GL_FLOAT;
import static org.lwjgl.opengl.GL11C.GL_RGB;
import static org.lwjgl.opengl.GL30C.*;

public class GBuffer extends FrameBuffer
{
  public static final int UNIFORM_POSITION_TEXTURE_BUFFER_POSITION = 0;
  public static final int UNIFORM_NORMAL_TEXTURE_BUFFER_POSITION = 1;
  public static final int UNIFORM_ALBEDO_TEXTURE_BUFFER_POSITION = 2;
  public static final int UNIFORM_RGH_MTL_AO_TEXTURE_BUFFER_POSITION = 3;
  public static final int UNIFORM_LIGHTING_TEXTURE_BUFFER_POSITION = 4;

  private Texture position;
  private Texture normal;
  private Texture albedo;
  private Texture roughness_metallic_ao;

  private Texture lighting;
  private Texture pp;

  private IntBuffer buffers;

  public void bind_geometry()
  {
    glDrawBuffers(this.buffers);
  }

  @Override
  public void clear()
  {
    this.bind();
    glDrawBuffers(this.buffers);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
  }

  /**
   * Binds the gbuffers' textures to their respective slots.
   */
  public void bind_lighting()
  {
    this.position.bind(UNIFORM_POSITION_TEXTURE_BUFFER_POSITION);
    this.normal.bind(UNIFORM_NORMAL_TEXTURE_BUFFER_POSITION);
    this.albedo.bind(UNIFORM_ALBEDO_TEXTURE_BUFFER_POSITION);
    this.roughness_metallic_ao.bind(UNIFORM_RGH_MTL_AO_TEXTURE_BUFFER_POSITION);

    glDrawBuffers(GL_COLOR_ATTACHMENT4);
  }

  public void bind_pp()
  {
    this.lighting.bind(UNIFORM_LIGHTING_TEXTURE_BUFFER_POSITION);

    this.position.bind(UNIFORM_POSITION_TEXTURE_BUFFER_POSITION);
    this.normal.bind(UNIFORM_NORMAL_TEXTURE_BUFFER_POSITION);
    this.albedo.bind(UNIFORM_ALBEDO_TEXTURE_BUFFER_POSITION);
    this.roughness_metallic_ao.bind(UNIFORM_RGH_MTL_AO_TEXTURE_BUFFER_POSITION);

    glDrawBuffers(GL_COLOR_ATTACHMENT5);
  }

  public void bind_forward()
  {

  }

  public Texture getFinalTexture()
  {
    return this.lighting;
  }

  /**
   * Binds the gbuffers' texture slots to a shader.
   * @param shader The shader object.
   */
  public void bind(Shader shader)
  {
    shader.setUniform("u_gbuffer.position", 0);
    shader.setUniform("u_gbuffer.normal", 1);
    shader.setUniform("u_gbuffer.albedo", 2);
    shader.setUniform("u_gbuffer.roughness_metallic_ao", 3);
  }

  public GBuffer(int width, int height)
  {
    super(width, height);

    this.buffers = BufferUtils.createIntBuffer(6);
    this.buffers.put(GL_COLOR_ATTACHMENT0);
    this.buffers.put(GL_COLOR_ATTACHMENT1);
    this.buffers.put(GL_COLOR_ATTACHMENT2);
    this.buffers.put(GL_COLOR_ATTACHMENT3);
    this.buffers.put(GL_COLOR_ATTACHMENT4);
    this.buffers.put(GL_COLOR_ATTACHMENT5);
    this.buffers.flip();

    this.bind();

    // create position attachment
    this.position = new Texture(this.width, this.height, GL_RGB16F, GL_RGB, GL_FLOAT);
    this.addTexture(this.position, GL_COLOR_ATTACHMENT0);

    // create normal attachment
    this.normal = new Texture(this.width, this.height, GL_RGB16F, GL_RGB, GL_FLOAT);
    this.addTexture(this.normal, GL_COLOR_ATTACHMENT1);

    // create albedo & specular attachment
    this.albedo = new Texture(this.width, this.height, GL_RGB16F, GL_RGB, GL_FLOAT);
    this.addTexture(this.albedo, GL_COLOR_ATTACHMENT2);

    // create roughness, metallic, ao and emissive attachment
    this.roughness_metallic_ao = new Texture(this.width, this.height, GL_RGBA, GL_RGBA, GL_FLOAT);
    this.addTexture(this.roughness_metallic_ao, GL_COLOR_ATTACHMENT3);

    // create final lighting attachment
    this.lighting = new Texture(this.width, this.height, GL_RGB16F, GL_RGB, GL_FLOAT);
    this.addTexture(this.lighting, GL_COLOR_ATTACHMENT4);

    // create final texture attachment
    this.pp = new Texture(this.width, this.height, GL_RGB16F, GL_RGB, GL_FLOAT);
    this.addTexture(this.pp, GL_COLOR_ATTACHMENT5);

    this.addDepthTexture();
  }
}