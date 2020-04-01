package engine.core.entity.component;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class TransformComponent extends EntityComponent
{
  public Vector3f position;
  public Quaternionf rotation;
  public Vector3f euler;
  public float scale;

  public Matrix4f test = new Matrix4f();

  private Matrix4f m_Construct;

  public Matrix4f construct()
  {
    return this.m_Construct.identity().translate(this.position).rotate(this.rotation).scale(this.scale);
  }

  public TransformComponent(TransformComponent parent, Vector3f position, Quaternionf rotation, float scale)
  {
    this.position = parent.position;
    this.rotation = parent.rotation;
    this.euler = parent.euler;
    this.scale = parent.scale;
    this.m_Construct = parent.m_Construct;

    this.position.add(position);
    this.rotation.mul(rotation);
    this.scale *= scale;
  }

  public TransformComponent(Vector3f position, Quaternionf rotation, float scale)
  {
    this.position = position;
    this.rotation = rotation;
    this.euler = new Vector3f();

    this.rotation.getEulerAnglesXYZ(this.euler);
    this.scale = scale;

    this.m_Construct = new Matrix4f().identity();
  }

  public TransformComponent()
  {
    this(new Vector3f(), new Quaternionf(), 1.0f);
  }
}
