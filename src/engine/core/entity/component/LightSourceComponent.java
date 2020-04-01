package engine.core.entity.component;

import org.joml.Vector3f;

public class LightSourceComponent extends EntityComponent
{
  public Vector3f dir;
  public Vector3f ambient;
  public Vector3f clq;
  public Vector3f color;

  public LightSourceComponent(Vector3f color, Vector3f ambient, Vector3f clq)
  {
    this.ambient = ambient;
    this.color = color;
    this.clq = clq;
  }
}