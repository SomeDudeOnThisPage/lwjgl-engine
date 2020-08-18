package engine.gfx.opengl.texture;

import engine.asset.load.STBILoader;
import engine.gfx.BaseAssetBindable;
import engine.gfx.Texture2D;
import org.joml.Vector2i;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL45C.*;

public class GLTexture2D extends BaseAssetBindable implements Texture2D
{
  private int slot;
  private final int target;

  private final GLTextureFormat format;
  private final GLTextureFilter filter;
  private final GLTextureWrap   wrap;

  private final Vector2i size;

  @Override
  public int getTextureSlot()
  {
    return this.slot;
  }

  public int getTarget()
  {
    return this.target;
  }

  @Override
  public Vector2i size()
  {
    return this.size;
  }

  @Override
  public void slot(int slot)
  {
    this.slot = slot;
  }

  @Override
  public void bind()
  {
    this.bind(this.slot);
  }

  @Override
  public void bind(int slot)
  {
    glActiveTexture(GL_TEXTURE0 + slot);
    this.slot = slot;
    glBindTexture(this.target, this.id);
  }

  @Override
  public void unbind()
  {
    glActiveTexture(this.slot);
    glBindTexture(this.target, GL_NONE);
  }

  @Override
  public void dispose()
  {
    this.unbind();
    if (glIsTexture(this.id))
    {
      glDeleteTextures(this.id);
    }
  }

  /**
   * Creates an empty texture.
   */
  public GLTexture2D(Vector2i size, GLTextureFormat format, GLTextureFilter filter, GLTextureWrap wrap)
  {
    this.id = glGenTextures();
    this.slot = 0;
    this.size = size;

    this.target = GL_TEXTURE_2D;
    this.format = format;
    this.filter = filter;
    this.wrap = wrap;

    this.bind();
    glTexImage2D(
      GL_TEXTURE_2D,
      0,
      format.internal(),
      size.x,
      size.y,
      0, format.type(),
      format.data(),
      0
    );

    this.filter.apply(GL_TEXTURE_2D);
    this.unbind();
  }

  /**
   * Creates a texture with an image source.
   */
  public GLTexture2D(String texture, GLTextureFormat format, GLTextureFilter filter, GLTextureWrap wrap)
  {
    this.id = glGenTextures();
    this.slot = 0;

    this.target = GL_TEXTURE_2D;
    this.format = format;
    this.filter = filter;
    this.wrap = wrap;

    STBILoader loader = new STBILoader();
    loader.load(texture, this.format.components());

    this.bind();
    glTexImage2D(
      GL_TEXTURE_2D,
      0,
      format.internal(),
      loader.width(),
      loader.height(),
      0,
      format.type(),
      format.data(),
      loader.data()
    );

    this.filter.apply(GL_TEXTURE_2D);

    this.unbind();

    this.size = new Vector2i(loader.width(), loader.height());
    loader.free();
  }
}
