package editor.entity.system;

import engine.entity.component.MaterialComponent;
import engine.entity.component.Mesh;
import engine.entity.*;
import engine.entity.component.Transform;
import engine.gfx.material.Material;
import engine.gfx.buffer.VertexArray;
import engine.render.ForwardRenderer;
import engine.render.Pipeline;
import engine.render.RenderStage;
import engine.render.deferred.DeferredRenderer;
import engine.scene.SceneGraph;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Predicate;

import static engine.Engine.*;

import static org.lwjgl.opengl.GL42C.*;

// todo: rendering systems
public class SimpleMeshRenderer extends EntityCollection implements RenderSystem<DeferredRenderer>
{
  //private final ShaderProgram forward;

  private final HashMap<String, ArrayList<AbstractMap.SimpleEntry<Entity, String>>> sorted = new HashMap<>();

  @Override
  public <T extends EntityComponent> Class<T>[] components()
  {
    return new Class[]
      {
        MaterialComponent.class,
        Transform.class,
        Mesh.class
      };
  }

  @Override
  public RenderStage stage()
  {
    return RenderStage.FORWARD_PASS;
  }

  @Override
  public void onEntityAdded(Entity entity)
  {
    ArrayList<String> material = entity.getComponent(MaterialComponent.class).material;
    for (int i = 0; i < material.size(); i++)
    {
      String mtl = material.get(i);
      if (!this.sorted.containsKey(mtl))
      {
        this.sorted.put(mtl, new ArrayList<>());
      }

      this.sorted.get(mtl).add(new AbstractMap.SimpleEntry<>(entity, entity.getComponent(Mesh.class).mesh.get(i)));
    }
  }

  @Override
  public void onEntityRemoved(Entity entity)
  {
    ArrayList<String> material = entity.getComponent(MaterialComponent.class).material;

    for (ArrayList<AbstractMap.SimpleEntry<Entity, String>> list : this.sorted.values())
    {
      list.removeIf(entry -> entry.getKey() == entity);
    }
  }

  @Override
  public void render(DeferredRenderer pipeline)
  {
    for (Entity entity : this.entities)
    {
      // check mapping
      if (entity.getComponent(MaterialComponent.class).remapped)
      {
        this.onEntityRemoved(entity);
        this.onEntityAdded(entity);
        entity.getComponent(MaterialComponent.class).remapped = false;
      }
    }

    pipeline.ubo().bind();

    for (String materialID : this.sorted.keySet())
    {
      ArrayList<AbstractMap.SimpleEntry<Entity, String>> meshes = this.sorted.get(materialID);

      Material material = AssetManager.request(materialID, Material.class);
      material.bind();

      for (AbstractMap.SimpleEntry<Entity, String> mesh : meshes)
      {
        // key is entity
        material.shader().setUniform("u_model", SceneGraph.transform(mesh.getKey()));

        // value is mesh id
        VertexArray vao = AssetManager.request(mesh.getValue(), VertexArray.class);
        vao.bind();

        if (vao.indexed())
        {
          glDrawElements(GL_TRIANGLES, vao.count(), GL_UNSIGNED_INT, GL_NONE);
        }
        else
        {
          glDrawArrays(GL_TRIANGLES, 0, vao.count());
        }

        AssetManager.release(vao, VertexArray.class);
      }

      AssetManager.release(material, Material.class);
    }
  }

  public SimpleMeshRenderer() {}
}
