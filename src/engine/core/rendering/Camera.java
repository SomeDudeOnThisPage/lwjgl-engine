package engine.core.rendering;

import engine.Engine;
import engine.core.entity.Entity;
import engine.core.entity.component.ProjectionComponent;
import engine.core.entity.component.TransformComponent;

public final class Camera extends Entity
{
  public void onScreenSizeChanged(int x, int y)
  {
    this.get(ProjectionComponent.class).projection.identity().perspective(
      1.04f, (float) x / (float) y, 0.1f, 1000.0f
    );
  }

  public Camera()
  {
    super("scene-camera");
    this.add(new ProjectionComponent(60.0f, (float) Engine.window.getWidth() / (float) Engine.window.getHeight(), 0.1f, 1000.0f))
        .add(new TransformComponent());
  }
}