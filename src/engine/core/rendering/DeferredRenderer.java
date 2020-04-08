package engine.core.rendering;

import engine.Engine;
import engine.core.entity.system.rendering.SSRRenderingSystem;
import engine.core.gfx.filter.FXAAFilter;
import engine.core.gfx.filter.Filter;
import engine.core.gfx.FrameBuffer;
import engine.core.gfx.Shader;
import engine.core.gfx.VertexArray;
import engine.core.gfx.filter.FilterBuffer;
import engine.core.gfx.filter.ToneMapFilter;
import engine.core.gfx.texture.Texture;
import engine.core.scene.Scene;
import org.lwjgl.system.NonnullDefault;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL30C.*;

/**
 * Pipeline Description:
 *
 * 1. Geometry Pass
 *    - Written to the GBuffer
 * 2. Lighting Pass
 *    - Written to the GBuffers 4th Color Attachment
 * 3. Filter Pass
 *    - Programmable via XML
 *    - Filters are essentially full-screen quads rendered with specific shaders.
 *    - Shader-Inputs include:
 *      -> The final color texture of the GBuffer
 *      -> The depth-map of the GBuffer
 */
public class DeferredRenderer extends Renderer
{
  /**
   * Color-Buffer.
   * This buffer will contain the final image after the scene has been rendered.
   */
  private FrameBuffer cbuffer;

  private ArrayList<Filter> filters;

  /**
   * Geometry-Buffer.
   * This buffer is used to store the necessary data for the deferred lighting pass on the light-buffer.
   */
  private GBuffer gbuffer;

  private FilterBuffer fbuffer;

  private Shader pp;

  private FXAAFilter fxaa;
  private Filter fadein;
  private Filter tonemap;

  public GBuffer getGBuffer()
  {
    return this.gbuffer;
  }

  /**
   * Performs the geometry pass.
   */
  private void geometry(Scene scene)
  {
    glEnable(GL_DEPTH_TEST);

    // write to gbuffer
    this.gbuffer.clear();
    this.gbuffer.bind_geometry();
    scene.ecs().render(RenderStage.DEFERRED_GEOMETRY_PASS);
  }

  /**
   * Performs the lighting pass.
   */
  private void lighting(Scene scene)
  {
    this.gbuffer.bind_lighting();

    glEnable(GL_BLEND);
    glBlendEquation(GL_FUNC_ADD);
    glBlendFunc(GL_ONE, GL_ONE);
    glDisable(GL_DEPTH_TEST);

    scene.ecs().render(RenderStage.DEFERRED_LIGHTING_PASS);

    glEnable(GL_DEPTH_TEST);
    glDisable(GL_BLEND);
  }

  /**
   * Applies exposure, tonemapping and color correction to a texture bound at uniform location 'color' in the deferred.post_processing shader.
   */
  private void correct(Scene scene)
  {
    scene.ecs().render(RenderStage.FORWARD_PASS);

    /*this.pp.bind();
    this.gbuffer.bind_pp();
    //this.gbuffer.clear();
    this.gbuffer.bind(this.pp);
    this.pp.setUniform("color", GBuffer.UNIFORM_LIGHTING_TEXTURE_BUFFER_POSITION);

    // draw six vertices
    VertexArray.empty.bind();
    VertexArray.postRenderPass();
    glDrawArrays(GL_TRIANGLES, 0, 6);
    VertexArray.preRenderPass();

    this.pp.unbind();*/
    this.gbuffer.bind_pp();
  }

  private void filter(Scene scene)
  {
    glDisable(GL_DEPTH_TEST);
    glDisable(GL_BLEND);

    this.fbuffer.clear();
    this.fbuffer.bind();

    this.fbuffer.begin(this.gbuffer.getFinalTexture());

    this.fbuffer.apply(this.tonemap);
    this.fbuffer.apply(this.fxaa);
    this.fbuffer.apply(this.fadein);

    scene.ecs().render(RenderStage.FILTER_PASS);

    glEnable(GL_DEPTH_TEST);

    // todo: make this nicer, ideally without blitting

    glBindFramebuffer(GL_READ_FRAMEBUFFER, this.fbuffer.getID());
    glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);

    glReadBuffer(this.fbuffer.getCurrentAttachment());

    glBlitFramebuffer(0, 0, this.fbuffer.getWidth(), this.fbuffer.getHeight(),
                      0, 0, Engine.window.getWidth(), Engine.window.getHeight(),
                      GL_COLOR_BUFFER_BIT,
                      GL_NEAREST
    );
  }

  @Override
  public void render(Scene scene)
  {
    this.uniforms.bind();

    // bind player camera uniforms to the shared uniform buffer
    this.setCamera(scene.getPlayer().getCamera());
    VertexArray.preRenderPass();

    // call ecs render
    scene.ecs().render(RenderStage.BEFORE);

    if (Engine.WIREFRAME)
    {
      glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
    }

    this.geometry(scene);

    if (Engine.WIREFRAME)
    {
      glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
    }

    // this.shadow(scene);

    this.lighting(scene);

    this.correct(scene);

    this.gbuffer.unbind();

    //scene.ecs().render(RenderStage.SCREEN_PASS);

    this.filter(scene);

    // call ecs render
    scene.ecs().render(RenderStage.AFTER);

    VertexArray.postRenderPass();
    this.uniforms.unbind();
  }

  public DeferredRenderer()
  {
    this.setupDefaultUniformBuffer();

    this.pp = Shader.getInstance("deferred/post_processing");

    this.cbuffer = new FrameBuffer(Engine.window.getWidth(), Engine.window.getHeight());
    this.cbuffer.addTexture(
      new Texture(Engine.window.getWidth(), Engine.window.getHeight(), GL_RGBA16F, GL_RGB, GL_FLOAT),
      GL_COLOR_ATTACHMENT0
    );

    this.gbuffer = new GBuffer(Engine.window.getWidth(), Engine.window.getHeight());
    this.fbuffer = new FilterBuffer(Engine.window.getWidth(), Engine.window.getHeight());

    this.filters = new ArrayList<>();

    this.tonemap = new ToneMapFilter();
    this.fxaa = new FXAAFilter();
    this.fadein = new Filter("fadein");
  }
}
