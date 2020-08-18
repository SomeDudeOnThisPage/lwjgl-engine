package engine.entity;

import engine.render.Pipeline;
import engine.render.RenderStage;
import engine.scene.Scene;

import java.util.*;

import static engine.Engine.*;

public class EntityComponentSystem
{
  public enum SystemPriority
  {
    SYNC_PHYSICS_BEFORE,
    LEVEL_1,
    LEVEL_2,
    LEVEL_3,
    LEVEL_4,
    LEVEL_5,
    SYNC_PHYSICS_AFTER,

    /**
     * CALLBACK_ONLY should not be used in ECS update loops, rather outside of any ECS update / render loop.
     * For instance, the management systems for the physics engine use this constant.
     */
    CALLBACK_ONLY
  }

  /** Entity IDs mapped to their handles. */
  private final HashMap<UUID, Entity> entities;

  /** Entity identifiers mapped to their handles */
  private final HashMap<String, Entity> identifiers;

  /** System priority map */
  private final HashMap<SystemPriority, ArrayList<EntitySystem>> priorities;

  /** Active collections map. */
  private final HashMap<Class<? extends EntityCollection>, EntityCollection> collections;

  private final HashMap<RenderStage, ArrayList<RenderSystem<? extends Pipeline>>> renderers;

  /** Add entities queue */
  private final ArrayList<Entity> tba;

  /** Remove entities queue */
  private final ArrayList<Entity> tbr;

  /**
   * Checks if an {@link Entity} can be mapped to an {@link EntityCollection}, and adds the {@link Entity},
   * and adds said {@link Entity} to the list of {@link EntityCollection#entities}.
   * @param entity entity
   * @param collection collection
   */
  private void mapEntityCollection(Entity entity, EntityCollection collection)
  {
    if (collection.components().length > 0)
    {
      boolean hasRequired = true;
      for (Class<? extends EntityComponent> listen : collection.components())
      {
        if (!entity.containsComponent(listen))
        {
          hasRequired = false;
          break;
        }
      }

      if (hasRequired)
      {
        // add entity to collection
        if (!collection.containsEntity(entity))
        {
          collection.addEntity(entity);
        }
      }
      else
      {
        // always call remove to enable easy removal of components at runtime
        // remove entity from collection
        if (collection.containsEntity(entity))
        {
          collection.removeEntity(entity);
        }
      }
    }
  }

  @SuppressWarnings("rawtypes") // bad design :)
  public void render(RenderStage stage, Pipeline pipeline)
  {
    for (RenderSystem system : this.renderers.get(stage))
    {
      system.render(pipeline);
    }
  }

  public void update(Scene scene)
  {
    for (Entity entity : this.tba)
    {
      // generate entity ID
      this.entities.put(entity.id(), entity);

      if (entity.name() != null)
      {
        this.identifiers.put(entity.name(), entity);
      }

      for (EntityCollection collection : this.collections.values())
      {
        this.mapEntityCollection(entity, collection);
      }

      Log.info("[ECS] added entity '" + ((entity.name() != null) ? entity.name() : entity.id().toString()) + "'");
    }

    // update systems
    for (Map.Entry<SystemPriority, ArrayList<EntitySystem>> level : this.priorities.entrySet())
    {
      if (level.getKey().equals(SystemPriority.CALLBACK_ONLY)) { continue; }

      for (EntitySystem system : level.getValue())
      {
        system.update(scene);
      }
    }

    // update entity scripts
    for (Entity entity : this.entities.values())
    {
      // remap entity on signature (component) change
      if (entity.signature() && !entity.flagged(Entity.Flags.DISABLE_SIGNATURE))
      {
        this.map(entity);
      }

      entity.update(scene);
    }

    // for (System system : this.systems.values())
    // {
    //  system.update(scene);
    // }

    for (Entity entity : this.tbr)
    {
      // forecefully rip the child out of a loving fathers' arms
      Entity parent = entity.getParent();
      if (parent != null)
      {
        parent.children().remove(entity);
      }

      Log.info("removing entity '" + entity.name() + "'");

      // re-home all children
      for (Entity child : entity.children())
      {
        Log.info("relocating child '" + child.name() + "' from entity '" + entity.name() + "' to entity '" + parent + "'");
        child.setParent(parent);
        if (parent != null)
        {
          parent.addChild(child);
        }
      }

      this.entities.remove(entity.id());
      this.identifiers.remove(entity.name());

      // remove entity from all applicable collections
      for (EntityCollection collection : this.collections.values())
      {
        if (collection.containsEntity(entity))
        {
          collection.removeEntity(entity);
        }
      }

      entity.removeComponents();
      entity.update(scene); // update the entity one last time to process changes
    }

    // clear queues
    this.tba.clear();
    this.tbr.clear();
  }

