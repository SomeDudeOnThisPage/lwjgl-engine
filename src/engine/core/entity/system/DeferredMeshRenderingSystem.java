package engine.core.entity.system;

import engine.core.entity.Entity;
import engine.core.entity.component.*;
import engine.core.gfx.batching.AssetManager;
import engine.core.gfx.material.MaterialArchetype;
import engine.core.gfx.VertexArray;
import engine.core.rendering.RenderStage;
import engine.core.scene.Scene;
import engine.core.scene.SceneGraph;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL43C.*;

public class DeferredMeshRenderingSystem extends UpdateSystem implements IRenderSystem
{
  @Override
  public <T extends EntityComponent> Class<T>[] components()
  {
    return new Class[]
    {
      MeshComponent.class,
      TransformComponent.class,
    };
  }

  @Override
  public void update(Scene scene, ArrayList<Entity> entities) {}

  @Override
  public void added(Entity entity) {}

  @Override
  public RenderStage priority()
  {
    return RenderStage.DEFERRED_GEOMETRY_PASS;
  }

  @Override
  public void render(Scene scene, ArrayList<Entity> entities)
  {
    glEnable(GL_CULL_FACE);
    glCullFace(GL_BACK);
    for (Entity entity : entities)
    {
      VertexArray[] mesh = entity.get(MeshComponent.class).mesh;
      MaterialArchetype[] material = entity.get(MeshComponent.class).material;

      for (int i = 0; i < mesh.length; i++)
      {
        if (material[i] == null)
        {
          AssetManager.getMissingMaterial().bind();
          AssetManager.getMissingMaterial().shader().setUniform("u_model", SceneGraph.constructTransform(entity));
        }
        else
        {
          material[i].bind();
          material[i].shader().setUniform("u_model", SceneGraph.constructTransform(entity));
        }

        mesh[i].render();
      }
    }

    glDisable(GL_CULL_FACE);
  }

  public DeferredMeshRenderingSystem()
  {
  }
}