package engine.core.entity.system;

import engine.core.entity.Entity;
import engine.core.entity.component.EntityComponent;
import engine.core.scene.Scene;

import java.util.ArrayList;

public abstract class UpdateSystem
{
  /**
   * This method needs to return an array of all {@link EntityComponent} subclasses the system requires an entity
   * to own.
   * @return The list of {@link EntityComponent} subclasses this system should listen to.
   */
  public abstract <T extends EntityComponent> Class<T>[] components();

  /**
   * The update method should update all {@link Entity} instances mapped to this system.
   * @param scene The {@link Scene} this system should update.
   * @param entities An {@link ArrayList} containing all the {@link Entity} handles this
   *                 system needs to update.
   */
  public abstract void update(Scene scene, ArrayList<Entity> entities);

  /**
   * Called whenever a new {@link Entity} has been added to the system.
   * @param entity The entity handle of the added entity.
   */
  public abstract void added(Entity entity);
}
