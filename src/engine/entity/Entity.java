package engine.entity;

import engine.scene.Scene;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Entity
{
  public enum Flags
  {
    NO_REMOVE,
    NO_PARENT_CHANGE,
    NO_GUIZMO,
    DISABLE_SIGNATURE
  }

  private final UUID id = UUID.randomUUID();
  protected String identifier;

  /** Does this {@link Entity} need to be remapped? */
  private boolean signatureChange = false;

  private final EnumSet<Flags> flags = EnumSet.noneOf(Flags.class);

  private final HashMap<Class<? extends EntityComponent>, EntityComponent> components = new HashMap<>();
  private final HashMap<Class<?>, Behaviour> scripts = new HashMap<>();

  private final ArrayList<Class<?>> tbr = new ArrayList<>();

  private final CopyOnWriteArrayList<Entity> children = new CopyOnWriteArrayList<>();
  private final CopyOnWriteArrayList<Entity> tbrc = new CopyOnWriteArrayList<>();
  private Entity parent;

  private <T extends EntityComponent> boolean canRemove(T component)
  {
    if (this.containsComponent(component.getClass()))
    {
      return !component.flags().contains(EntityComponent.Flags.NO_REMOVE);
    }
    return false;
  }

  protected boolean signature()
  {
    // fine as this method should only be called by the ECS in specific cases, kinda shit code style though...
    if (this.signatureChange)
    {
      this.signatureChange = false;
      return true;
    }

    return false;
  }

  /**
   * Adds all the given {@link Flags Entity Flags} to this {@link Entity}.
   * @param flag Flags to be added. This is a {@code Varargs} parameter.
   * @return {@code this} {@link Entity}, to enable factory behaviour.
   */
  public Entity flag(@NotNull Flags... flag)
  {
    for (Flags eflag : flag)
    {
      if (!this.flags.contains(eflag))
      {
        this.flags.add(eflag);
      }
    }

    return this;
  }

  /**
   * Removes all the given {@link Flags Entity Flags} from this {@link Entity}.
   * @param flag Flags to be removed. This is a {@code Varargs} parameter.
   * @return {@code this} {@link Entity}, to enable factory behaviour.
   */
  public Entity unflag(@NotNull Flags... flag)
  {
    for (Flags eflag : flag)
    {
      this.flags.remove(eflag);
    }

    return this;
  }

  /**
   * Returns {@code true} if this {@link Entity} contains all the given {@link Flags Entity Flags}.
   * @param flag Flags to be checked. This is a {@code Varargs} parameter.
   * @return {@code true} if this {@link Entity} contains all the given {@link Flags Entity Flags}.
   */
  public boolean flagged(@NotNull Flags... flag)
  {
    for (Flags eflag : flag)
    {
      if (!this.flags.contains(eflag))
      {
        return false;
      }
    }

    return true;
  }

  /**
   * Returns a {@link List} of child {@link Entity Entities} of this {@link Entity}.
   * @return {@link List} of child {@link Entity Entities} of this {@link Entity}.
   */
  public List<Entity> children()
  {
    return this.children;
  }

  /**
   * Adds a child {@link Entity} to this {@link Entity}, and sets the {@code parent} of the child {@link Entity}
   * to this {@link Entity}.
   * @param entity The child {@link Entity}.
   */
  public void addChild(@NotNull Entity entity)
  {
    if (entity.getParent() != null && !this.children.contains(entity))
    {
      entity.getParent().children().remove(entity);
      entity.setParent(this);
      this.children.add(entity);
    }

    if (entity.getParent() == null && !this.children.contains(entity))
    {
      entity.setParent(this);
      this.children.add(entity);
    }
  }

  /**
   * Removes a child {@link Entity} from this {@link Entity}, and sets the {@code parent} of the child {@link Entity}
   * to {@code null}.
   * <p>
   *   Note that children are not removed immediately, rather they are removed during the next
   *   {@link Entity#update(Scene)} tick, so that we don't run into any concurrent modification problems.
   * </p>
   * @param entity The child {@link Entity}.
   */
  public void removeChild(@NotNull Entity entity)
  {
    this.tbrc.add(entity);
  }

  /**
   * Sets the {@code parent} {@link Entity} of this {@link Entity}.
   * @param entity The {@code parent} {@link Entity} of this {@link Entity}. Can be {@code null}.
   */
  public void setParent(Entity entity)
  {
    this.parent = entity;
  }

  /**
   * Returns the {@code parent} {@link Entity} of this {@link Entity}.
   * @return The {@code parent} {@link Entity} of this {@link Entity}. Note that the {@code parent} may be {@code null}!
   */
  @Nullable
  public Entity getParent()
  {
    return this.parent;
  }

  /**
   * Returns the {@link UUID} of this {@link Entity}.
   * @return The {@link UUID} of this {@link Entity}.
   */
  @NotNull
  public UUID id()
  {
    return this.id;
  }

  /**
   * Returns the {@link String} name of this {@link Entity}.
   * <p>
   *   Note that, should the name not be set on creation of this {@link Entity}, this method will just return
   *   a {@link String} representation of this {@link Entity entities} {@link UUID}.
   * </p>
   * @return The {@link String} name of this {@link Entity}.
   */
  public String name()
  {
    if (this.identifier != null)
    {
      return this.identifier;
    }

    return this.id.toString();
  }

  /**
   * What do you think this does?
   * @return Woo wee what does this return?
   */
  @Override
  public String toString()
  {
    return this.name();
  }

  public <T extends EntityComponent> Entity addComponent(@NotNull T component)
  {
    component.entity = this;

    if (component.getClass().getSuperclass().equals(Behaviour.class))
    {
      this.scripts.put(component.getClass(), (Behaviour) component);
    }
    else
    {
      this.components.put(component.getClass(), component);
    }

    // call hook
    component.onComponentAttached();
    this.signatureChange = true;

    return this;
  }

  public <T extends EntityComponent> boolean containsComponent(@NotNull Class<T> component)
  {
    if (this.tbr.size() > 0)
    {
      // do O(n) check
      return (this.components.containsKey(component) || this.scripts.containsKey(component)) && !this.tbr.contains(component);
    }
    // do O(1) check
    return this.components.containsKey(component) || this.scripts.containsKey(component);
  }

  public <T extends EntityComponent> T getComponent(@NotNull Class<T> component)
  {
    if (component.getSuperclass() == Behaviour.class)
    {
      if (!this.scripts.containsKey(component))
      {
        return null;
      }

      return component.cast(this.scripts.get(component));
    }

    if (!this.containsComponent(component))
    {
      throw new UnsupportedOperationException("attempted to retrieve nonexistant component of type '"
        + component.getSimpleName() + "' from entity '" + this + "'");
    }

    return component.cast(this.components.get(component));
  }

  public Collection<EntityComponent> components()
  {
    return this.components.values();
  }

  public Collection<Behaviour> scripts()
  {
    return this.scripts.values();
  }

  public <T extends EntityComponent> void removeComponent(@NotNull Class<T> component)
  {
    if (this.containsComponent(component) && this.canRemove(this.getComponent(component)))
    {
      this.tbr.add(component);
      this.signatureChange = true;
    }
  }

  public <T extends EntityComponent> void removeComponent(@NotNull T component)
  {
    if (this.containsComponent(component.getClass()) && this.canRemove(component))
    {
      this.tbr.add(component.getClass());
      this.signatureChange = true;
    }
  }

  public final void removeComponents()
  {
    this.tbr.addAll(this.components.keySet());
    this.tbr.addAll(this.scripts.keySet());
  }

  /**
   * Updates this Entities' {@link Behaviour}s.
   * This method should be called by the {@link EntityComponentSystem} internally, and not be overridden.
   * @param scene The {@link Scene} the {@link Behaviour}s should base their updates on.
   */
  public final void update(@NotNull Scene scene)
  {
    for (Class<?> component : this.tbr)
    {
      // call removal hook and remove component / script after execution of said hook
      if (component.getSuperclass().equals(Behaviour.class))
      {
        if (this.scripts.containsKey(component))
        {
          this.scripts.get(component).onComponentRemoved();
          this.scripts.remove(component);
        }
      }
      else
      {
        if (this.components.containsKey(component))
        {
          this.components.get(component).onComponentRemoved();
          this.components.remove(component);
        }
      }
    }

    this.tbr.clear();

    for (Entity entity : this.tbrc)
    {
      entity.setParent(null);
      this.children.remove(entity);
    }

    this.tbrc.clear();

    // update self-contained systems (behaviours)
    for (Behaviour script : this.scripts.values())
    {
      if (!script.flags().contains(EntityComponent.Flags.INACTIVE))
      {
        script.update(scene);
      }
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
  public Entity(@NotNull String identifier)
  {
    this.identifier = identifier.toLowerCase();
  }
}