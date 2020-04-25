package engine.core.gfx;

import engine.Engine;
import engine.core.gfx.texture.ITextureFilter;
import engine.core.gfx.texture.Texture;
import engine.core.gfx.texture.TextureFilterLinear;
import engine.core.gfx.texture.TextureWrap;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL44C.*;

public class FrameBuffer
{
  private static int current = -1;

  private static ArrayList<FrameBuffer> buffers = new ArrayList<>();

  protected int id;
  protected int width;
  protected int height;

  private boolean colorBuffers = false;
  private Texture depthTexture;

  /**
   * Clears all OpenGL-Resources used by {@link FrameBuffer}s.
   */
  public static void terminate()
  {
    int[] buffers = new int[FrameBuffer.buffers.size()];

    for (int i = 0; i < FrameBuffer.buffers.size(); i++)
    {
      buffers[i] = FrameBuffer.buffers.get(i).getID();
    }

    glDeleteFramebuffers(buffers);
  }

  public int getID()
  {
    return this.id;
  }

  public int getWidth()
  {
    return this.width;
  }

  public int getHeight()
  {
    return this.height;
  }

  public void clear()
  {
    this.bind();
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
  }

  public final void bind()
  {
    if (FrameBuffer.current != this.id)
    {
      glBindFramebuffer(GL_FRAMEBUFFER, this.id);
      glViewport(0, 0, this.width, this.height);
      FrameBuffer.current = this.id;
    }
  }

  public void unbind()
  {
    glBindFramebuffer(GL_FRAMEBUFFER, 0);
    glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
    glBindFramebuffer(GL_READ_FRAMEBUFFER, 0);

    glViewport(0, 0, Engine.window.getWidth(), Engine.window.getHeight());
    FrameBuffer.current = -1;
  }

  public void read()
  {
    glBindFramebuffer(GL_READ_FRAMEBUFFER, this.id);
  }

  public void draw()
  {
    glBindFramebuffer(GL_DRAW_FRAMEBUFFER, this.id);
  }

  public void addTexture(Texture texture, int attachment, int multisample)
  {
    this.colorBuffers = true;
    texture.bind();

    this.bind();
    glFramebufferTexture2D(GL_FRAMEBUFFER, attachment, multisample, texture.getID(), 0);
    this.unbind();

    texture.unbind();
  }

  public void addTexture(Texture texture, int attachment)
  {
    this.colorBuffers = true;
    this.addTexture(texture, attachment, GL_TEXTURE_2D);
  }

  public void addDepthStencil()
  {
    this.bind();

    this.depthTexture = new Texture(this.width, this.height, GL_DEPTH24_STENCIL8, GL_DEPTH_COMPONENT, true);
    this.depthTexture.bind();

    glFramebufferTexture(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, this.depthTexture.getID(), 0);
    this.unbind();

    this.depthTexture.unbind();
  }

  public void addDepthTextureClamped()
  {
    this.bind();

    this.depthTexture = new Texture(width, height, GL_DEPTH_COMPONENT32, GL_DEPTH_COMPONENT, true);
    this.depthTexture.bind();

    glFramebufferTexture(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, this.depthTexture.getID(), 0);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
    float[] borderColor = { 1.0f, 1.0f, 1.0f, 1.0f };
    glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, borderColor);

    if (!this.colorBuffers)
    {
      glDrawBuffer(GL_NONE);
      glReadBuffer(GL_NONE);
    }

    this.unbind();
    this.depthTexture.unbind();
  }

  public void addDepthTexture()
  {
    this.addDepthTexture(this.width, this.height);
  }

  public void addDepthTexture(int width, int height, ITextureFilter filter)
  {
    this.bind();

    this.depthTexture = new Texture(width, height, GL_DEPTH_COMPONENT, GL_DEPTH_COMPONENT, true);
    this.depthTexture.bind();

    glFramebufferTexture(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, this.depthTexture.getID(), 0);

    if (!this.colorBuffers)
    {
      glDrawBuffer(GL_NONE);
      glReadBuffer(GL_NONE);
    }

    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);

    filter.apply(GL_TEXTURE_2D);

    this.unbind();
    this.depthTexture.unbind();
  }

  public void addDepthTexture(int width, int height)
  {
    this.addDepthTexture(width, height, new TextureFilterLinear());
  }

  public void addRenderbuffer(int type, int slot)
  {
    int renderbuffer = glGenRenderbuffers();
    glBindRenderbuffer(GL_RENDERBUFFER, renderbuffer);
    glRenderbufferStorage(GL_RENDERBUFFER, type, this.width, this.height);

    this.bind();
    glFramebufferRenderbuffer(GL_FRAMEBUFFER, slot, GL_RENDERBUFFER, renderbuffer);
    this.unbind();
  }

  public FrameBuffer(int width, int height)
  {
    this.id = glGenFramebuffers();
    this.width = width;
    this.height = height;

    FrameBuffer.buffers.add(this);
  }
}