  /**
   * Adds an entity to this ECS.
   * The entity gets retroactively added to the systems whose' requirements it meets.
   * @param entity entity
   */
  public void addEntity(Entity entity)
  {
    if (!this.tba.contains(entity))
    {
      this.tba.add(entity);
    }
  }

  /**
   * Adds an {@link Entity} to this {@link EntityComponentSystem}, if not present, and (re-)maps the {@link Entity} to the
   * corresponding {@link EntityCollection}s.
   * @param entity The {@link Entity} to be mapped.
   */
  public void map(Entity entity)
  {
    // addEntity entity if it isn't in the ECS yet.
    if (!this.entities.containsKey(entity.id()))
    {
      this.addEntity(entity);
    }

    for (EntityCollection collection : this.collections.values())
    {
      this.mapEntityCollection(entity, collection);
    }
  }

  public void removeEntity(Entity entity)
  {
    this.tbr.add(entity);
  }

  public boolean hasEntity(String id)
  {
    return this.identifiers.containsKey(id);
  }

  public Entity get(UUID id)
  {
    return this.entities.getOrDefault(id, null);
  }
  public Entity get(String identifier)
  {
    return this.identifiers.getOrDefault(identifier, null);
  }

  public int amount()
  {
    return this.entities.size();
  }

  public Collection<Entity> entities()
  {
    return this.entities.values();
  }

  public HashMap<Class<? extends EntityCollection>, EntityCollection> collections()
  {
    return this.collections;
  }

  /**
   * Retrieves an {@link EntityCollection} instance based on its' class index.
   * @param collection The {@link Class} of the {@link EntityCollection} instance.
   * @param <T> Type parameter - Class should extend {@link EntityCollection}.
   * @return The {@link EntityCollection} instance, or null, should none be found.
   */
  public <T extends EntityCollection> T get(Class<T> collection)
  {
    T value = collection.cast(this.collections.getOrDefault(collection, null));

    if (value == null)
    {
      Log.warning("[ECS] attempted to retrieve invalid collection '" + collection.getSimpleName() + "'");
    }

    return value;
  }

  /**
   * Adds a new {@link EntityCollection} to the list of {@link EntityCollection}s that this {@link EntityComponentSystem}
   * manages.
   * @param collection The {@link EntityCollection} to be added.
   */
  public void addCollection(EntityCollection collection)
  {
    if (this.collections.containsKey(collection.getClass()))
    {
      Log.warning("[ECS] attempted to add duplicate collection '" + collection.getClass().getSimpleName() + "'");
      return;
    }

    // add the collection to our active collections
    this.collections.put(collection.getClass(), collection);

    // check if the collection is also a system
    if (collection instanceof EntitySystem)
    {
      EntitySystem system = (EntitySystem) collection;
      this.priorities.get(system.priority()).add(system);
    }

    // check if the collection is also a rendering system
    if (collection instanceof RenderSystem)
    {
      RenderSystem<? extends Pipeline> system = (RenderSystem<? extends Pipeline>) collection;
      this.renderers.get(system.stage()).add(system);
    }

    // map any entity that fulfills the component requirements to the newly added collection
    for (Entity entity : this.entities.values())
    {
      this.mapEntityCollection(entity, collection);
    }
  }

  public EntityComponentSystem()
  {
    this.entities = new HashMap<>();
    this.identifiers = new HashMap<>();

    this.collections = new HashMap<>();
    this.priorities = new HashMap<>();
    for (SystemPriority priority : SystemPriority.values())
    {
      this.priorities.put(priority, new ArrayList<>());
    }

    this.renderers = new HashMap<>();
    for (RenderStage stage : RenderStage.values())
    {
      this.renderers.put(stage, new ArrayList<>());
    }

    this.tba = new ArrayList<>();
    this.tbr = new ArrayList<>();
  }
}







