package engine.core.entity.system.rendering;

import engine.core.entity.Entity;
import engine.core.entity.component.EntityComponent;
import engine.core.entity.component.TransformComponent;
import engine.core.entity.component.lighting.DirectionalLightSourceComponent;
import engine.core.entity.system.IRenderSystem;
import engine.core.entity.system.UpdateSystem;
import engine.core.entity.system.rendering.shadow.ShadowMapSystem;
import engine.core.gfx.Shader;
import engine.core.gfx.UniformBuffer;
import engine.core.gfx.VertexArray;
import engine.core.gfx.shadow.ShadowMapBuffer;
import engine.core.rendering.DeferredRenderer;
import engine.core.rendering.GBuffer;
import engine.core.rendering.RenderStage;
import engine.core.scene.Scene;
import org.joml.Vector3f;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL42C.*;

public class DirectionalLightingSystem extends UpdateSystem implements IRenderSystem
{
  private static final int MAX_DIRECTIONAL_LIGHTS = 256;
  private static final int DIRECTIONAL_LIGHT_BYTES = 4 * UniformBuffer.OFFSET_VEC4F;// + UniformBuffer.N_BYTES;

  private Shader quad;
  private UniformBuffer lights;

  @Override
  public <T extends EntityComponent> Class<T>[] components()
  {
    return new Class[]
      {
        DirectionalLightSourceComponent.class,
        TransformComponent.class
      };
  }

  @Override
  public RenderStage priority()
  {
    return RenderStage.DEFERRED_LIGHTING_PASS;
  }

  @Override
  public void render(Scene scene, ArrayList<Entity> entities)
  {
    this.quad.setUniform("u_gbuffer.position", GBuffer.UNIFORM_POSITION_TEXTURE_BUFFER_POSITION);
    this.quad.setUniform("u_gbuffer.normal", GBuffer.UNIFORM_NORMAL_TEXTURE_BUFFER_POSITION);
    this.quad.setUniform("u_gbuffer.albedo", GBuffer.UNIFORM_ALBEDO_TEXTURE_BUFFER_POSITION);
    this.quad.setUniform("u_gbuffer.roughness_metallic_ao", GBuffer.UNIFORM_RGH_MTL_AO_TEXTURE_BUFFER_POSITION);
    this.quad.setUniform("u_lsm", ShadowMapSystem.lsm());

    ((DeferredRenderer) scene.getRenderer()).getSBuffer().bind(this.quad);

    this.quad.bind();

    // A directional light is represented as a fullscreen quad being drawn. This quad is constant.
    // The quad is created in the vertex shader, so all that we need to do is bind an empty vertex array and
    // draw six vertices, the vertex shader handles everything else internally.

    int i = 0;
    for (Entity entity : entities)
    {
      Vector3f position = entity.get(TransformComponent.class).position;
      DirectionalLightSourceComponent data = entity.get(DirectionalLightSourceComponent.class);

      this.lights.setUniform(position,        i * DIRECTIONAL_LIGHT_BYTES                                 );
      this.lights.setUniform(data.direction,  i * DIRECTIONAL_LIGHT_BYTES +     UniformBuffer.OFFSET_VEC4F);
      this.lights.setUniform(data.color,      i * DIRECTIONAL_LIGHT_BYTES + 2 * UniformBuffer.OFFSET_VEC4F);
      this.lights.setUniform(data.shadowIndex,i * DIRECTIONAL_LIGHT_BYTES + 3 * UniformBuffer.OFFSET_VEC4F);
      //this.lights.setUniform(data.clq,        i * DIRECTIONAL_LIGHT_BYTES + 3 * UniformBuffer.OFFSET_VEC4F);

      i++;
    }

    // set light amount
    this.lights.setUniform(i, MAX_DIRECTIONAL_LIGHTS * DIRECTIONAL_LIGHT_BYTES);

    VertexArray.empty.bind();
    VertexArray.postRenderPass();
    glDrawArrays(GL_TRIANGLES, 0, 6);
    VertexArray.preRenderPass();
  }

  @Override
  public void update(Scene scene, ArrayList<Entity> entities) {}

  @Override
  public void added(Entity entity) {}

  public DirectionalLightingSystem()
  {
    this.quad = Shader.getInstance("deferred/directional_lighting");
    this.lights = new UniformBuffer(
      UniformBuffer.N_BYTES +
      DIRECTIONAL_LIGHT_BYTES * MAX_DIRECTIONAL_LIGHTS,
      2
    );
  }
}
