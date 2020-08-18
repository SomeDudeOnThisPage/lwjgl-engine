package engine.gfx.opengl.texture;

public class GLTextureFormat
{
  private final int internal;
  private final int format;
  private final int type;

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
    return this.format;
  }

  public int data()
  {
    return this.type;
  }

  public GLTextureFormat(int internal, int format, int type)
  {
    this.internal = internal;
    this.format = format;
    this.type = type;
  }
}