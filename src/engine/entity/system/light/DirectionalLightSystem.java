package engine.entity.system.light;

import engine.Console;
import engine.entity.*;
import engine.entity.component.light.DirectionalLight;
import engine.gfx.buffer.UniformBuffer;
import engine.gfx.uniform.BufferedUniform;
import engine.gfx.uniform.BufferedUniformArray;
import engine.gfx.uniform.BufferedUniformStruct;
import engine.gfx.uniform.GLUniformBuffer;
import engine.scene.Scene;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;

public class DirectionalLightSystem extends EntityCollection implements EntitySystem
{
  private static UniformBuffer ubo;

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
      DirectionalLight.class
    };
  }

  @Override
  public void update(Scene scene)
  {
    int i = 0;
    for (Entity entity : this.entities)
    {
      BufferedUniform<Vector4f> dir = (BufferedUniform<Vector4f>) ubo.getUniform("u_directional_light[" + i + "].dir");
      BufferedUniform<Vector4f> col = (BufferedUniform<Vector4f>) ubo.getUniform("u_directional_light[" + i + "].col");

      DirectionalLight light = entity.getComponent(DirectionalLight.class);

      if (!light.flagged(EntityComponent.Flags.INACTIVE))
      {
        dir.set(new Vector4f(light.direction.x, light.direction.y, light.direction.z, 0.0f));
        col.set(new Vector4f(light.color.x, light.color.y, light.color.z, 0.0f));

        ubo.update("u_directional_light[" + i + "].dir");
        ubo.update("u_directional_light[" + i + "].col");
        i++;

        if ((Boolean) Console.getConVar("debug_lights").get())
        {
        /*
          Vector2f p = MathUtil.worldToWindow(scene, scene.getEntity("editor-camera"), pos.get());
          ImGui.setNextWindowPos(p.x, p.y, ImGuiCond.Always);
          ImGui.begin("###lool");
          ImGui.text("Soose");
          ImGui.end();
        */
        }
      }
    }

    BufferedUniform<Integer> num = (BufferedUniform<Integer>) ubo.getUniform("u_num_directional_lights");
    num.set(i);
    ubo.update("u_num_directional_lights");
  }

  public DirectionalLightSystem()
  {
    if (DirectionalLightSystem.ubo == null)
    {
      ArrayList<BufferedUniform<?>> uniforms = new ArrayList<>();
      for (int i = 0; i < 64; i++)
      {
        uniforms.add(new BufferedUniformStruct<>("",
          new BufferedUniform<>("dir", new Vector3f(0.0f, -1.0f, 0.0f)),
          new BufferedUniform<>("col", new Vector3f(1.0f))
        ));
      }

      DirectionalLightSystem.ubo = new GLUniformBuffer(
        1, // todo: make the binding point dynamic
        new BufferedUniformArray<>("u_directional_light", 64, 16 * 2, uniforms),
        new BufferedUniform<>("u_num_directional_lights", 0)
      );
    }
  }
}
