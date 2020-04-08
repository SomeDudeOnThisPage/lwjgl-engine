package engine.core.entity.system;

import engine.core.entity.Entity;
import engine.core.entity.component.EntityComponent;
import engine.core.entity.component.SkyboxComponent;
import engine.core.gfx.Shader;
import engine.core.rendering.RenderStage;
import engine.core.scene.Scene;
import org.joml.Matrix4f;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL30C.*;

public class SkyboxSystem extends UpdateSystem implements IRenderSystem
{
  private Shader shader;

  @Override
  public <T extends EntityComponent> Class<T>[] components()
  {
    return new Class[]
    {
      SkyboxComponent.class
    };
  }

  @Override
  public void update(Scene scene, ArrayList<Entity> entities) {}

  @Override
  public void added(Entity entity) {}

  @Override
  public RenderStage priority()
  {
    return RenderStage.FORWARD_PASS;
  }

  @Override
  public void render(Scene scene, ArrayList<Entity> entities)
  {
    this.shader.bind();

    for (Entity entity : entities)
    {
      glDepthMask(false);
      glCullFace(GL_FRONT);
      entity.get(SkyboxComponent.class).texture.bind(0);
      this.shader.setUniform("u_model", new Matrix4f().scale(250.0f));
      this.shader.setUniform("skybox", 0);
      SkyboxComponent.mesh.render();
      glCullFace(GL_BACK);
      glDepthMask(true);
    }
  }

  public SkyboxSystem()
  {
    this.shader = Shader.getInstance("skybox");
  }
}
