package engine.core.entity.component;

import engine.core.entity.Entity;
import engine.core.scene.Scene;

public abstract class Behaviour extends EntityComponent
{
  public abstract void update(Scene scene);

  /**
   * Convenience shortcut retrieving the {@link TransformComponent} of the {@link Entity} this {@link Behaviour} is
   * bound to.
   * @return The {@link TransformComponent} of the {@link Entity} this {@link Behaviour} is bound to.
   */
  public TransformComponent transform()
  {
    return this.entity.get(TransformComponent.class);
  }

  /**
   * Returns an {@link EntityComponent} or {@link Behaviour} of the {@link Entity} this {@link Behaviour}
   * is attached to.
   * @param component A type parameter defining the {@link Class} of the {@link EntityComponent} or {@link Behaviour}.
   * @param <T> A type parameter defining the {@link Class} of the {@link EntityComponent} or {@link Behaviour}.
   * @return The {@link EntityComponent} or {@link Behaviour} or {@code null} if no component was found.
   */
  protected final <T extends EntityComponent> T get(Class<T> component)
  {
    return this.entity.get(component);
  }
}