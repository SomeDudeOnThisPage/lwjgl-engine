package engine.entity.component;

import engine.entity.EditorField;
import engine.render.Viewport;
import org.joml.Matrix4f;

public class Camera3D extends ProjectionComponent
{
  @EditorField public float fov;
  @EditorField public float aspect;
  @EditorField public float near;
  @EditorField public float far;

  private final Matrix4f projection;

  public Matrix4f construct(Viewport viewport)
  {
    this.aspect = viewport.size().x / (float) viewport.size().y;
    return this.projection.identity().perspective(this.fov, this.aspect, this.near, this.far);
  }

  public Camera3D()
  {
    this(1.04f, 0.1f, 1000.0f);
  }

  public Camera3D(float fov, float near, float far)
  {
    this.fov = fov;
    this.aspect = 0.0f;
    this.near = near;
    this.far = far;
    this.projection = new Matrix4f();
  }
}