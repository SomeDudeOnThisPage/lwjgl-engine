package engine.core.gfx.texture;

public class TextureWrap
{
  private int s;
  private int t;

  public int s()
  {
    return this.s;
  }

  public int t()
  {
    return this.t;
  }

  public TextureWrap(int both)
  {
    this.s = both;
    this.t = both;
  }

  public TextureWrap(int s, int t)
  {
    this.s = s;
    this.t = t;
  }
}