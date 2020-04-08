package engine.core.entity.component;

import engine.core.entity.Entity;
import engine.core.scene.Scene;

public abstract class ScriptComponent extends EntityComponent
{
  public abstract void update(Scene scene);

  /**
   * Returns an {@link EntityComponent} or {@link ScriptComponent} of the {@link Entity} this {@link ScriptComponent}
   * is attached to.
   * @param component A type parameter defining the {@link Class} of the {@link EntityComponent} or {@link ScriptComponent}.
   * @param <T> A type parameter defining the {@link Class} of the {@link EntityComponent} or {@link ScriptComponent}.
   * @return The {@link EntityComponent} or {@link ScriptComponent} or {@code null} if no component was found.
   */
  protected final <T extends EntityComponent> T get(Class<T> component)
  {
    return this.entity.get(component);
  }
}