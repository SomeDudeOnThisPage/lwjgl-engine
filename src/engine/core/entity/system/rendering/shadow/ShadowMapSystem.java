package engine.core.entity.system.rendering.shadow;

import engine.core.entity.Entity;
import engine.core.entity.component.EntityComponent;
import engine.core.entity.component.MeshComponent;
import engine.core.entity.component.TransformComponent;
import engine.core.entity.component.shadow.ShadowSourceComponent;
import engine.core.entity.system.IRenderSystem;
import engine.core.entity.system.UpdateSystem;
import engine.core.rendering.RenderStage;
import engine.core.scene.Scene;
import engine.util.settings.Settings;

import java.util.ArrayList;

public class ShadowMapSystem extends UpdateSystem implements IRenderSystem
{
  private ArrayList<Entity> enabled;

  @Override
  public RenderStage priority() { return RenderStage.DEFERRED_SHADOW_PASS; }

  @Override
  public <T extends EntityComponent> Class<T>[] components()
  {
    return new Class[]
      {
        TransformComponent.class,
        MeshComponent.class,
        ShadowSourceComponent.class
      };
  }

  @Override
  public void render(Scene scene, ArrayList<Entity> entities)
  {
    // bind shadow maps FBO
    for (Entity entity : this.enabled)
    {
      // bind depth shader

      // render
    }
  }

  @Override
  public void update(Scene scene, ArrayList<Entity> entities)
  {
    // clear the list of enabled shadow sources, and repopulate based on a condition (distance player <-> source)
    this.enabled.clear();
    for (Entity entity : entities)
    {
      float distance = entity.get(TransformComponent.class).position.distance(scene.getPlayer().getPosition());
      if (distance < entity.get(ShadowSourceComponent.class).distance)
      {
        if (this.enabled.size() < Settings.geti("MaxShadowTextureMaps"))
        {
          this.enabled.add(entity);
        }
        else
        {
          break;
        }
      }
    }
  }

  @Override
  public void added(Entity entity) {}

  public ShadowMapSystem()
  {
    this.enabled = new ArrayList<>();
  }
}
