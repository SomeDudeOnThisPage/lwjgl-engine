package editor.ui;

import engine.entity.component.MaterialComponent;
import engine.entity.component.Mesh;
import engine.Console;
import engine.entity.EntityComponent;
import engine.gfx.material.Material;
import imgui.*;
import imgui.enums.ImGuiInputTextFlags;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import static engine.Engine.*;

public interface EditorFieldDisplay
{
  /**
   * These are different ImGUI displays for different field types.
   */
  HashMap<Class<?>, EditorFieldDisplay> renderers = new HashMap<>()
  {{
    put(Float.TYPE, (field, component) ->
    {
      float f = field.getFloat(component);
      ImFloat ip = new ImFloat(f);
      ImGui.inputFloat(field.getName(), ip);
      field.setFloat(component, ip.get());
    });

    put(Double.TYPE, (field, component) ->
    {
      double d = field.getDouble(component);
      ImDouble ip = new ImDouble(d);
      ImGui.inputDouble(field.getName(), ip);
      field.setDouble(component, ip.get());
    });

    put(Integer.TYPE, (field, component) ->
    {
      int i = field.getInt(component);
      ImInt ip = new ImInt(i);
      ImGui.inputInt(field.getName(), ip);
      field.setInt(component, ip.get());
    });

    put(Boolean.TYPE, (field, component) ->
    {
      boolean b = field.getBoolean(component);
      ImBool ip = new ImBool(b);
      ImGui.checkbox(field.getName(), ip);
      field.setBoolean(component, ip.get());
    });

    put(Vector2f.class, (field, component) ->
    {
      Vector2f vec2 = ((Vector2f) field.get(component));
      float[] values = new float[] {vec2.x, vec2.y};
      ImGui.inputFloat2(field.getName(), values);
      vec2.set(values);
    });

    put(Vector3f.class, (field, component) ->
    {
      Vector3f vec3 = ((Vector3f) field.get(component));
      float[] values = new float[] {vec3.x, vec3.y, vec3.z};
      ImGui.inputFloat3(field.getName(), values);
      vec3.set(values);
    });

    put(Vector4f.class, (field, component) ->
    {
      Vector4f vec4 = ((Vector4f) field.get(component));
      float[] values = new float[] {vec4.x, vec4.y, vec4.z, vec4.w};
      ImGui.inputFloat4(field.getName(), values);
      vec4.set(values);
    });

    put(Quaternionf.class, (field, component) ->
    {
      Quaternionf quatf = ((Quaternionf) field.get(component));
      Vector3f vec3 = quatf.getEulerAnglesXYZ(new Vector3f());
      float[] values = new float[]
      {
        (float) Math.toDegrees(vec3.x),
        (float) Math.toDegrees(vec3.y),
        (float) Math.toDegrees(vec3.z)
      };
      ImGui.inputFloat3(field.getName(), values);
      quatf.rotationXYZ(
        (float) Math.toRadians(values[0]),
        (float) Math.toRadians(values[1]),
        (float) Math.toRadians(values[2])
      );
    });

    put(ArrayList.class, (field, component) ->
    {
      // type of the array list depends on the component in this case
      // this is kinda shit, as two ArrayLists per component will now not work!
      // but it will work for now...
      if (component instanceof Mesh)
      {
        ArrayList<String> assets = (ArrayList<String>) field.get(component);
        for (String asset : assets)
        {
          ImGui.text(asset);
        }
      }
      else if (component instanceof MaterialComponent)
      {
        ArrayList<String> assets = (ArrayList<String>) field.get(component);
        int i = 0;
        for (String asset : assets)
        {
          ImString s = new ImString(255);
          s.set(asset);
          if (ImGui.inputText("Material Nr. " + (i) + "###mtl" + asset, s, ImGuiInputTextFlags.EnterReturnsTrue))
          {
            String material = s.get();

            if (!AssetManager.exists(material, Material.class))
            {
              Console.error("cannot find material with id '" + material + "' - make sure the material is loaded");
              return;
            }

            try
            {
              ((MaterialComponent) component).map(i, material);
            }
            catch(Exception e)
            {
              e.printStackTrace();
            }
            i++;
          }
        }
      }
    });
  }};

  static <T extends EntityComponent> void render(Field field, T component)
  {
    try
    {
      if (!EditorFieldDisplay.renderers.containsKey(field.getType()))
      {
        throw new UnsupportedOperationException("cannot render display for field of type '" + field.getType() + "' - " +
          "no renderer found");
      }

      EditorFieldDisplay.renderers.get(field.getType()).display(field, component);
    }
    catch (Exception e)
    {
      Console.error("cannot render display for field of type '" + field.getType() + "' - " +
        "rendering failed");
    }
  }

  void display(Field field, EntityComponent component) throws Exception;
}
