package engine.core.entity.component.physics;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btStaticPlaneShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRaycastVehicle;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import engine.Engine;
import engine.core.entity.Entity;
import engine.core.entity.component.EntityComponent;
import engine.core.entity.component.TransformComponent;

import java.util.ArrayList;

public class CollisionShapeComponent extends EntityComponent
{
  private static final ArrayList<CollisionShapeComponent> shapes = new ArrayList<>();

  public btRigidBody body;
  public btCollisionShape shape;
  public btMotionState state;
  public float mass;

  private btRigidBody.btRigidBodyConstructionInfo info;
  private Matrix4 initial;
  private Vector3 inertia;

  @Override
  public void onComponentRemoved()
  {
    this.body.dispose();
    this.shape.dispose();
    this.state.dispose();
  }

  public CollisionShapeComponent(Entity entity, btCollisionShape shape)
  {
    this(entity, 0.0f, shape);
  }

  public CollisionShapeComponent(Entity entity, float mass, btCollisionShape shape)
  {
    CollisionShapeComponent.shapes.add(this);

    this.mass = mass;
    this.shape = shape;

    float scale = entity.get(TransformComponent.class).scale;

    if (this.shape instanceof btStaticPlaneShape)
    {
      scale = 1.0f;
    }

    this.state = new btDefaultMotionState();

    this.inertia = new Vector3();
    this.shape.calculateLocalInertia(mass, this.inertia);

    this.info = new btRigidBody.btRigidBodyConstructionInfo(this.mass, this.state, this.shape, this.inertia);

    this.initial = new Matrix4()
      .translate(
        entity.get(TransformComponent.class).position.x,
        entity.get(TransformComponent.class).position.y,
        entity.get(TransformComponent.class).position.z
      ).rotate(
        entity.get(TransformComponent.class).rotation.x,
        entity.get(TransformComponent.class).rotation.y,
        entity.get(TransformComponent.class).rotation.z,
        entity.get(TransformComponent.class).rotation.w
      ).scale(scale, scale, scale);

    this.body = new btRigidBody(this.info);
    this.body.setWorldTransform(this.initial);
    this.body.getMotionState().setWorldTransform(this.initial);

    Engine.scene_manager.getScene().physics().addEntity(this);
  }
}