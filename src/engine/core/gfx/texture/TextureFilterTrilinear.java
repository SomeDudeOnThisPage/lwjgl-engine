package engine.core.gfx.texture;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL14C.GL_TEXTURE_LOD_BIAS;
import static org.lwjgl.opengl.GL30C.glGenerateMipmap;

public class TextureFilterTrilinear implements ITextureFilter
{
  private float bias;

  @Override
  public void apply(int target)
  {
    glGenerateMipmap(target);
    glTexParameteri(target, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
    glTexParameteri(target, GL_TEXTURE_MAG_FILTER, GL_LINEAR_MIPMAP_LINEAR);
    glTexParameterf(target, GL_TEXTURE_LOD_BIAS, this.bias);
  }

  public TextureFilterTrilinear()
  {
    this.bias = -0.1f;
  }

  public TextureFilterTrilinear(float bias)
  {
    this.bias = bias;
  }
}