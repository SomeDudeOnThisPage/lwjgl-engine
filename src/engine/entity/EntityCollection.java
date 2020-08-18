package engine.entity;

import java.util.HashSet;

public abstract class EntityCollection
{
  protected final HashSet<Entity> entities = new HashSet<>();

  public abstract <T extends EntityComponent> Class<T>[] components();

  public final HashSet<Entity> getEntities()
  {
    return this.entities;
  }

  public final boolean containsEntity(Entity entity)
  {
    return this.entities.contains(entity);
  }

  protected void onCollectionRemoved() {}

  protected void onEntityAdded(Entity entity) {}
  protected void onEntityRemoved(Entity entity) {}

  public final void addEntity(Entity entity)
  {
    if (this.entities.contains(entity))
    {
      throw new UnsupportedOperationException("cannot add entity " + entity.id() + " to collection " + this + " - collection already contains entity");
    }

    this.entities.add(entity);
    this.onEntityAdded(entity);
  }

  public final void removeEntity(Entity entity)
  {
    if (!this.entities.contains(entity))
    {
      throw new UnsupportedOperationException("cannot remove entity " + entity.id() + " from collection " + this + " - collection does not contain entity");
    }

    this.entities.remove(entity);
    this.onEntityRemoved(entity);
  }
}
