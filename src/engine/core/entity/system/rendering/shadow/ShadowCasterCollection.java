package engine.core.entity.system.rendering.shadow;

import engine.core.entity.Entity;
import engine.core.entity.component.EntityComponent;
import engine.core.entity.component.MeshComponent;
import engine.core.entity.component.ShadowCasterComponent;
import engine.core.entity.component.TransformComponent;
import engine.core.entity.system.UpdateSystem;
import engine.core.scene.Scene;

import java.util.ArrayList;

public class ShadowCasterCollection extends UpdateSystem
{
  @Override
  public <T extends EntityComponent> Class<T>[] components()
  {
    return new Class[]
    {
      TransformComponent.class,
      MeshComponent.class,
      ShadowCasterComponent.class
    };
  }

  @Override
  public void update(Scene scene, ArrayList<Entity> entities) {}

  @Override
  public void added(Entity entity) {}
}