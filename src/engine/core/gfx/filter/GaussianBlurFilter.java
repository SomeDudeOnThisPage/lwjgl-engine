package engine.core.gfx.filter;

import engine.Engine;
import engine.core.gfx.texture.Texture;
import engine.core.gfx.texture.TextureArray2D;
import engine.core.rendering.DeferredRenderer;
import engine.util.settings.Settings;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL42C.*;

public final class GaussianBlurFilter extends Filter
{
  private float scale;
  private Filter filter7x1;

  @Override
  public void apply()
  {
    ((DeferredRenderer) Engine.scene_manager.getScene().getRenderer()).getSBuffer().bind(this);
  }

  public void applyLayer(Texture texture, int attachment0, Texture temp, int attachment1)
  {
    glDisable(GL_DEPTH_TEST);

    glDrawBuffers(attachment1);
    texture.bind(Filter.FILTER_COLOR_BUFFER_BINDING);
    this.filter7x1.bind();
      this.filter7x1.setUniform("u_scale", new Vector3f(1.0f / texture.width() * scale, 0.0f, 0.0f));
      Filter.pass();
    this.filter7x1.unbind();
    texture.unbind();

    temp.bind(Filter.FILTER_COLOR_BUFFER_BINDING);
    glDrawBuffers(attachment0);
    this.bind();
      this.setUniform("u_scale", new Vector3f(0.0f, 1.0f / texture.width() * scale, 0.0f));
      Filter.pass();
    this.unbind();

    glEnable(GL_DEPTH_TEST);
  }

  public GaussianBlurFilter(float scale)
  {
    super("gaussian_blur");
    this.filter7x1 = new Filter("gaussian_blur_7x1");
    this.scale = scale;
  }
}
