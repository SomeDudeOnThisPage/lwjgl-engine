package engine.core.scene;

import engine.core.entity.Entity;
import engine.core.entity.component.FPCMouseOrientationComponent;
import engine.core.rendering.Camera;

public final class Player extends Entity
{
  private Camera camera;

  public final Camera getCamera()
  {
    return this.camera;
  }

  public Player()
  {
    super("player");
    this.camera = new Camera();
    this.camera.add(new FPCMouseOrientationComponent());
  }
}
