package engine.core.entity.system.rendering.shadow;

import engine.Engine;
import engine.core.Input;
import engine.core.entity.Entity;
import engine.core.entity.component.EntityComponent;
import engine.core.entity.component.MeshComponent;
import engine.core.entity.component.ProjectionComponent;
import engine.core.entity.component.TransformComponent;
import engine.core.entity.component.lighting.DirectionalLightSourceComponent;
import engine.core.entity.component.shadow.ShadowSourceComponent;
import engine.core.entity.system.IRenderSystem;
import engine.core.entity.system.UpdateSystem;
import engine.core.gfx.Shader;
import engine.core.gfx.VertexArray;
import engine.core.rendering.RenderStage;
import engine.core.scene.Scene;
import engine.core.scene.SceneGraph;
import engine.util.settings.Settings;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_O;
import static org.lwjgl.opengl.GL11C.*;

public class ShadowMapSystem extends UpdateSystem implements IRenderSystem
{
  private ArrayList<Entity> enabled;
  private Shader shader;

  private static Matrix4f lsm;

  @Override
  public RenderStage priority() { return RenderStage.DEFERRED_SHADOW_PASS; }

  @Override
  public <T extends EntityComponent> Class<T>[] components()
  {
    return new Class[]
      {
        TransformComponent.class,
        ShadowSourceComponent.class,
        DirectionalLightSourceComponent.class
      };
  }

  public static Matrix4f lsm()
  {
    return lsm;
  }

  @Override
  public void render(Scene scene, ArrayList<Entity> entities)
  {
    // bind depth shader
    this.shader.bind();
    int layer = 0;

    //glEnable(GL_CULL_FACE);
    //glCullFace(GL_FRONT);
    glEnable(GL_DEPTH_TEST);
    glEnable(GL_CULL_FACE);

    for (Entity entity : this.enabled)
    {
      // construct orthographic light-space matrix
      lsm.identity()
        .ortho(-25, 25, -25, 25, 0.1f, 350.0f)
        .lookAt(
          new Vector3f(entity.get(TransformComponent.class).position).mul(0.1f),
          //new Vector3f(entity.get(DirectionalLightSourceComponent.class).direction).mul(100.0f),
          new Vector3f(0.0f),
          new Vector3f(0.0f, 1.0f, 0.0f)
        );

      this.shader.setUniform("u_layer", layer);
      this.shader.setUniform("u_light_space", lsm);

      // render each shadow map to a layer of the array texture
      for (Entity caster : scene.ecs().getSystemEntities(ShadowCasterCollection.class))
      {
        if (caster.get(MeshComponent.class).culling == GL_BACK)
        {
          glEnable(GL_CULL_FACE);
          glCullFace(GL_FRONT);
        }
        else
        {
          glDisable(GL_CULL_FACE);
          glCullFace(GL_BACK);
        }

        this.shader.setUniform("u_model", SceneGraph.constructTransform(caster));
        for (VertexArray mesh : caster.get(MeshComponent.class).mesh)
        {
          mesh.render();
        }
      }

      layer++;
    }

    glCullFace(GL_BACK);
    glDisable(GL_CULL_FACE);
  }

  @Override
  public void update(Scene scene, ArrayList<Entity> entities)
  {
    // clear the list of enabled shadow sources, and repopulate based on a condition (distance player <-> source)
    this.enabled.clear();
    int i = 0;
    for (Entity entity : entities)
    {
      float distance = entity.get(TransformComponent.class).position.distance(scene.getPlayer().getPosition());
      if (distance < entity.get(ShadowSourceComponent.class).distance)
      {
        if (this.enabled.size() < Settings.geti("Max2DShadowMaps"))
        {
          this.enabled.add(entity);
          entity.get(DirectionalLightSourceComponent.class).shadowIndex = i;
          i++;
        }
        else
        {
          entity.get(DirectionalLightSourceComponent.class).shadowIndex = -1;
          break;
        }
      }
      else
      {
        entity.get(DirectionalLightSourceComponent.class).shadowIndex = -1;
      }
    }
  }

  @Override
  public void added(Entity entity) {}

  public ShadowMapSystem()
  {
    this.enabled = new ArrayList<>();
    this.shader = Shader.getInstance("depth");
    this.lsm = new Matrix4f();
  }
}
