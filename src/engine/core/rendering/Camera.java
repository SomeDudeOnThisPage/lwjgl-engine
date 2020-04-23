package engine.core.rendering;

import engine.Engine;
import engine.core.entity.Entity;
import engine.core.entity.component.ProjectionComponent;
import engine.core.entity.component.TransformComponent;
import engine.core.scene.SceneGraph;
import engine.util.settings.Settings;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public final class Camera extends Entity
{
  private float fov;
  private float ratio;

  private float near;
  private float far;

  public void onScreenSizeChanged(int x, int y)
  {
    this.ratio = (float) x / (float) y;

    this.get(ProjectionComponent.class).projection.identity().perspective(
      this.fov, this.ratio, 0.1f, 1000.0f
    );
  }

  public Matrix4f getProjection()
  {
    return this.get(ProjectionComponent.class).projection;
  }

  public Matrix4f getView()
  {
    return SceneGraph.constructTransform(this);
  }

  public Matrix4f getViewLocal()
  {
    return this.get(TransformComponent.class).construct();
  }

  public Vector3f getPosition()
  {
    return SceneGraph.constructTransform(this).getTranslation(new Vector3f());
  }

  public Camera(float fov, float ratio, float near, float far)
  {
    super("scene-camera");

    this.fov = fov;
    this.ratio = ratio;

    this.near = near;
    this.far = far;

    this.add(new ProjectionComponent(this.fov, this.ratio, this.near, this.far))
      .add(new TransformComponent());
  }

  public Camera()
  {
    this(Settings.getf("FOV"), (float) Engine.window.getWidth() / (float) Engine.window.getHeight(), 0.1f, 1000.0f);
  }
}