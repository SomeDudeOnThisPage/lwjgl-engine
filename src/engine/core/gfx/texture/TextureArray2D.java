package engine.core.gfx.texture;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL42C.*;

public class TextureArray2D extends Texture
{
  @Override
  public void bind(int slot)
  {
    glActiveTexture(GL_TEXTURE0 + slot);
    glBindTexture(GL_TEXTURE_2D_ARRAY, this.id);
  }

  @Override
  public void bind()
  {
    this.bind(0);
  }

  @Override
  public void unbind()
  {
    glBindTexture(GL_TEXTURE_2D_ARRAY, 0);
  }

  public TextureArray2D(int x, int y, int layers, ITextureFilter filter, TextureWrap wrap, TextureFormat format)
  {
    this.format = format;
    this.filter = filter;
    this.wrap = wrap;
    this.width = x;
    this.height = y;

    this.id = glGenTextures();
    glBindTexture(GL_TEXTURE_2D_ARRAY, this.id);

    glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_S, wrap.s());
    glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_T, wrap.t());
    filter.apply(GL_TEXTURE_2D_ARRAY);

    glTexImage3D(GL_TEXTURE_2D_ARRAY, 0, format.internal(), x, y, layers, 0, format.type(), format.data(), (ByteBuffer) null);

    this.unbind();
  }
}