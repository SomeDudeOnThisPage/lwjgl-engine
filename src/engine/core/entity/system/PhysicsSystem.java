package engine.core.entity.system;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import engine.Engine;
import engine.core.entity.Entity;
import engine.core.entity.component.EntityComponent;
import engine.core.entity.component.TransformComponent;
import engine.core.entity.component.physics.CollisionShapeComponent;
import engine.core.scene.Scene;

import java.util.ArrayList;

/**
 * This system synchronizes the internal transforms of the {@link engine.core.physics.PhysicsEngine} and the internal
 * {@link Engine}s' {@link Entity} instance transforms, stored in their respective {@link TransformComponent}s.
 */
public class PhysicsSystem extends UpdateSystem
{
  /** Used to reduce memory allocation per frame. */
  private Matrix4 transform;

  /** Used to reduce memory allocation per frame. */
  private Vector3 position;

  /** Used to reduce memory allocation per frame. */
  private Quaternion rotation;

  @Override
  public <T extends EntityComponent> Class<T>[] components()
  {
    // all game objects that contain their own transform, and are participating in the physics world
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
      entity.get(CollisionShapeComponent.class).body.getMotionState().getWorldTransform(this.transform);

      this.transform.getTranslation(this.position);

      this.rotation = new Quaternion();
      this.transform.getRotation(this.rotation);

      // synchronize internal bullet transform and our internal engine transform
      entity.get(TransformComponent.class).position.set(
        this.position.x,
        this.position.y,
        this.position.z);
      entity.get(TransformComponent.class).rotation.set(
        this.rotation.x,
        this.rotation.y,
        this.rotation.z,
        this.rotation.w
      );
    }
  }

  @Override
  public void added(Entity entity) {}

  public PhysicsSystem()
  {
    this.transform = new Matrix4();
    this.position = new Vector3();
    this.rotation = new Quaternion();
  }
}
