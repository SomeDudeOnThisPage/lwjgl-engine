package engine.core.gfx.texture;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL11C.GL_NEAREST;

public class TextureFilterLinear implements ITextureFilter
{
  @Override
  public void apply(int target)
  {
    glTexParameteri(target, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    glTexParameteri(target, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
  }
}