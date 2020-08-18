package engine.scene;

import engine.entity.Entity;
import engine.entity.component.Transform;
import org.joml.Matrix4f;

public final class SceneGraph
{
  public static Matrix4f transform(Entity entity)
  {
    if (!entity.containsComponent(Transform.class))
    {
      return new Matrix4f().identity();
    }

    if (entity.getParent() == null)
    {
      return entity.getComponent(Transform.class).construct();
    }

    return new Matrix4f(entity.getComponent(Transform.class).construct()).mul(SceneGraph.transform(entity.getParent()));
  }
}
