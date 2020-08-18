package engine.scene;

import engine.entity.Entity;
import engine.entity.EntityCollection;
import engine.entity.EntityComponentSystem;
import engine.physics.BulletPhysics;
import engine.render.Pipeline;
import engine.render.Viewport;
import engine.ui.DebugGUI;
import org.joml.Vector3f;

import java.util.Collection;
import java.util.UUID;

public abstract class Scene
{
  protected EntityComponentSystem ecs = new EntityComponentSystem();

  protected BulletPhysics.World physics = new BulletPhysics.World(this, new Vector3f(0.0f, -9.8f, 0.0f));

  protected Entity root = new Entity("world").flag(Entity.Flags.NO_REMOVE).flag(Entity.Flags.NO_PARENT_CHANGE);

  protected Pipeline renderer;

  protected Viewport viewport;

  public final Entity root()
  {
    return this.root;
  }

  public final EntityComponentSystem ecs()
  {
    return this.ecs;
  }

  private float dt;

  public final BulletPhysics.World physics()
  {
    if (this.physics == null)
    {
      throw new UnsupportedOperationException("attempted to retrieve physics world of scene '" + this + "', without" +
        "the physics world being set");
    }
    return this.physics;
  }

  public final Pipeline renderer()
  {
    return this.renderer;
  }

  public final Viewport viewport()
  {
    return this.viewport;
  }

  public final float dt()
  {
    return this.dt;
  }

  /**
   * Called once after initialization of this {@link Scene}
   */
  public abstract void onInit();

  /**
   * Called every time this {@link Scene} is entered (meaning it is swapped to as the main scene).
   */
  public abstract void onEnter();

  /**
   * Called every time this {@link Scene} is exited (meaning it is swapped from being the main scene).
   */
  public abstract void onExit();

  /**
   * Called once before the destruction of this {@link Scene}.
   */
  public abstract void onDispose();

  /**
   * Called every update cycle <b>after</b> update of the physics engine / ECS.
   * @return The {@link Scene} that the SceneManager should set next. Return {@code this} to keep the current scene.
   */
  public abstract Scene onTick(float dt);

  /**
   * Shortcut to {@link EntityComponentSystem#addEntity(Entity)}.
   * @param entity The {@link Entity} to be added.
   */
  public final void addEntity(Entity entity)
  {
    this.ecs.addEntity(entity);
    for (Entity child : entity.children())
    {
      if (!this.ecs.hasEntity(child.toString()))
      {
        this.addEntity(child);
      }
    }

    if (entity.getParent() == null)
    {
      this.root.addChild(entity);
    }
  }

  /**
   * Shortcut to {@link EntityComponentSystem#addEntity(Entity)}.
   * @param entity The {@link Entity} to be added.
   */
  public final void addEntity(Entity entity, Entity parent)
  {
    this.ecs.addEntity(entity);
    parent.addChild(entity);
  }

  /**
   * Shortcut to {@link EntityComponentSystem#addCollection(EntityCollection)}.
   * @param collection The {@link EntityCollection} to be added.
   */
  public final void addCollection(EntityCollection collection)
  {
    this.ecs.addCollection(collection);
  }

  /**
   * Shortcut to {@link EntityComponentSystem#get(UUID)}.
   * @param id The {@link UUID} of the {@link Entity} handle to be retrieved.
   * @return An {@link Entity} handle.
   */
  public final Entity getEntity(UUID id)
  {
    return this.ecs.get(id);
  }

  /**
   * Shortcut to {@link EntityComponentSystem#get(String)}.
   * @param id The {@link String} identifier of the {@link Entity} handle to be retrieved.
   * @return An {@link Entity} handle.
   */
  public final Entity getEntity(String id)
  {
    return this.ecs.get(id);
  }

  public final boolean hasEntity(String id)
  {
    return this.ecs.hasEntity(id);
  }

  public final int entityCount()
  {
    return this.ecs.amount();
  }

  public final Collection<Entity> entities()
  {
    return this.ecs.entities();
  }

  /**
   * Ticks this {@link Scene}s internal systems like the {@link EntityComponentSystem}.
   */
  public final Scene tick(float dt)
  {
    this.dt = dt;

    // do physics calculations
    this.physics.update(dt);

    // sync physics states of physics engine with internal transforms
    this.physics.preSceneUpdate(this);

    // update the scene systems / entities
    this.ecs.update(this);

    // sync states of internal transforms with physics engine
    this.physics.postSceneUpdate(this);

    // call custom onTick
    return this.onTick(dt);
  }

  public final void render()
  {
    if (this.renderer != null && this.viewport != null)
    {
      this.renderer.render(this, this.viewport);
      return;
    }
    throw new RuntimeException("attempted to render scene '" + this.getClass().getSimpleName() + "' without " +
      "instantiated rendering pipeline or viewport");
  }
}
