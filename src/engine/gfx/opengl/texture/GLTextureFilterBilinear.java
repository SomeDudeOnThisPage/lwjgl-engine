package engine.gfx.opengl.texture;

import static org.lwjgl.opengl.GL45C.*;

public class GLTextureFilterBilinear extends GLTextureFilter
{
  @Override
  void apply(int target)
  {
    glTexParameteri(target, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(target, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
  }
}
