package engine.render.deferred;

import engine.Engine;
import engine.asset.AssetManager;
import engine.entity.Camera;
import engine.gfx.Texture2D;
import engine.gfx.buffer.FrameBuffer;
import engine.gfx.buffer.UniformBuffer;
import engine.gfx.opengl.buffer.GLFrameBuffer;
import engine.gfx.uniform.BufferedUniform;
import engine.gfx.uniform.GLUniformBuffer;
import engine.render.Pipeline;
import engine.render.RenderStage;
import engine.render.Viewport;
import engine.scene.Scene;
import engine.ui.DebugGUI;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL11C.*;

public class DeferredRenderer implements Pipeline
{
  private Camera camera;
  private final UniformBuffer ubo;
  private final FrameBuffer gbuffer;

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

    this.gbuffer.bind();

    this.gbuffer.clear(FrameBuffer.BIT_FLAGS.COLOR, FrameBuffer.BIT_FLAGS.DEPTH);
    scene.ecs().render(RenderStage.DEFERRED_GEOMETRY_PASS, this);

    this.gbuffer.unbind();
  }

  public DeferredRenderer()
  {
    // initialize shared uniform buffer
    this.ubo = new GLUniformBuffer(
      0,
      new BufferedUniform<>("u_projection", new Matrix4f().identity()),
      new BufferedUniform<>("u_view", new Matrix4f().identity()),
      new BufferedUniform<>("u_view_position", new Vector4f())
    );

    // load assets required for geometry buffer
    Engine.AssetManager.load("gbuffer.ppl");

    // initialize geometry buffer // todo: use framebuffer factory with data defined in .ppl file
    this.gbuffer = new GLFrameBuffer(new Vector2i().set(Engine.Display.size()));
    this.gbuffer.addTexture2D(AssetManager.getInstance().request("gbuffer.phong.position", Texture2D.class));
    this.gbuffer.addTexture2D(AssetManager.getInstance().request("gbuffer.phong.normal", Texture2D.class));
  }
}
