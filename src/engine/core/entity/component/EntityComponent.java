package engine.core.entity.component;

import engine.core.entity.Entity;

public abstract class EntityComponent
{
  public Entity entity;

  public void onComponentRemoved() {}

  /**
   * This method should be used instead of a constructor, as the {@link Entity} member variable of this
   * component will already have been set.
   */
  public void onComponentAttached() {}
}