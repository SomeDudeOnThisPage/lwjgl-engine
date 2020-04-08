package engine.core.physics;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.*;
import engine.core.entity.Entity;
import engine.core.entity.component.physics.CollisionShapeComponent;

import java.util.concurrent.*;

public class PhysicsEngine
{
  public Runnable thread;
  private double dt;
  private ExecutorService executor;

  private Future barrier;

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
    this.dt = dt / 1000.0f;
    //this.world.stepSimulation((float) this.dt, 5);
    this.barrier = this.executor.submit(this.thread);
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
    try
    {
      if (this.barrier != null && !this.barrier.isDone())
      {
        this.barrier.get();
      }
    }
    catch (InterruptedException | ExecutionException e)
    {
      e.printStackTrace();
    }
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
    this.thread = () -> this.world.stepSimulation((float) this.dt, 10);

    this.dt = 1;
    this.executor = Executors.newSingleThreadExecutor();

    this.config = new btDefaultCollisionConfiguration();
    this.dispatcher = new btCollisionDispatcher(this.config);
    this.broadphase = new btDbvtBroadphase();
    this.solver = new btSequentialImpulseConstraintSolver();

    this.world = new btDiscreteDynamicsWorld(this.dispatcher, this.broadphase, this.solver, this.config);
    this.world.setGravity(new Vector3(0.0f, -9.8f, 0.0f));
  }
}