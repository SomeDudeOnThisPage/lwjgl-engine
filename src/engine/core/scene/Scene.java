package engine.core.scene;

import engine.core.entity.Entity;

import engine.core.entity.EntityComponentSystem;
import engine.core.entity.component.TransformComponent;
import engine.core.entity.system.IRenderSystem;
import engine.core.entity.system.UpdateSystem;
import engine.core.rendering.Renderer;
import engine.core.physics.PhysicsEngine;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * A {@link Scene} is essentially a 'World', as it may be called in other engines.
 * Scenes manage a list of {@link Entity} instances, and the {@link UpdateSystem}s interacting with them.
 */
public abstract class Scene
{
  /**
   * The {@link EntityComponentSystem} of this {@link Scene}.
   * <p>Each {@link Scene} has its' own {@link EntityComponentSystem}, meaning it has its own {@link Entity} instances,
   * {@link UpdateSystem}s and {@link engine.core.entity.component.EntityComponent} mappings.</p>
   */
  private EntityComponentSystem ecs;

  /**
   * The {@link PhysicsEngine} this scene uses. By default this will be set to use the JBullet physics engine.
   */
  private PhysicsEngine physics;

  protected Entity world = new Entity("world");

  protected Player player;

  /** Has this scene finished initializing? */
  private boolean initialized = false;

  /**
   * The delta-time since the last update loop in {@link TimeUnit#MILLISECONDS}.
   */
  private double deltaTime;

  /**
   * Renderer of this scene. Must be set upon initializing a scene.
   * <p>The renderer is responsible for running the required {@link IRenderSystem}s in a
   * specific order.</p>
   * <p>See {@link engine.core.rendering.DeferredRenderer} for a more specific example.</p>
   */
  protected Renderer renderer;

  /**
   * Returns whether the {@link Scene} has finished initializing or not.
   * This should only be called internally by the {@link SceneManager}.
   * @return A boolean value describing if the {@link Scene} has finished internal initialization.
   */
  public final boolean initialized()
  {
    return this.initialized;
  }

  public final Player getPlayer()
  {
    return this.player;
  }

  /**
   * Internally initializes the scene.
   * This should only be called by the {@link SceneManager}.
   */
  public final void initialize()
  {
    this.ecs = new EntityComponentSystem(this);
    this.physics = new PhysicsEngine();
    this.world.add(new TransformComponent());
    this.onInit();
    this.initialized = true;
  }

  /**
   * Internally terminates the scene.
   * This should only be called by the {@link SceneManager}.
   */
  public final void terminate()
  {
    this.physics.terminate();
  }

  /**
   * Updates all systems contained in this scene.
   * @param dt delta-time (since last update)
   */
  public void update(double dt)
  {
    this.deltaTime = dt;
    if (!this.initialized) { return; }

    // update ECS on the main thread
    this.ecs.update();
  }

  public final void integrate(double dt)
  {
    // kick off physics computations (on a separate thread)
    this.physics.update(dt);
    // wait for the physics computations to conclude before we continue with the rendering stage.
    this.physics.join();
  }

  public EntityComponentSystem ecs()
  {
    return this.ecs;
  }

  public PhysicsEngine physics()
  {
    return this.physics;
  }

  /**
   * Signals the scenes' renderer to render the scene.
   */
  public void render()
  {
    if (this.renderer == null)
    {
      System.err.println("tried to render scene without attached renderer");
      System.exit(1);
    }

    if (!this.initialized) { return; }

    this.renderer.setCamera(this.player.getCamera());
    this.renderer.render(this);
  }

  /**
   * Adds an entity to the scene, without mapping its' components to systems.
   * This method is called internally, adding an entity to the scene is done automatically when
   * adding a component to an entity.
   * @param object entity
   */
  public final void add(Entity object)
  {
    this.ecs.addEntity(object);
  }

  /**
   * Adds an {@link UpdateSystem} to the {@link Scene}.
   * <p>This system will be run each tick, and the systems' {@link UpdateSystem#update(Scene, ArrayList)} method will be
   * provided with a list of entities containing the required components set in the systems' {@link UpdateSystem#components()}
   * method.</p>
   * <p>Should the {@link UpdateSystem} implement the {@link engine.core.entity.system.IRenderSystem} interface, the
   * system will also be run during the corresponding {@link engine.core.rendering.RenderStage} set in the
   * {@link engine.core.entity.system.IRenderSystem}s' {@link IRenderSystem#priority()} method, provided the
   * scenes' {@link Renderer} supports the selected {@link engine.core.rendering.RenderStage}.
   * during the next frame / update loop.</p>
   * @param system The {@link UpdateSystem} to addEntity to this {@link Scene}.
   */
  public final void add(UpdateSystem system)
  {
    this.ecs.addSystem(system);
  }

  /**
   * Returns an {@link Entity} queried by its' {@link UUID}.
   * @param id The {@link UUID} of the {@link Entity}.
   * @return The {@link Entity} with the corresponding {@link UUID}, or {@code null} if no {@link Entity} was found.
   */
  public final Entity get(UUID id) { return this.ecs.get(id); }

  /**
   * Returns an {@link Entity} queried by its' {@link String} name.
   * @param name The {@link String} name of the {@link Entity}.
   * @return The {@link Entity} with the corresponding name, or {@code null} if no {@link Entity} was found.
   */
  public final Entity get(String name) { return this.ecs.get(name); }

  /**
   * Returns this {@link Scene}s' assigned {@link Renderer}.
   * @return The {@link Renderer} of this {@link Scene}.
   */
  public final Renderer getRenderer()
  {
    return this.renderer;
  }

  /**
   * Gets this {@link Scene}s' elapsed time since the beginning of the last update loop in milliseconds.
   * @return The delta-time between this, and the last update loop in milliseconds.
   */
  public final double getDeltaTime()
  {
    return this.deltaTime;
  }

  /**
   * Hook called on creation of the scene.
   */
  public /* abstract */ void onInit() {}

  /**
   * Hook called whenever the scene is entered.
   */
  public /* abstract */ void onEnter() {}

  /**
   * Hook called when the scene is exited.
   */
  public /* abstract */ void onExit() {}
}