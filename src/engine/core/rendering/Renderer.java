package engine.core.rendering;

import engine.Engine;
import engine.core.entity.component.ProjectionComponent;
import engine.core.entity.component.TransformComponent;
import engine.core.gfx.UniformBuffer;
import engine.core.scene.Scene;
import engine.core.scene.SceneGraph;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public abstract class Renderer
{
  private static Vector3f position = new Vector3f();

  /**
   * Scene this renderer is assigned to.
   */
  protected UniformBuffer uniforms;

  public void onScreenSizeChanged(int x, int y) {}

  protected void setupDefaultUniformBuffer()
  {
    this.uniforms = new UniformBuffer(
      /* Matrix4f */ 16 * Float.BYTES * 2 +
      /* View Position */ 4 * Float.BYTES +
      /* Screen Size */ 4 * Float.BYTES
      , 7);
  }

  public final void setCamera(Camera camera)
  {
    this.uniforms.setUniform(camera.get(ProjectionComponent.class).projection, 0);
    this.uniforms.setUniform(SceneGraph.constructTransform(camera).invert(), UniformBuffer.OFFSET_VIEW_MATRIX);
    this.uniforms.setUniform(SceneGraph.constructTransform(camera).getTranslation(Renderer.position).negate(), UniformBuffer.OFFSET_VIEW_POSITION);

    this.uniforms.setUniform(new Vector4f(Engine.window.getWidth(), Engine.window.getHeight(), 0.0f, 0.0f), UniformBuffer.OFFSET_SCREEN_SIZE);
  }

  public UniformBuffer getUniformBuffer()
  {
    return this.uniforms;
  }

  public abstract void render(Scene scene);
}