package engine.render;

import engine.entity.Camera;
import engine.gfx.buffer.UniformBuffer;
import engine.gfx.uniform.BufferedUniform;
import engine.gfx.uniform.GLUniformBuffer;
import engine.scene.Scene;
import engine.ui.DebugGUI;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL42C.*;

public class ForwardRenderer implements Pipeline
{
  private Camera camera;

  private final UniformBuffer ubo;

  @Override
  public Camera camera()
  {
    return this.camera;
  }

  @Override
  public void camera(Camera camera)
  {
    this.camera = camera;
  }

  @Override
  public UniformBuffer ubo()
  {
    return this.ubo;
  }

  @Override
  public void render(Scene scene, Viewport viewport)
  {
    glViewport(viewport.position().x, viewport.position().y, viewport.size().x, viewport.size().y);
    glEnable(GL_DEPTH_TEST);
    glEnable(GL_CULL_FACE);
    glCullFace(GL_BACK);

    // can't get any simpler really
    scene.ecs().render(RenderStage.BEFORE, this);
    scene.ecs().render(RenderStage.FORWARD_PASS, this);
    scene.ecs().render(RenderStage.AFTER, this);
  }

  public ForwardRenderer()
  {
    this.ubo = new GLUniformBuffer(
      0,
      new BufferedUniform<>("u_projection", new Matrix4f().identity()),
      new BufferedUniform<>("u_view", new Matrix4f().identity()),
      new BufferedUniform<>("u_view_position", new Vector4f())
    );
  }
}
