package engine.core.entity.system.rendering;

import engine.Engine;
import engine.core.entity.Entity;
import engine.core.entity.component.*;
import engine.core.entity.component.lighting.DirectionalLightSourceComponent;
import engine.core.entity.component.lighting.PointLightSourceComponent;
import engine.core.entity.system.IRenderSystem;
import engine.core.entity.system.UpdateSystem;
import engine.core.gfx.FrameBuffer;
import engine.core.gfx.Shader;
import engine.core.gfx.VertexArray;
import engine.core.gfx.texture.Texture;
import engine.core.rendering.RenderStage;
import engine.core.scene.Scene;
import engine.core.scene.SceneGraph;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL30C.*;

public class SSRRenderingSystem extends UpdateSystem implements IRenderSystem
{
  public Texture buffer;

  public FrameBuffer fbo;

  private Shader shader;

  @Override
  public RenderStage priority()
  {
    return RenderStage.FORWARD_PASS;
  }

  @Override
  public void render(Scene scene, ArrayList<Entity> entities)
  {
    this.fbo.bind();
    glDrawBuffers(new int[] { GL_COLOR_ATTACHMENT0 });
    System.out.println(glGetError());
    System.out.println(glGetError());
    this.fbo.clear();

    glEnable(GL_DEPTH_TEST);
    this.shader.bind();

    for (Entity entity : entities)
    {
      if (entity.has(PointLightSourceComponent.class) || entity.has(DirectionalLightSourceComponent.class))
      {
        this.shader.setUniform("u_emissive", 1);
      }
      else
      {
        this.shader.setUniform("u_emissive", 0);
      }

      this.shader.setUniform("u_model", SceneGraph.constructTransform(entity));

      for (VertexArray vao : entity.get(MeshComponent.class).mesh)
      {
        vao.render();
      }
    }

    this.shader.unbind();

    this.fbo.unbind();
  }

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
  public void update(Scene scene, ArrayList<Entity> entities)
  {

  }

  @Override
  public void added(Entity entity) {}

  public SSRRenderingSystem()
  {
    this.fbo = new FrameBuffer(Engine.window.getWidth(), Engine.window.getHeight());
    this.shader = Shader.getInstance("effects/ssr");

    this.fbo.bind();
    this.buffer = new Texture(Engine.window.getWidth(), Engine.window.getHeight(), GL_RGB16F, GL_RGB, GL_FLOAT);
    this.fbo.addTexture(this.buffer, GL_COLOR_ATTACHMENT0);
    this.fbo.addDepthTexture();
  }
}
