package engine.core.scene;

import engine.core.entity.Entity;
import engine.core.entity.component.TransformComponent;
import engine.core.gui.GUIObject;
import org.joml.Matrix4f;
import org.joml.Vector2f;

public class SceneGraph
{
  public static Vector2f constructTransform(GUIObject object)
  {
    if (object.getParent() == null)
    {
      return object.getPosition();
    }

    return constructTransform(object.getParent()).mul(object.getPosition());
  }

  public static Matrix4f constructTransform(Entity entity)
  {
    Matrix4f transform = new Matrix4f().identity();

    // if the entity has no transform component, return an identity matrix and thus stop the recursion
    if (!entity.has(TransformComponent.class))
    {
      return transform;
    }

    // if the parent entity does not exist or is the world (the root of our scene graph), return the entities transform
    if (entity.getParent() == null || entity.name() != null && entity.name().equals("world"))
    {
      return entity.get(TransformComponent.class).construct();
    }

    return constructTransform(entity.getParent()).mul(entity.get(TransformComponent.class).construct());
  }
}
