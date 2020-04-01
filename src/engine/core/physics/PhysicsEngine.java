package engine.core.physics;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.*;
import com.badlogic.gdx.physics.bullet.linearmath.*;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import engine.core.entity.Entity;
import engine.core.entity.component.physics.CollisionShapeComponent;

import javax.vecmath.Vector3f;
import java.util.concurrent.*;

public class PhysicsEngine
{
  public Runnable thread;
  private double dt;
  private ExecutorService executor;

  private Future barrier;

  /*private DynamicsWorld world;
  private CollisionConfiguration config;
  private CollisionDispatcher dispatcher;
  private BroadphaseInterface broadphase;
  private ConstraintSolver solver;*/

  private btDynamicsWorld world;
  private btCollisionConfiguration config;
  private btCollisionDispatcher dispatcher;
  private btBroadphaseInterface broadphase;
  private btConstraintSolver solver;

  /**
   * Updates the physics systems' internal dynamics world by a given time step.
   * @param dt Delta time since last update.
   */
  public void update(double dt)
  {
    this.dt = dt;
    //this.barrier = this.executor.submit(this.thread);
    //this.world.stepSimulation((float) dt);
  }

  public btDynamicsWorld world()
  {
    return this.world;
  }

  /**
   * Adds a new Entity to the systems' dynamics world.
   * The entity must contain a {@link CollisionShapeComponent}.
   * @param entity The entity to addEntity to the world.
   */
  public void addEntity(Entity entity)
  {
    if (!entity.has(CollisionShapeComponent.class))
    {
      throw new Error("failed to addEntity entity " + entity.id() + "to dynamics world - entity has no CollisionShapeComponent attached");
    }

    this.world.addRigidBody(entity.get(CollisionShapeComponent.class).body);
  }

  public void addEntity(CollisionShapeComponent component)
  {
    this.world.addRigidBody(component.body);
  }

  public void join()
  {
    this.world.stepSimulation((float) this.dt);

    /*try
    {
      if (this.barrier != null && !this.barrier.isDone())
      {
        this.barrier.get();
      }
    }
    catch (InterruptedException | ExecutionException e)
    {
      e.printStackTrace();
    }*/
  }

  public void terminate()
  {
    try
    {
      this.executor.shutdown();
      this.executor.awaitTermination(1, TimeUnit.SECONDS);
    }
    catch (InterruptedException e)
    {
      e.printStackTrace();
    }
    finally
    {
      this.executor.shutdownNow();
    }
  }

  /**
   * The PhysicsEngine presents a premade wrapper for the jBullet physics engine.
   */
  public PhysicsEngine()
  {
    /*this.config = new DefaultCollisionConfiguration();
    this.dispatcher = new CollisionDispatcher(this.config);
    this.broadphase = new DbvtBroadphase();
    this.solver = new SequentialImpulseConstraintSolver();

    this.world = new DiscreteDynamicsWorld(this.dispatcher, this.broadphase, this.solver, this.config);
    this.world.setGravity(new Vector3f(0.0f, -9.8f, 0.0f));

    this.thread = () -> this.world.stepSimulation((float) this.dt);*/

    this.dt = 1;
    this.executor = Executors.newSingleThreadExecutor();

    this.config = new btDefaultCollisionConfiguration();
    this.dispatcher = new btCollisionDispatcher(this.config);
    this.broadphase = new btDbvtBroadphase();
    this.solver = new btSequentialImpulseConstraintSolver();

    this.world = new btDiscreteDynamicsWorld(this.dispatcher, this.broadphase, this.solver, this.config);
    this.world.setGravity(new Vector3(0.0f, -9.8f, 0.0f));

    /*btCollisionShape shape = new btBoxShape(new Vector3(1.0f, 1.0f, 1.0f));
    btMotionState ms = new btDefaultMotionState();
    ms.setWorldTransform(new Matrix4().setToWorld(new Vector3(0.0f, 2.0f, 5.0f), new Vector3(0.0f, 1.0f, 0.0f), new Vector3(0.0f, 1.0f, 0.0f)));
    Vector3 iner = new Vector3();
    shape.calculateLocalInertia(1.0f, iner);

    btRigidBody.btRigidBodyConstructionInfo data = new btRigidBody.btRigidBodyConstructionInfo(
      1.0f, ms, shape, iner
    );

    w.addRigidBody(
      new btRigidBody(data)
    );*/
  }
}