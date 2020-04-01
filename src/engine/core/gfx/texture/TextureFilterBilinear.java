package engine.core.gfx.texture;

import static org.lwjgl.opengl.GL11C.*;

public class TextureFilterBilinear implements ITextureFilter
{
  @Override
  public void apply()
  {
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
  }
}