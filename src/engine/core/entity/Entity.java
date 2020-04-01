package engine.core.entity;

import engine.Engine;
import engine.core.entity.component.EntityComponent;
import engine.core.entity.component.ScriptComponent;
import engine.core.scene.Scene;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * The {@link Entity} handle class is used to interface with the {@link EntityComponentSystem}.
 * An {@link Entity} has a set of {@link EntityComponent}s, based on which the entity is added to
 * different {@link engine.core.entity.system.UpdateSystem}s of the {@link EntityComponentSystem}.
 * An {@link Entity} can only have one component of each type at one time, when adding a new
 * {@link EntityComponent} of one type again, the old component will simply be overridden completely.
 * <p>An entity can also contain a set of {@link engine.core.entity.component.ScriptComponent}s. These
 * act somewhat like self-contained systems, in the sense that they possess their own
 * {@link engine.core.entity.component.ScriptComponent} method, in which custom computation
 * can take place. Attaching a {@link engine.core.entity.component.ScriptComponent} can be done the same
 * way as attaching an {@link EntityComponent}, however, they are managed separately in the {@link EntityComponentSystem}
 * and executed before any {@link engine.core.entity.system.UpdateSystem}s' code is executed.
 * When attaching {@link engine.core.entity.component.ScriptComponent}s, make sure that all required data you
 * wish to assign in the {@link ScriptComponent} method is already available (e.g. required components)
 * are already assigned.</p>
 * <p>An {@link Entity} can be indexed in custom code from the {@link EntityComponentSystem} when an unique
 * {@link String} identifier has passed to the entities' constructor. Note that entities subclassing this
 * class should implement a constructor calling {@code super(String identifier)}, to enable this behaviour
 * with custom entity implementations.</p>
 * <p>For an example of custom entity subclassing, refer to the {@link engine.core.rendering.Camera} and
 * {@link engine.core.scene.Player} entities.</p>
 */
public class Entity
{
  /**
   * Numeric {@link UUID} of this {@link Entity}.
   */
  private UUID id = UUID.randomUUID();

  /**
   * The (unique, guaranteed by the {@link EntityComponentSystem}) {@link String} identifier of this {@link Entity}.
   */
  private String identifier;

  /**
   * A {@link HashMap} of {@link EntityComponent}s attached to this {@link Entity} indexed by their
   * {@link Class} identifier.
   */
  private HashMap<Class, EntityComponent> components = new HashMap<>();

  /**
   * The {@link HashMap} of {@link ScriptComponent}s (MonoBehaviours) attached to this {@link Entity}.
   */
  //private ArrayList<ScriptComponent> scripts = new ArrayList<>();
  private HashMap<Class, ScriptComponent> scripts = new HashMap<>();

  /**
   * The child {@link Entity} instances of this {@link Entity} in the {@link Engine}s'
   * {@link engine.core.scene.SceneGraph} structure.
   */
  private ArrayList<Entity> children = new ArrayList<>();

  /**
   * The parent {@link Entity} of this {@link Entity} in the {@link Engine}s' {@link engine.core.scene.SceneGraph}
   * structure.
   */
  private Entity parent;

  /**
   * Returns the {@link java.util.UUID} of this {@link Entity}.
   * @return The ID of this {@link Entity}.
   */
  public UUID id()
  {
    return this.id;
  }

  /**
   * Returns the (unique, guaranteed by the {@link EntityComponentSystem}) {@link String} identifier of this
   * {@link Entity}.
   * @return The {@link String} identifier of this {@link Entity}.
   */
  public String name()
  {
    return this.identifier;
  }

  /**
   * Adds a new {@link EntityComponent} to this {@link Entity}.
   * If the {@link Entity} already contains an {@link EntityComponent} with the same class identifier, this component
   * will be overridden in favour of the new {@link EntityComponent}.
   * @param component The {@link EntityComponent} to be added to this {@link Entity}.
   * @return The {@link Entity}, returned to enable chaining of component adding operations.
   */
  public <T extends EntityComponent> Entity add(T component)
  {
    if (component.getClass().getSuperclass().equals(ScriptComponent.class))
    {
      this.scripts.put(component.getClass(), (ScriptComponent) component);
      ((ScriptComponent) component).internal_init(this);
      ((ScriptComponent) component).init(Engine.scene_manager.getScene());
    }
    else
    {
      this.components.put(component.getClass(), component);
      Engine.scene_manager.getScene().ecs().map(this);
    }

    return this;
  }

  public void setParent(Entity parent)
  {
    this.parent = parent;
  }

  public Entity getParent()
  {
    return this.parent;
  }

  public ArrayList getChildren()
  {
    return this.children;
  }

  public void add(Entity child)
  {
    child.setParent(this);
    this.children.add(child);
  }

  public void remove(Entity child)
  {
    child.setParent(null);
    this.children.remove(child);
  }

  /**
   * Checks if this entity contains the given component, determined by the components' class.
   * @param component the components' class
   * @return has component
   */
  public <T extends EntityComponent> boolean has(Class<T> component)
  {
    return this.components.containsKey(component);
  }

  /**
   * Returns an entities' {@link EntityComponent} indexed by its' class.
   * @param component An {@link EntityComponent} subclass.
   * @return The {@link EntityComponent} cast to the corresponding class, or null if no {@link EntityComponent} was
   *         found.
   */
  public <T extends EntityComponent> T get(Class<T> component)
  {
    if (component.getSuperclass() == ScriptComponent.class)
    {
      if (!this.scripts.containsKey(component))
      {
        //throw new Error("entity " + this.id() + " does not contain component " + component);
        return null;
      }

      return component.cast(this.scripts.get(component));
    }

    if (!this.has(component))
    {
      //throw new Error("entity " + this.id() + " does not contain component " + component);
      return null;
    }

    return component.cast(this.components.get(component));
  }

  public <T extends EntityComponent> void remove(Class<T> component)
  {
    Engine.scene_manager.getScene().ecs().remove(this.id, component);
  }

  /**
   * Updates this Entities' {@link ScriptComponent}s.
   * This method should be called by the {@link EntityComponentSystem} internally, and not be overridden.
   * @param scene The {@link Scene} the {@link ScriptComponent}s should base their updates on.
   */
  public final void update(Scene scene)
  {
    for (ScriptComponent script : this.scripts.values())
    {
      script.update(scene);
    }
  }

  /**
   * Constructs a new {@link Entity} without a unique {@link String} identifier.
   */
  public Entity() {}

  /**
   * Constructs a new {@link Entity} with a unique {@link String} identifier.
   * Note that {@link String} identifiers are always converted to lower case.
   * @param identifier The {@link String} identifier for this {@link Entity}.
   */
  public Entity(String identifier)
  {
    this.identifier = identifier.toLowerCase();
  }
}