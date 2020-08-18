package engine.gfx.opengl.texture;

public class GLTextureWrap
{
  private final int s;
  private final int t;

  public int s()
  {
    return this.s;
  }

  public int t()
  {
    return this.t;
  }

  public GLTextureWrap(int both)
  {
    this.s = both;
    this.t = both;
  }

  public GLTextureWrap(int s, int t)
  {
    this.s = s;
    this.t = t;
  }
}
