package engine.core.entity;

import engine.core.entity.component.EntityComponent;
import engine.core.entity.system.IRenderSystem;
import engine.core.entity.system.UpdateSystem;
import engine.core.rendering.RenderStage;
import engine.core.scene.Scene;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class EntityComponentSystem
{
  private Scene scene;

  /** Entity IDs mapped to their handles. */
  private HashMap<UUID, Entity> entities;

  /** Entity identifiers mapped to their handles */
  private HashMap<String, Entity> identifiers;

  /** Active system map. */
  private HashMap<Class, UpdateSystem> systems;

  /** Active renderable system map. */
  private HashMap<RenderStage, HashMap<Class, IRenderSystem>> rendersystems;

  /** Entities mapped to systems. */
  private HashMap<Class, ArrayList<Entity>> sm;

  public <T extends UpdateSystem> ArrayList<Entity> getSystemEntities(Class<T> system)
  {
    return this.sm.get(system);
  }

  /**
   * Maps an entity to a system (if applicable).
   * @param entity entity
   * @param system system
   */
  private void mapEntitySystem(Entity entity, UpdateSystem system)
  {
    if (this.sm.get(system.getClass()) == null)
    {
      java.lang.System.err.println("[INFO] system " + system + " is not mapped in the ecs");
      return;
    }

    if (this.sm.get(system.getClass()).contains(entity))
    {
      java.lang.System.err.println("[INFO] attempted to add duplicate entity to system");
      return;
    }

    if (system.components().length > 0)
    {
      boolean hasRequired = true;
      for (Class listen : system.components())
      {
        if (entity.get(listen) == null)
        {
          hasRequired = false;
          break;
        }
      }

      if (hasRequired)
      {
        java.lang.System.out.println("[INFO] added entity " + entity.id() + " to system " + system);
        this.sm.get(system.getClass()).add(entity);
        system.added(entity);
      }
    }
  }

  /**
   * Renders all systems implementing the {@link IRenderSystem} interface, in order according to their set {@link RenderStage}.
   */
  public void render(RenderStage stage)
  {
    for (IRenderSystem system : this.rendersystems.get(stage).values())
    {
      system.render(this.scene, this.getSystemEntities(((UpdateSystem) system).getClass()));
    }
  }

  /**
   * Updates all systems.
   */
  public void update()
  {
    // execute scripts
    // this.scripts.forEach((k, v) -> v.forEach((script) -> script.update(this.scene)));

    for (Entity entity : this.entities.values())
    {
      entity.update(this.scene);
    }

    for (UpdateSystem system : this.systems.values())
    {
      system.update(this.scene, this.getSystemEntities(system.getClass()));
    }
  }

  /**
   * Adds an entity to this ECS.
   * The entity gets retroactively added to the systems whose' requirements it meets.
   * @param entity entity
   */
  public void addEntity(Entity entity)
  {
    // generate entity ID
    this.entities.put(entity.id(), entity);

    if (entity.name() != null)
    {
      this.identifiers.put(entity.name(), entity);
    }

    for (UpdateSystem system : this.systems.values())
    {
      this.mapEntitySystem(entity, system);
    }
  }

  /**
   * Checks if an entity has a component attached to it.
   * @param entity entity
   * @param component class identifier of the component
   * @param <T> extends EntityComponent
   * @return contains
   */
  public <T extends EntityComponent> boolean has(Entity entity, Class<T> component)
  {
    return entity.has(component);//this.ecm.get(entity).containsKey(component);
  }

  public void map(Entity entity)
  {
    // addEntity entity if it isn't in the ECS yet.
    if (!this.entities.containsKey(entity.id()))
    {
      this.addEntity(entity);
    }

    for (UpdateSystem system : this.systems.values())
    {
      this.mapEntitySystem(entity, system);
    }
  }

  public <T extends EntityComponent> void remove(UUID id, Class<T> component) {}

  public <T extends EntityComponent> T get(Entity entity, Class<T> component)
  {
    if (!this.has(entity, component))
    {
      throw new Error("[ERROR] entity " + entity.id() + " does not contain component " + component);
    }

    return entity.get(component);
    // return component.cast(this.ecm.get(entity).get(component));
  }

  public Entity get(UUID id)
  {
    return this.entities.get(id);
  }

  public Entity get(String identifier)
  {
    return this.identifiers.getOrDefault(identifier, null);
  }

  public <T extends UpdateSystem> T get(Class<T> system)
  {
    return (T) this.systems.get(system);
  }

  /**
   * Adds a system to this ECS.
   * Any entities that fulfill the systems' component requirements will be retroactively mapped to the system.
   * This is a costly operation, ideally all systems should be added before any entities were initialized.
   * @param system system
   */
  public void addEntity(UpdateSystem system)
  {
    if (this.systems.containsKey(system.getClass()))
    {
      java.lang.System.err.println("[INFO] cannot add the same system twice");
      return;
    }

    // test if any entities can be mapped to the system
    this.systems.put(system.getClass(), system);
    this.sm.put(system.getClass(), new ArrayList<>());
    for (Entity entity : this.entities.values())
    {
      this.mapEntitySystem(entity, system);
    }

    if (system instanceof IRenderSystem)
    {
      this.rendersystems.get(((IRenderSystem) system).priority()).put(system.getClass(), (IRenderSystem) system);
    }
  }

  public EntityComponentSystem(Scene scene)
  {
    this.scene = scene;
    this.entities = new HashMap<>();
    this.identifiers = new HashMap<>();
    this.systems = new HashMap<>();
    this.sm = new HashMap<>();
    this.rendersystems = new HashMap<>();

    for (RenderStage stage : RenderStage.values())
    {
      this.rendersystems.put(stage, new HashMap<>());
    }
  }
}