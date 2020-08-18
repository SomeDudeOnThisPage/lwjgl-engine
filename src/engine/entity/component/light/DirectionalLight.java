package engine.entity.component.light;

import engine.entity.EditorField;
import engine.entity.EntityComponent;
import org.joml.Vector3f;

public class DirectionalLight extends EntityComponent
{
  @EditorField
  public Vector3f direction;

  @EditorField
  public Vector3f color;

  public DirectionalLight(Vector3f direction, Vector3f color)
  {
    this.color = color;
    this.direction = direction;
  }
}
