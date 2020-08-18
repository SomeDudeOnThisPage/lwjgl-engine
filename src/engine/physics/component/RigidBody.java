package engine.physics.component;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import engine.asset.AssetManager;
import engine.entity.EditorField;
import engine.entity.Entity;
import engine.entity.EntityComponent;
import engine.entity.EntityComponentSystem;
import engine.entity.component.Transform;
import engine.physics.CollisionShape;
import engine.scene.SceneGraph;
import engine.util.MathUtil;

public class RigidBody extends EntityComponent
{
  private final btRigidBody body;
  private final btCollisionShape shape;
  private final btMotionState state;

  private Matrix4 world;

  @EditorField
  public float mass;

  private final btRigidBody.btRigidBodyConstructionInfo info;
  private final Vector3 inertia;

  public final btRigidBody internal()
  {
    return this.body;
  }

  @Override
  public void onComponentDestroyed()
  {
    this.body.dispose();
    this.shape.dispose();
    this.state.dispose();
  }

  public RigidBody(btCollisionShape shape, float mass, Entity entity)
  {
    this.mass = mass;
    this.shape = shape;

    this.state = new btDefaultMotionState();
    this.inertia = new Vector3();
    this.shape.calculateLocalInertia(mass, this.inertia);

    this.info = new btRigidBody.btRigidBodyConstructionInfo(this.mass, this.state, this.shape, this.inertia);

    this.world = new Matrix4()
      .translate(
        entity.getComponent(Transform.class).position.x,
        entity.getComponent(Transform.class).position.y,
        entity.getComponent(Transform.class).position.z
      ).rotate(
        entity.getComponent(Transform.class).rotation.x,
        entity.getComponent(Transform.class).rotation.y,
        entity.getComponent(Transform.class).rotation.z,
        entity.getComponent(Transform.class).rotation.w
      ).scale(
        entity.getComponent(Transform.class).scale.x,
        entity.getComponent(Transform.class).scale.y,
        entity.getComponent(Transform.class).scale.z
      );

    this.body = new btRigidBody(this.info);
    this.body.setWorldTransform(this.world);
    this.body.getMotionState().setWorldTransform(this.world);


    //this.body.getMotionState().setWorldTransform(this.world);
    //this.body.setWorldTransform(this.world);
  }

  public RigidBody(String body, float mass, Entity entity)
  {
    this(AssetManager.getInstance().request(body, CollisionShape.class).internal(), mass, entity);
  }
}
