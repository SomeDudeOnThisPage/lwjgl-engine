package engine.core.gfx.texture;

public class TextureFormat
{
  private int internal;
  private int type;
  private int data;

  public int components()
  {
    return 4;
  }

  public int internal()
  {
    return this.internal;
  }

  public int type()
  {
    return this.type;
  }

  public int data()
  {
    return this.data;
  }

  public TextureFormat(int internal, int type, int data)
  {
    this.internal = internal;
    this.type = type;
    this.data = data;
  }
}