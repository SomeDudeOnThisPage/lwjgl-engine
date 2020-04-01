package engine.core.entity.component.lighting;

import engine.core.entity.component.EntityComponent;
import org.joml.Vector3f;

public class DirectionalLightSourceComponent extends EntityComponent
{
  public Vector3f direction;
  public Vector3f color;
  public Vector3f clq;

  public DirectionalLightSourceComponent(Vector3f direction, Vector3f color, Vector3f clq)
  {
    this.direction = direction.normalize();
    this.color = color;
    this.clq = clq;
  }
}