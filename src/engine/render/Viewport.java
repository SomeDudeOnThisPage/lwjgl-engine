package engine.render;

import org.joml.Vector2i;

public class Viewport
{
  protected final Vector2i position;
  protected final Vector2i size;

  public Vector2i position()
  {
    return this.position;
  }

  public Vector2i size()
  {
    return this.size;
  }

  public Viewport(int x, int y, int sx, int sy)
  {
    this.position = new Vector2i(x, y);
    this.size = new Vector2i(sx, sy);
  }
}
