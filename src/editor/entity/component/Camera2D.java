package editor.entity.component;

import engine.entity.EditorComponent;
import engine.entity.EditorField;
import engine.entity.component.ProjectionComponent;
import engine.render.Viewport;
import org.joml.Matrix4f;

@EditorComponent
public class Camera2D extends ProjectionComponent
{
  public Matrix4f projection;

  @EditorField
  public float scale;

  @Override
  public Matrix4f construct(Viewport viewport)
  {
    float x = viewport.size().x / 2.0f;
    float y = viewport.size().y / 2.0f;
    return this.projection.ortho2D(-x, x, -y, y);
  }

  public Camera2D(float scale)
  {
    this.projection = new Matrix4f().identity();
    this.scale = scale;
  }

  public Camera2D()
  {
    this(1.0f);
  }
}