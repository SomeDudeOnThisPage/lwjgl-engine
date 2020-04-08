package engine.core.entity.component;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import engine.core.Input;
import engine.core.entity.component.physics.CollisionShapeComponent;
import engine.core.scene.Player;
import engine.core.scene.Scene;
import engine.core.scene.SceneGraph;
import engine.util.Utils;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class CharacterControllerComponent extends ScriptComponent
{
  private static final float SPEED_MULTIPLIER = 5.0f;
  private static final float PLAYER_HEIGHT = 1.5f;
  private static final float PLAYER_WIDTH = 0.75f;
  private static final float AIR_STRAFE_MODIFIER = 2.0f;
  private static final float JUMP_MODIFIER = 1.0f;

  private boolean grounded;

  private Vector3f oldVelocity;
  private Vector3f slopeNormal = new Vector3f(0.0f);

  private btConvexShape shape;

  public btRigidBody body;

  private ClosestRayResultCallback groundCB;

  /**
   * Performs a ray test to see if the player is in an acceptable margin to the ground to count as "grounded".
   * @param scene Scene with physics world.
   */
  private void raytestGround(Scene scene)
  {
    Vector3 from = Utils.convert(this.entity.get(TransformComponent.class).position);
    from.y -= PLAYER_HEIGHT;
    Vector3 to = Utils.convert(this.entity.get(TransformComponent.class).position);
    to.y -= PLAYER_HEIGHT * 2.0f;

    this.groundCB.setRayFromWorld(from);
    this.groundCB.setRayToWorld(to);

    this.groundCB.setCollisionObject(null);
    this.groundCB.setClosestHitFraction(0.1f);

    //this.groundCB.setCollisionFilterMask(STATIC_FILTER);

    scene.physics().world().rayTest(from, to, this.groundCB);
    if (this.groundCB.hasHit())
    {
      Vector3 r = new Vector3();
      Vector3 n = new Vector3();
      this.groundCB.getHitPointWorld(r);
      this.groundCB.getHitNormalWorld(n);
      Vector3f result = Utils.convert(r);

      Vector3f normal = Utils.convert(n);
      Vector3f normalPlayer = new Vector3f(0.0f, -1.0f, 0.0f);

      this.slopeNormal = normal;

      float degrees = (float) (180.0f - Math.toDegrees(normal.angle(normalPlayer)));

      if (from.y - result.y < (0.1f + degrees))
      {
        this.grounded = true;
      }
    }
    else
    {
      this.grounded = false;
      this.slopeNormal.set(0.0f, 0.0f, 0.0f);
    }
  }

  @Override
  public void onComponentAttached()
  {
    this.oldVelocity = new Vector3f(0.0f, 0.0f, 0.0f);
    this.shape = new btCapsuleShape(PLAYER_WIDTH, PLAYER_HEIGHT);
    this.shape.setMargin(0.001f);

    this.entity.add(new CollisionShapeComponent(this.entity, 15.0f, this.shape));
    this.body = this.entity.get(CollisionShapeComponent.class).body;
    this.body.setActivationState(CollisionConstants.DISABLE_DEACTIVATION);
    this.body.setCollisionFlags(btCollisionObject.CollisionFlags.CF_CHARACTER_OBJECT);

    this.body.setAngularFactor(0.0f);
    this.body.setInvInertiaDiagLocal(new Vector3(0.0f, 0.1f, 0.0f));
    this.body.updateInertiaTensor();
    this.body.setGravity(new Vector3(0.0f, -19.6f, 0.0f));
    this.shape.setMargin(0.001f);

    this.groundCB = new ClosestRayResultCallback(new Vector3(0.0f, 0.0f, 0.0f), new Vector3(0.0f, 1.0f, 0.0f));
  }

  @Override
  public void onComponentRemoved()
  {
    this.shape.dispose();
    this.body.dispose();
    this.groundCB.dispose();
  }

  @Override
  public void update(Scene scene)
  {
    // perform ray test to check if the player is grounded before continuing
    this.raytestGround(scene);

    if (!this.grounded)
    {
      this.body.setInvInertiaDiagLocal(new Vector3(0.9f, 0.9f, 0.9f));
      this.body.updateInertiaTensor();
    }
    else
    {
      this.body.setInvInertiaDiagLocal(new Vector3(0.0f, 0.9f, 0.0f));
      this.body.updateInertiaTensor();
    }

    Quaternionf rotation = new Quaternionf(SceneGraph.constructTransform(((Player) this.entity).getCamera()).getRotation(new AxisAngle4f()));

    Vector3f direction = new Vector3f(0.0f, 0.0f, 1.0f);
    direction.rotate(rotation).negate();

    Vector3f right = new Vector3f(1.0f, 0.0f, 0.0f);
    right.rotate(rotation).negate();

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
      move.y = JUMP_MODIFIER;
      //this.controller.jump();
    }
    else
    {
      move.y = 0.0f;
    }

    move.mul(SPEED_MULTIPLIER /* (float) scene.getDeltaTime()*/);

    if (this.grounded)
    {
      Vector3f newVelocity = this.oldVelocity.sub(this.slopeNormal.mul(this.slopeNormal.dot(this.oldVelocity)));
      this.body.setLinearVelocity(new Vector3(move.x, newVelocity.y + move.y, move.z));
      this.body.setFriction(0.9999f);
    }
    else
    {
      this.body.applyCentralForce(new Vector3(
        move.x * AIR_STRAFE_MODIFIER,
        0.0f,
        move.z * AIR_STRAFE_MODIFIER)
      );
      this.body.setFriction(0.0001f);
    }

    this.oldVelocity = Utils.convert(this.body.getLinearVelocity());
  }
}
