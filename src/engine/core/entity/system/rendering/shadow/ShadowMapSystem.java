package engine.core.entity.system.rendering.shadow;

import engine.Engine;
import engine.core.Input;
import engine.core.entity.Entity;
import engine.core.entity.component.EntityComponent;
import engine.core.entity.component.MeshComponent;
import engine.core.entity.component.TransformComponent;
import engine.core.entity.component.lighting.DirectionalLightSourceComponent;
import engine.core.entity.component.shadow.ShadowSourceComponent;
import engine.core.entity.system.IRenderSystem;
import engine.core.entity.system.UpdateSystem;
import engine.core.gfx.Shader;
import engine.core.gfx.VertexArray;
import engine.core.gfx.batching.AssetManager;
import engine.core.rendering.DeferredRenderer;
import engine.core.rendering.RenderStage;
import engine.core.scene.Player;
import engine.core.scene.Scene;
import engine.core.scene.SceneGraph;
import engine.util.settings.Settings;
import org.joml.*;

import java.lang.Math;
import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_K;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_L;
import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL20C.glDrawBuffers;
import static org.lwjgl.opengl.GL30C.GL_COLOR_ATTACHMENT0;

public class ShadowMapSystem extends UpdateSystem implements IRenderSystem
{
  private ArrayList<Entity> enabled;
  private Shader shader;

  private static Matrix4f lsm;

  @Override
  public RenderStage priority() { return RenderStage.DEFERRED_SHADOW_PASS; }

  @Override
  public <T extends EntityComponent> Class<T>[] components()
  {
    return new Class[]
      {
        TransformComponent.class,
        ShadowSourceComponent.class,
        DirectionalLightSourceComponent.class
      };
  }

  public static Matrix4f lsm()
  {
    return lsm;
  }

  protected Vector3f getCenter(Vector3f min, Vector3f max, Matrix4f view)
  {
    float x = (min.x - max.x) / 2.0f;
    float y = (min.y - max.y) / 2.0f;
    float z = (min.z - max.z) / 2.0f;
    Vector4f center = new Vector4f(x, y, z, 1);
    Matrix4f inverted = new Matrix4f(view).invert();
    Vector4f transform = inverted.transform(center);

    System.err.println(transform.x + " " + transform.y + " " + transform.z);

    return new Vector3f(transform.x, transform.y, transform.z);
  }

  private float lol = 25.0f;

  @Override
  public void render(Scene scene, ArrayList<Entity> entities)
  {
    // clear the list of enabled shadow sources, and repopulate based on a condition (distance player <-> source)
    this.enabled.clear();
    int i = 0;
    for (Entity entity : entities)
    {
      float distance = entity.get(TransformComponent.class).position.distance(scene.getPlayer().getPosition());
      if (distance < entity.get(ShadowSourceComponent.class).distance)
      {
        if (this.enabled.size() < Settings.geti("Max2DShadowMaps"))
        {
          this.enabled.add(entity);
          entity.get(DirectionalLightSourceComponent.class).shadowIndex = i;
          i++;
        }
        else
        {
          entity.get(DirectionalLightSourceComponent.class).shadowIndex = -1;
          break;
        }
      }
      else
      {
        entity.get(DirectionalLightSourceComponent.class).shadowIndex = -1;
      }
    }

    // bind depth shader
    this.shader.bind();
    int layer = 0;

    glEnable(GL_DEPTH_TEST);
    glEnable(GL_CULL_FACE);

    float perspectiveLength = 25.0f;

    Matrix4f projection = scene.getPlayer().getCamera().getProjection();
    Matrix4f test = new Matrix4f().perspective(
      projection.perspectiveFov(),
      (float) Engine.window.getWidth() / (float) Engine.window.getHeight(),
      projection.perspectiveNear(),
      perspectiveLength
    );
    test.mul(new Matrix4f(scene.getPlayer().getCamera().getView()));

    Vector3f min = new Vector3f();
    Vector3f max = new Vector3f();

    test.invert().frustumAabb(min, max);

    float width = (max.x - min.x) / 2.0f;
    float height = (max.y - min.y) / 2.0f;
    float length = (max.z - min.z) / 2.0f;

    if (Input.keyDown(GLFW_KEY_L))
    {
      lol += 0.1f;
    }
    if (Input.keyDown(GLFW_KEY_K))
    {
      lol -= 0.1f;
    }

    for (Entity entity : this.enabled)
    {
      // construct orthographic light-space matrix
      Vector3f position = scene.getPlayer().getCamera().getPosition().negate();

      ShadowMapSystem.lsm.identity()
        .ortho(-25, 25, -25, 25, -25, 25)
        //.rotateLocalX(-1.04f)
        //.translate(position);
        .lookAt(
          //getCenter(min, max, scene.getPlayer().getCamera().getView()),
          //new Vector3f(scene.getPlayer().getPosition()),
          new Vector3f(scene.getPlayer().getCamera().getPosition()),
          new Vector3f(entity.get(DirectionalLightSourceComponent.class).direction).mul(1000.0f),
          new Vector3f(0.0f, 1.0f, 0.0f)
        );

      this.shader.setUniform("u_layer", layer);
      this.shader.setUniform("u_light_space", lsm);

      glDisable(GL_CULL_FACE);

      // render each shadow map to a layer of the array texture
      for (Entity caster : scene.ecs().getSystemEntities(ShadowCasterCollection.class))
      {
        this.shader.setUniform("u_model", SceneGraph.constructTransform(caster));
        for (VertexArray mesh : caster.get(MeshComponent.class).mesh)
        {
          mesh.render();
        }
      }

      ((DeferredRenderer) scene.getRenderer()).getSBuffer().blur(layer);

      layer++;
    }

    glCullFace(GL_BACK);
    glDisable(GL_CULL_FACE);
  }

  @Override
  public void update(Scene scene, ArrayList<Entity> entities) {}

  @Override
  public void added(Entity entity) {}

  public ShadowMapSystem()
  {
    this.enabled = new ArrayList<>();
    this.shader = Shader.getInstance("depth");
    lsm = new Matrix4f();
  }
}
