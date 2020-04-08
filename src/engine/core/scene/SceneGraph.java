package engine.core.scene;

import engine.core.entity.Entity;
import engine.core.entity.component.TransformComponent;
import engine.core.gui.GUIConstraints;
import engine.core.gui.GUIElement;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class SceneGraph
{
  public static Vector4f constructTransform(GUIElement element)
  {
    // x = position x
    // y = position y
    // z = size x
    // w = size y
    Vector4f transform = new Vector4f();

    // if we have no parent, or our position is absolute
    if (element.parent() == null || element.constraints().position() == GUIConstraints.Position.ABSOLUTE)
    {
      transform.x = element.position().x + element.constraints().margin()[0];
      transform.y = element.position().y + element.constraints().margin()[1];
      transform.z = element.size().x;
      transform.w = element.size().y;
      return transform;
    }

    // if we have a parent, construct an additive transform
    return constructTransform(element.parent()).add(
      element.position().x + element.constraints().margin()[0] + element.parent().constraints().margin()[0],
      element.position().y + element.constraints().margin()[1] + element.parent().constraints().margin()[1],
      -element.constraints().margin()[2] - element.constraints().margin()[0],
      -element.constraints().margin()[3] - element.constraints().margin()[1]
    );
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
