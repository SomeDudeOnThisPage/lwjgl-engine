package engine.core.entity.system.rendering;

import engine.core.entity.Entity;
import engine.core.entity.component.EntityComponent;
import engine.core.entity.component.TransformComponent;
import engine.core.entity.component.lighting.PointLightSourceComponent;
import engine.core.entity.system.IRenderSystem;
import engine.core.entity.system.UpdateSystem;
import engine.core.gfx.Shader;
import engine.core.gfx.UniformBuffer;
import engine.core.gfx.VertexArray;
import engine.core.rendering.GBuffer;
import engine.core.rendering.RenderStage;
import engine.core.scene.Scene;
import engine.core.scene.SceneGraph;
import engine.util.settings.Settings;
import org.joml.Vector3f;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL42C.*;

public class PointLightSystem extends UpdateSystem implements IRenderSystem
{
  private static final int POINT_LIGHT_BYTES = 3 * UniformBuffer.OFFSET_VEC4F;

  private Shader shader;
  private UniformBuffer lights;

  @Override
  public <T extends EntityComponent> Class<T>[] components()
  {
    return new Class[]
      {
        TransformComponent.class,
        PointLightSourceComponent.class
      };
  }

  @Override
  public void added(Entity entity) {}

  @Override
  public RenderStage priority()
  {
    return RenderStage.DEFERRED_LIGHTING_PASS;
  }

  @Override
  public void render(Scene scene, ArrayList<Entity> entities)
  {
    this.shader.setUniform("u_gbuffer.position", GBuffer.UNIFORM_POSITION_TEXTURE_BUFFER_POSITION);
    this.shader.setUniform("u_gbuffer.normal", GBuffer.UNIFORM_NORMAL_TEXTURE_BUFFER_POSITION);
    this.shader.setUniform("u_gbuffer.albedo", GBuffer.UNIFORM_ALBEDO_TEXTURE_BUFFER_POSITION);
    this.shader.setUniform("u_gbuffer.roughness_metallic_ao", GBuffer.UNIFORM_RGH_MTL_AO_TEXTURE_BUFFER_POSITION);
    this.shader.bind();

    int i = 0;
    Vector3f position = new Vector3f();
    for (Entity entity : entities)
    {
      SceneGraph.constructTransform(entity).getTranslation(position);
      PointLightSourceComponent data = entity.get(PointLightSourceComponent.class);

      this.lights.setUniform(position,        i * POINT_LIGHT_BYTES                                 );
      this.lights.setUniform(data.color,      i * POINT_LIGHT_BYTES +     UniformBuffer.OFFSET_VEC4F);
      this.lights.setUniform(data.clq,        i * POINT_LIGHT_BYTES + 2 * UniformBuffer.OFFSET_VEC4F);

      // todo: shadow map indices

      i++;
    }

    this.lights.setUniform(i, Settings.geti("MaxPointLights") * POINT_LIGHT_BYTES);

    VertexArray.empty.bind();
    VertexArray.postRenderPass();
    glDrawArrays(GL_TRIANGLES, 0, 6);
    VertexArray.preRenderPass();
  }

  @Override
  public void update(Scene scene, ArrayList<Entity> entities) {}

  public PointLightSystem()
  {
    this.shader = Shader.getInstance("deferred/point_lighting");
    this.lights = new UniformBuffer(
      UniformBuffer.N_BYTES +
        POINT_LIGHT_BYTES * Settings.geti("MaxPointLights"),
      1
    );
  }
}
