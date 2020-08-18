package engine.entity.component.light;

import engine.entity.EditorComponent;
import engine.entity.EditorField;
import engine.entity.EntityComponent;
import org.joml.Vector3f;

@EditorComponent
public class PointLight extends EntityComponent
{
  @EditorField
  public Vector3f color;

  @EditorField
  public Vector3f clq;

  public PointLight(Vector3f color, Vector3f clq)
  {
    this.color = color;
    this.clq = clq;
  }
}
