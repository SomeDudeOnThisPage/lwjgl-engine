package engine.core.entity.component.lighting;

import engine.core.entity.component.EntityComponent;
import org.joml.Vector3f;

public class PointLightSourceComponent extends EntityComponent
{
  public Vector3f color;
  public Vector3f clq;
  // public Matrix4f radius; // sphere model matrix for deferred shading

  public PointLightSourceComponent(Vector3f color, Vector3f clq)
  {
    this.color = color;
    this.clq = clq;
  }
}