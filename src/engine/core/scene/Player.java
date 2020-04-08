package engine.core.scene;

import engine.core.entity.Entity;
import engine.core.entity.component.FPCMouseOrientationComponent;
import engine.core.entity.component.TransformComponent;
import engine.core.rendering.Camera;
import org.joml.Vector3f;

public final class Player extends Entity
{
  private Camera camera;

  public final Camera getCamera()
  {
    return this.camera;
  }

  public final Vector3f getPosition()
  {
    return this.get(TransformComponent.class).position;
  }

  public Player()
  {
    super("player");
    this.camera = new Camera();
    this.camera.add(new FPCMouseOrientationComponent());

    this.camera.get(TransformComponent.class).position.y = 0.5f;
  }
}
