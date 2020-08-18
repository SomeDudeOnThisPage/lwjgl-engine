package engine.gfx.opengl.buffer;

import engine.Engine;
import engine.asset.AssetManager;
import engine.asset.load.AssetFactory;
import engine.gfx.BaseAssetBindable;
import engine.gfx.Texture2D;
import engine.gfx.buffer.FrameBuffer;
import engine.gfx.opengl.GLState;
import engine.gfx.opengl.texture.GLTexture2D;
import org.joml.Vector2i;
import org.lwjgl.BufferUtils;

import java.nio.IntBuffer;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL45C.*;

public class GLFrameBuffer extends BaseAssetBindable implements FrameBuffer
{
  protected Vector2i size;
  private final IntBuffer buffers;

  private final ArrayList<Texture2D> textures;

  @Override
  public void clear(BIT_FLAGS... flags)
  {
    int mask = 0x0;
    if (flags.length > 0) // construct OpenGL bitmask based on our flags
    {
      for (BIT_FLAGS flag : flags)
      {
        switch(flag)
        {
          case COLOR -> mask |= GL_COLOR_BUFFER_BIT;
          case DEPTH -> mask |= GL_DEPTH_BUFFER_BIT;
          case STENCIL -> mask |= GL_STENCIL_BUFFER_BIT;
        }
      }
    }
    else // default: clear color and depth buffer
    {
      mask |= GL_COLOR_BUFFER_BIT;
      mask |= GL_DEPTH_BUFFER_BIT;
    }

    this.bind();
    glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
    glClear(mask);
    glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
  }

  @Override
  public void addTexture2D(Texture2D texture, int attachment)
  {
    if (!(texture instanceof GLTexture2D))
    {
      throw new UnsupportedOperationException("cannot add non-GL-texture to GL framebuffer");
    }

    texture.bind();
    this.bind();

    glFramebufferTexture(
      GL_FRAMEBUFFER,
      GL_COLOR_ATTACHMENT0 + attachment,
      // ((GLTexture2D) texture).getTarget(),
      texture.id(),
      0
    );

    if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
    {
      throw new AssetFactory.AssetLoadingException(glCheckFramebufferStatus(GL_FRAMEBUFFER) + "");
    }

    this.buffers.put(GL_COLOR_ATTACHMENT0 + texture.getTextureSlot());

    this.unbind();
    texture.unbind();
  }

  @Override
  public void addTexture2D(Texture2D texture)
  {
    this.addTexture2D(texture, texture.getTextureSlot());
  }

  @Override
  public void bind()
  {
    if (GLState.framebuffer(GL_FRAMEBUFFER, this.id))
    {
      glDrawBuffers(this.buffers);
      glViewport(0, 0, this.size.x, this.size.y);
    }
  }

  @Override
  public void unbind()
  {
    if (GLState.framebuffer(GL_FRAMEBUFFER, GL_NONE))
    {
      glViewport(0, 0, Engine.Display.size().x, Engine.Display.size().y);
    }
  }

  @Override
  public void dispose()
  {
    // release all textures
    for (Texture2D texture : this.textures)
    {
      AssetManager.getInstance().release(texture, Texture2D.class);
    }

    // dispose framebuffer
    if (glIsFramebuffer(this.id))
    {
      this.unbind();
      glDeleteFramebuffers(this.id);
    }
  }

  public GLFrameBuffer(Vector2i size)
  {
    this.id = glGenFramebuffers();
    this.size = size;
    this.buffers = BufferUtils.createIntBuffer(32);
    this.textures = new ArrayList<>();
  }
}
