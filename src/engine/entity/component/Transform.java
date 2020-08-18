package engine.entity.component;

import engine.entity.EditorComponent;
import engine.entity.EditorConstructor;
import engine.entity.EditorField;
import engine.entity.EntityComponent;
import org.joml.*;

@EditorComponent
public class Transform extends EntityComponent
{
  @EditorField
  public Vector3f position;

  @EditorField
  public Quaternionf rotation;

  @EditorField
  public Vector3f scale;

  private final Matrix4f transform;

  public Matrix4f construct()
  {
    return this.transform.identity()
      .rotate(   this.rotation)          // for some reason everything goes to shit when I translate first
      .translate(this.position)
      .scale(    this.scale   );
  }

  public Transform(Vector3fc position, Quaternionfc rotation, Vector3fc scale)
  {
    this.transform = new Matrix4f().identity();

    this.position = new Vector3f().set(position);
    this.rotation = new Quaternionf().set(rotation);
    this.scale = new Vector3f().set(scale);
  }

  @EditorConstructor
  public Transform()
  {
    this.transform = new Matrix4f().identity();

    this.position = new Vector3f(0.0f);
    this.rotation = new Quaternionf().identity();
    this.scale = new Vector3f(1.0f);
  }
}