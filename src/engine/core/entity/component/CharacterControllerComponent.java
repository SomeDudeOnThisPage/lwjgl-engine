package engine.core.entity.component;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCapsuleShape;
import com.badlogic.gdx.physics.bullet.collision.btConvexShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.bulletphysics.collision.dispatch.CollisionFlags;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.GhostPairCallback;
import com.bulletphysics.collision.dispatch.PairCachingGhostObject;
import com.bulletphysics.collision.shapes.*;
import com.bulletphysics.dynamics.RigidBody;
import engine.core.Input;
import engine.core.entity.component.physics.CollisionShapeComponent;
import engine.core.scene.Player;
import engine.core.scene.Scene;
import engine.core.scene.SceneGraph;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class CharacterControllerComponent extends ScriptComponent
{
  private static final float SPEED_MULTIPLIER = 5f;

  private btConvexShape shape;

  public btRigidBody body;

  @Override
  public void init(Scene scene)
  {
    this.shape = new btCapsuleShape(0.8f, 2.0f);
    this.shape.setMargin(0.001f);

    this.entity.add(new CollisionShapeComponent(this.entity, 5.0f, this.shape));
    this.body = this.entity.get(CollisionShapeComponent.class).body;
    this.body.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
    this.body.setCollisionFlags(CollisionFlags.CHARACTER_OBJECT);

    //this.body.setMotionState(new MyMotionState(this.entity));
    this.body.setAngularFactor(0.0f);
    //this.body.setInvInertiaDiagLocal(new javax.vecmath.Vector3f(0.0f, 0.0f, 0.0f));
    this.body.setInvInertiaDiagLocal(new Vector3(0.0f, 0.0f, 0.0f));
    this.body.updateInertiaTensor();
    this.body.setGravity(new Vector3(0.0f, -35.6f, 0.0f));
    this.body.setDamping(0.5f, 0.0f);
    this.shape.setMargin(0.001f);

   // scene.physics().world().addCollisionObject();
  }

  @Override
  public void update(Scene scene)
  {
    Quaternionf rotation = new Quaternionf(SceneGraph.constructTransform(((Player) this.entity).getCamera()).getRotation(new AxisAngle4f()));

    Vector3f direction = new Vector3f(0.0f, 0.0f, 1.0f);
    direction.rotate(rotation).negate();

    Vector3f right = new Vector3f(1.0f, 0.0f, 0.0f);
    right.rotate(rotation).negate();

    Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);

    Vector3f move = new Vector3f();

    if (Input.keyDown(GLFW_KEY_W))
    {
      //move.sub(new Vector3f(0.0f, 0.0f, 5.0f));
      move.add(direction);
    }
    if (Input.keyDown(GLFW_KEY_S))
    {
      //move.add(new Vector3f(0.0f, 0.0f, 5.0f));
      move.sub(direction);
    }
    if (Input.keyDown(GLFW_KEY_A))
    {
      move.add(right);
    }
    if (Input.keyDown(GLFW_KEY_D))
    {
      move.sub(right);
    }

    if (Input.keyDown(GLFW_KEY_SPACE))
    {
      move.y = 1.0f;
      //this.controller.jump();
    }
    else
    {
      move.y = 0.0f;
    }

    //move.normalize();

    move.mul(SPEED_MULTIPLIER);

    this.body.setLinearVelocity(new Vector3(move.x, move.y, move.z));
  }
}
