package engine.entity.system.light;

import engine.Console;
import engine.Engine;
import engine.entity.*;
import engine.entity.component.Transform;
import engine.entity.component.light.PointLight;
import engine.gfx.Texture2D;
import engine.gfx.buffer.UniformBuffer;
import engine.gfx.uniform.BufferedUniform;
import engine.gfx.uniform.BufferedUniformArray;
import engine.gfx.uniform.BufferedUniformStruct;
import engine.gfx.uniform.GLUniformBuffer;
import engine.scene.Scene;
import engine.scene.SceneGraph;
import engine.util.MathUtil;
import imgui.ImDrawList;
import imgui.ImGui;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;

import static engine.Engine.AssetManager;

public class PointLightSystem extends EntityCollection implements EntitySystem
{
  private static UniformBuffer ubo;

  private final Texture2D pointlight;

  @Override
  public EntityComponentSystem.SystemPriority priority()
  {
    return EntityComponentSystem.SystemPriority.LEVEL_1;
  }

  @Override
  public <T extends EntityComponent> Class<T>[] components()
  {
    return new Class[]
      {
        Transform.class,
        PointLight.class
      };
  }

  @Override
  public void update(Scene scene)
  {
    int i = 0;
    ImDrawList list = ImGui.getBackgroundDrawList();

    // rebuild the uniform buffer holding the point lights
    // todo: make this a mapped buffer to improve performance (persistent double buffered || orphaning strategy?)
    for (Entity entity : this.entities)
    {
      PointLight light = entity.getComponent(PointLight.class);
      if (!light.flagged(EntityComponent.Flags.INACTIVE))
      {
        // set uniforms at the specific positions
        BufferedUniform<Vector4f> pos = (BufferedUniform<Vector4f>) ubo.getUniform("u_point_light[" + i + "].pos");
        BufferedUniform<Vector4f> col = (BufferedUniform<Vector4f>) ubo.getUniform("u_point_light[" + i + "].col");
        BufferedUniform<Vector4f> clq = (BufferedUniform<Vector4f>) ubo.getUniform("u_point_light[" + i + "].clq");

        Vector3f p = SceneGraph.transform(entity).getTranslation(new Vector3f());

        pos.set(new Vector4f(p.x, p.y, p.z, 0.0f));
        col.set(new Vector4f(light.color.x, light.color.y, light.color.z, 0.0f));
        clq.set(new Vector4f(light.clq.x, light.clq.y, light.clq.z, light.clq.z * light.clq.z));

        // update uniforms
        ubo.update("u_point_light[" + i + "].pos");
        ubo.update("u_point_light[" + i + "].col");
        ubo.update("u_point_light[" + i + "].clq");
        i++;

        if ((Boolean) Console.getConVar("debug_lights").get())
        {
          Entity camera = scene.getEntity("editor-camera"); // todo: use scene camera
          Vector3f campos = SceneGraph.transform(camera).getTranslation(new Vector3f());
          float max = (Float) Console.getConVar("point_light_img_fadeout_dist").get();

          if (max <= 0 || campos.distance(p) < max)
          {
            Vector2f pos3d = MathUtil.worldToWindow(scene, scene.getEntity("editor-camera"), p);
            list.addImage(this.pointlight.id(), pos3d.x - 20, pos3d.y - 20, pos3d.x + 20, pos3d.y + 20);
          }
        }

      }
    }

    // don't forget to set the current total amount of active lights
    BufferedUniform<Integer> num = (BufferedUniform<Integer>) ubo.getUniform("u_num_point_lights");
    num.set(i);
    ubo.update("u_num_point_lights");
  }

  @Override
  public void onCollectionRemoved()
  {
    AssetManager.release(this.pointlight, Texture2D.class);
  }

  public PointLightSystem()
  {
    if (PointLightSystem.ubo == null)
    {
      ArrayList<BufferedUniform<?>> uniforms = new ArrayList<>();
      for (int i = 0; i < 512; i++)
      {
        uniforms.add(new BufferedUniformStruct<>("",
          new BufferedUniform<>("pos", new Vector3f(1.0f)),
          new BufferedUniform<>("col", new Vector3f(0.0f)),
          new BufferedUniform<>("clq", new Vector3f(0.0f))
        ));
      }

      PointLightSystem.ubo = new GLUniformBuffer(
        2, // todo: make the binding point dynamic
        new BufferedUniformArray<>("u_point_light", 512, 16 * 3, uniforms),
        new BufferedUniform<>("u_num_point_lights", 0)
      );
    }

    // load point light image
    AssetManager.load("editor_gui_point_light.mtl");

    // equivalent to 'AssetManager.getFamily(Texture2D.class).request("editor.pointlight")'
    this.pointlight = AssetManager.request("editor.pointlight", Texture2D.class);

    Console.addConVar("point_light_img_fadeout_dist", new Console.ConVar<>(50.0f));
    Console.addCommand("plights", args ->
      Console.print("Number of active point lights: " + ubo.getUniform("u_num_point_lights").get()));
  }
}
