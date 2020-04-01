package engine.core.entity.system.rendering;

import engine.core.entity.Entity;
import engine.core.entity.component.EntityComponent;
import engine.core.entity.component.MaterialComponent;
import engine.core.entity.component.TransformComponent;
import engine.core.entity.component.terrain.TerrainComponent;
import engine.core.entity.system.IRenderSystem;
import engine.core.entity.system.UpdateSystem;
import engine.core.gfx.Shader;
import engine.core.rendering.RenderStage;
import engine.core.scene.Scene;

import java.util.ArrayList;

public class TerrainRenderingSystem extends UpdateSystem implements IRenderSystem
{
  private Shader shader;

  @Override
  public RenderStage priority()
  {
    return RenderStage.DEFERRED_GEOMETRY_PASS;
  }

  @Override
  public void render(Scene scene, ArrayList<Entity> entities)
  {
    this.shader.bind();

    for (Entity entity : entities)
    {
      this.shader.setUniform("u_model", entity.get(TransformComponent.class).construct());
      entity.get(MaterialComponent.class).material.bind(this.shader);
      entity.get(TerrainComponent.class).mesh.render();
    }
  }

  @Override
  public <T extends EntityComponent> Class<T>[] components() {
    return new Class[]
    {
      TerrainComponent.class,
      TransformComponent.class,
      MaterialComponent.class
    };
  }

  @Override
  public void update(Scene scene, ArrayList<Entity> entities) {}

  @Override
  public void added(Entity entity) {}

  public TerrainRenderingSystem()
  {
    this.shader = Shader.getInstance("deferred/terrain");
  }
}
