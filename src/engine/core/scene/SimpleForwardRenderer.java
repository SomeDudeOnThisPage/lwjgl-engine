package engine.core.scene;

import engine.Engine;
import engine.core.entity.Entity;
import engine.core.entity.component.ControlComponent;
import engine.core.entity.component.ProjectionComponent;
import engine.core.entity.component.TransformComponent;
import engine.core.gfx.Shader;
import engine.core.gfx.VertexArray;
import engine.core.gfx.batching.DeferredMeshBatcher;
import org.joml.Matrix4f;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.lwjgl.opengl.GL43C.*;

public class SimpleForwardRenderer// extends Renderer
{
  /*private Entity camera;
  private Shader shader;

  @Override
  public void onScreenSizeChange(int x, int y)
  {
    this.camera.get(ProjectionComponent.class).projection.identity().perspective(1.04f, (float) x / (float) y, 0.1f, 1000.0f);
    this.uniforms.setUniform(this.camera.get(ProjectionComponent.class).projection, 0);
  }

  @Override
  public void render() {

  }

  @Override
  public void before(Scene scene)
  {
    DeferredMeshBatcher.begin();
    //VertexArray.preRenderPass();
    this.uniforms.bind();
  }

  @Override
  public void screen(Scene scene)
  {
    DeferredMeshBatcher.end();

    this.shader.bind();

    glMultiDrawElementsIndirect(
      GL_TRIANGLES,
      GL_UNSIGNED_INT,
      0,
      DeferredMeshBatcher.getNumDrawCalls(),
      0
    );

    System.out.println(DeferredMeshBatcher.getNumDrawCalls());

    this.shader.unbind();
  }

  @Override
  public void after(Scene scene)
  {
    //VertexArray.postRenderPass();
  }

  public SimpleForwardRenderer()
  {
    this.setupDefaultUniformBuffer();

    // setup camera entity
    this.camera = new Entity();
    this.camera.addEntity(new TransformComponent());
    this.camera.addEntity(new ProjectionComponent(60.0f, 800.0f / 600.0f, 0.1f, 1000.0f));
    this.camera.addEntity(new ControlComponent(true));
    this.camera.get(ProjectionComponent.class).projection = new Matrix4f().perspective(1.04f, (float) Engine.window.getWidth() / (float)  Engine.window.getHeight(), 0.1f, 1000.0f);

    this.shader = Shader.getInstance("base_color");
    this.onScreenSizeChange(Engine.window.getWidth(), Engine.window.getHeight());

    DeferredMeshBatcher.initialize();
  }*/
}
