package engine.render;

import engine.Engine;
import org.joml.Vector2i;

public class MaxViewport extends Viewport
{
  @Override
  public Vector2i size()
  {
    // update size every time it is queried
    Vector2i window = Engine.Display.size();
    this.size.set(window.x - this.position.x, window.y - this.position.y);

    return this.size;
  }

  public MaxViewport(int x, int y)
  {
    super(x, y, 0, 0);
  }
}
