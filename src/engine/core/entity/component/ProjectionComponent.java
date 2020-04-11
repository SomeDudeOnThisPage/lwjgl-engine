package engine.core.entity.component;

import org.joml.Matrix4f;

public class ProjectionComponent extends EntityComponent
{
  public Matrix4f projection;

  public ProjectionComponent(float fov, float aspect, float near, float far)
  {
    if (fov > Math.PI)
    {
      fov *= (Math.PI / 180.0f);
    }

    this.projection = new Matrix4f()/*.ortho(-50, 50, -50, 50, 0.1f, 1000.0f);*/.perspective(fov, aspect, near, far);
  }
}