package engine.physics;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.btConstraintSolver;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.badlogic.gdx.utils.GdxNativesLoader;
import engine.Console;
import engine.Engine;
import engine.asset.AssetFamily;
import engine.physics.system.RigidBodyManagementSystem;
import engine.scene.Scene;
import org.joml.Vector3f;

public class BulletPhysics
{
  private static boolean initialized = false;
  private static DebugRenderer debug;

  public static final class World
  {
    private boolean enabled;

    private final btDynamicsWorld world;
    private final btCollisionConfiguration config;
    private final btCollisionDispatcher dispatcher;
    private final btBroadphaseInterface broadphase;
    private final btConstraintSolver solver;

    private final Vector3 gravity;

    private final RigidBodyManagementSystem rbManager;

    public btDynamicsWorld getWorld()
    {
      return this.world;
    }

    public void enabled(boolean state)
    {
      this.enabled = state;
    }

    public void preSceneUpdate(Scene scene)
    {
      // sync physics states
      this.rbManager.syncEngineToWorld(scene);
    }

    public void postSceneUpdate(Scene scene)
    {
      // sync physics states
      // this.rbManager.syncWorldToEngine(scene);
    }

    public void update(float dt)
    {
      if ((Boolean) Console.getConVar("phys_enable").get())
      {
        this.world.stepSimulation(dt);
      }
    }

    /**
     * Set the gravity of this world.
     * @param gravity The gravity vector.
     */
    public void setGravity(Vector3f gravity)
    {
      this.gravity.set(gravity.x, gravity.y, gravity.z);
      this.world.setGravity(this.gravity);
    }

    public World(Scene scene, Vector3f gravity)
    {
      if (!BulletPhysics.initialized)
      {
        throw new UnsupportedOperationException("attempted to create new PhysicsWorld without prior initialization - " +
          "call BulletPhysics.initialize() prior to creating a PhysicsWorld");
      }

      this.config = new btDefaultCollisionConfiguration();
      this.dispatcher = new btCollisionDispatcher(this.config);
      this.broadphase = new btDbvtBroadphase();
      this.solver = new btSequentialImpulseConstraintSolver();

      this.world = new btDiscreteDynamicsWorld(this.dispatcher, this.broadphase, this.solver, this.config);
      this.gravity = new Vector3(gravity.x, gravity.y, gravity.z);
      this.world.setGravity(this.gravity);

      this.world.setDebugDrawer(BulletPhysics.debug);

      this.rbManager = new RigidBodyManagementSystem(scene);

      scene.addCollection(this.rbManager);

      this.enabled = true;
    }
  }

  public static void initialize()
  {
    if (!BulletPhysics.initialized)
    {
      GdxNativesLoader.load();
      Bullet.init();
      BulletPhysics.debug = new DebugRenderer();
      BulletPhysics.initialized = true;
      Engine.Log.info("initialized bullet physics engine version " + Bullet.VERSION);

      // register family and factory
      Engine.AssetManager.registerFamily(new AssetFamily<>(CollisionShape.class));
      Engine.AssetManager.registerFactory(CollisionShape.class, new CollisionShapeFactory());

      // register base physics convars
      Console.addConVar("phys_enable", new Console.ConVar<>(false));
    }
  }
}
