package engine.core.gfx.batching;

import engine.core.gfx.UniformBuffer;
import engine.core.gfx.material.Material;
import engine.core.gfx.Shader;
import engine.core.gfx.VertexArray;
import engine.core.gfx.material.PBRMaterialFlat;
import engine.util.Assimp;

import java.util.HashMap;

public class AssetManager
{
  private static final int MAT_FPBR_UNIFORM_BUFFER_BINDING = 4;

  public static UniformBuffer fpbr;

  public static HashMap<String, Material> materials = new HashMap<>();
  public static HashMap<String, VertexArray[]> meshes = new HashMap<>();

  public static void initialize()
  {
    AssetManager.fpbr = new UniformBuffer(PBRMaterialFlat.UNIFORM_MATERIAL_INSTANCE_BYTES * 1024, MAT_FPBR_UNIFORM_BUFFER_BINDING);
  }

  public static Material loadMaterial(String name, PBRMaterialFlat material)
  {
    AssetManager.materials.put(name, material);
    return material;
  }

  public static Material getMaterial(String name)
  {
    if (AssetManager.materials.containsKey(name))
    {
      return AssetManager.materials.get(name);
    }

    PBRMaterialFlat material = new PBRMaterialFlat();
    AssetManager.materials.put(name, material);
    return material;
  }

  public static VertexArray[] getMesh(String name)
  {
    if (AssetManager.meshes.containsKey(name))
    {
      return AssetManager.meshes.get(name);
    }

    VertexArray[] mesh = Assimp.load_static(name);
    AssetManager.meshes.put(name, mesh);
    return mesh;
  }

  public static Shader getShader(String name)
  {
    return Shader.getInstance(name);
  }

  public static Shader getShader(String vertex, String fragment)
  {
    return Shader.getInstance(vertex, fragment);
  }

  public static Shader getShader(String vertex, String geometry, String fragment)
  {
    return Shader.getInstance(vertex, geometry, fragment);
  }
}
