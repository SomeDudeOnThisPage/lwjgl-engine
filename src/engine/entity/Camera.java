package engine.entity;

import engine.entity.component.ProjectionComponent;
import engine.entity.component.Transform;

public abstract class Camera extends Entity
{
  protected ProjectionComponent projection;
  protected Transform transform;

  public Camera() {}
}