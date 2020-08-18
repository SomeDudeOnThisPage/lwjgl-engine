package engine.physics.system;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import engine.entity.*;
import engine.physics.component.RigidBody;
import engine.entity.component.Transform;
import engine.scene.Scene;
import engine.scene.SceneGraph;
import engine.util.MathUtil;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * This system is responsible for the synchronization of the {@link Entity Entities}' transforms with their
 * internal {@link btMotionState MotionState}s. It also manages the addition / removal of any {@link RigidBody RigidBodies}
 * to / from the internal physics world.
 * <p>
 *   This system will be added to a scene after initialization of a physics context. Do not add / remove this
 *   system from / to a scene manually.
 * </p>
 */
public final class RigidBodyManagementSystem extends EntityCollection implements EntitySystem
{
  private final Scene scene;

  @Override
  public <T extends EntityComponent> Class<T>[] components()
  {
    return new Class[]
      {
        Transform.class,
        RigidBody.class
      };
  }

  @Override
  public EntityComponentSystem.SystemPriority priority()
  {
    return EntityComponentSystem.SystemPriority.CALLBACK_ONLY;
  }

  public void syncWorldToEngine(Scene scene)
  {
    for (Entity entity : this.entities)
    {
      Matrix4f transform = entity.getComponent(Transform.class).construct(); //SceneGraph.transform(entity);
      entity.getComponent(RigidBody.class).internal().getMotionState().setWorldTransform(MathUtil.convert(transform));
    }
  }

  private final Matrix4 transform = new Matrix4();
  private final Vector3 position = new Vector3();
  private final Quaternion rotation = new Quaternion();

  public void syncEngineToWorld(Scene scene)
  {
    for (Entity entity : this.entities)
    {
      entity.getComponent(RigidBody.class).internal().getWorldTransform(transform);

      transform.getTranslation(position);
      transform.getRotation(rotation);

      // synchronize internal bullet transform and our internal engine transform
      entity.getComponent(Transform.class).position.set(
        position.x,
        position.y,
        position.z
      );

      entity.getComponent(Transform.class).rotation.set(
        rotation.x,
        rotation.y,
        rotation.z,
        rotation.w
      );
    }
  }

  @Override
  public void update(Scene scene) {}

  @Override
  public void onEntityAdded(Entity entity)
  {
    /*Matrix4f transform = entity.getComponent(Transform.class).construct(); //SceneGraph.transform(entity);
    Matrix4 t = new Matrix4().idt();

    t.setTranslation(MathUtil.convert(transform.getTranslation(new Vector3f())));

    entity.getComponent(RigidBody.class).internal().setWorldTransform(
      t//MathUtil.convert(transform)
    );
    entity.getComponent(RigidBody.class).internal().getMotionState().setWorldTransform(
      t//MathUtil.convert(transform)
    );*/

    this.scene.physics().getWorld().addRigidBody(
      entity.getComponent(RigidBody.class).internal()
    );
  }

  @Override
  public void onEntityRemoved(Entity entity)
  {
    this.scene.physics().getWorld().removeRigidBody(
      entity.getComponent(RigidBody.class).internal()
    );
  }

  public RigidBodyManagementSystem(Scene scene)
  {
    this.scene = scene;
  }
}
