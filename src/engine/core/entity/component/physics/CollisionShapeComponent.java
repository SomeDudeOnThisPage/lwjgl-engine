package engine.core.entity.component.physics;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btStaticPlaneShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import com.badlogic.gdx.physics.bullet.linearmath.btTransform;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.StaticPlaneShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.Point2PointConstraint;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;
import engine.Engine;
import engine.core.entity.Entity;
import engine.core.entity.component.EntityComponent;
import engine.core.entity.component.TransformComponent;
import engine.util.Utils;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

public class CollisionShapeComponent extends EntityComponent
{
  /*public RigidBody body;
  public CollisionShape shape;
  public MotionState state;
  public float mass;*/

  public btRigidBody body;
  public btCollisionShape shape;
  public btMotionState state;
  public float mass;

  public CollisionShapeComponent(Entity entity, btCollisionShape shape)
  {
    this(entity, 0.0f, shape);
  }

  public CollisionShapeComponent(Entity entity, float mass, btCollisionShape shape)
  {
    this.mass = mass;
    this.shape = shape;

    float scale = entity.get(TransformComponent.class).scale;

    if (shape instanceof btStaticPlaneShape)
    {
      scale = 1.0f;
    }

    this.state = new btDefaultMotionState();

    Vector3 inertia = new Vector3();
    shape.calculateLocalInertia(mass, inertia);

    btRigidBody.btRigidBodyConstructionInfo info = new btRigidBody.btRigidBodyConstructionInfo(this.mass, this.state, this.shape, inertia);

    Matrix4 initial = new Matrix4()
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

    this.body = new btRigidBody(info);
    this.body.setWorldTransform(initial);
    this.body.getMotionState().setWorldTransform(initial);

    Engine.scene_manager.getScene().physics().addEntity(this);
  }
}