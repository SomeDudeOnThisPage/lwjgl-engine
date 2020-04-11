package engine.core.gfx.texture;

import static org.lwjgl.opengl.GL11C.*;

public class TextureFilterBilinear implements ITextureFilter
{
  @Override
  public void apply(int target)
  {
    glTexParameteri(target, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(target, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
  }
}