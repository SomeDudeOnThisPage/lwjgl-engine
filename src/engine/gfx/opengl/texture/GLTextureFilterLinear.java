package engine.gfx.opengl.texture;

import static org.lwjgl.opengl.GL45C.*;

public class GLTextureFilterLinear extends GLTextureFilter
{
  @Override
  public void apply(int target)
  {
    glTexParameteri(target, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    glTexParameteri(target, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
  }
}
