package engine.core.entity.system;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import engine.core.entity.Entity;
import engine.core.entity.component.EntityComponent;
import engine.core.entity.component.TransformComponent;
import engine.core.entity.component.physics.CollisionShapeComponent;
import engine.core.scene.Scene;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;

public class PhysicsSystem extends UpdateSystem
{
  @Override
  public <T extends EntityComponent> Class<T>[] components()
  {
    return new Class[]
    {
      CollisionShapeComponent.class,
      TransformComponent.class
    };
  }

  @Override
  public void update(Scene scene, ArrayList<Entity> entities)
  {
    for (Entity entity : entities)
    {
      /*Transform transform = entity.get(CollisionShapeComponent.class).body.getMotionState().getWorldTransform(new Transform());

      entity.get(TransformComponent.class).position = Utils.convert(transform.origin);
      entity.get(TransformComponent.class).rotation = Utils.convert(transform.getRotation(new Quat4f()));*/

      Matrix4 transform = new Matrix4();
      entity.get(CollisionShapeComponent.class).body.getMotionState().getWorldTransform(transform);
      Vector3 position = new Vector3();
      transform.getTranslation(position);
      Quaternion rotation = new Quaternion();
      transform.getRotation(rotation);

      entity.get(TransformComponent.class).position = new Vector3f(
        position.x,
        position.y,
        position.z
      );

      entity.get(TransformComponent.class).rotation = new Quaternionf(
        rotation.x,
        rotation.y,
        rotation.z,
        rotation.w
      );
    }
  }

  @Override
  public void added(Entity entity) {}
}
