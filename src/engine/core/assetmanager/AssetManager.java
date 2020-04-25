package engine.core.assetmanager;

import engine.core.gfx.UniformBuffer;
import engine.core.gfx.material.MaterialArchetype;
import engine.core.gfx.Shader;
import engine.core.gfx.VertexArray;
import engine.core.gfx.material.PBRMaterialFlat;
import engine.core.gfx.material.PBRMaterialTextured;
import engine.util.Assimp;
import engine.util.Resource;
import engine.util.logging.Logger;
import engine.util.settings.Settings;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;

public class AssetManager
{
  private static final int MAT_FPBR_UNIFORM_BUFFER_BINDING = 4;

  public static UniformBuffer fpbr;

  public static HashMap<String, MaterialArchetype> materials = new HashMap<>();
  public static HashMap<String, VertexArray[]> meshes = new HashMap<>();

  public static void initialize()
  {
    AssetManager.fpbr = new UniformBuffer(PBRMaterialFlat.UNIFORM_MATERIAL_INSTANCE_BYTES * 1024, MAT_FPBR_UNIFORM_BUFFER_BINDING);
    AssetManager.loadMaterial(Settings.gets("MissingMaterialDefinition"));
  }

  public static MaterialArchetype getMissingMaterial()
  {
    return AssetManager.getMaterial(Settings.gets("MissingMaterialName"));
  }

  public static void loadMaterial(String name, MaterialArchetype material)
  {
    Logger.info("[ASSETMANAGER] loaded material " + name);
    AssetManager.materials.put(name, material);
  }

  public static void loadMaterial(String xml)
  {
    Document doc = Resource.loadXML(Settings.gets("ResourceDirectory") + Settings.gets("MaterialDefinitionDirectory") + xml + ".mat");
    assert doc != null;
    NodeList materialsXML = doc.getElementsByTagName("mtldef");
    for (int i = 0; i < materialsXML.getLength(); i++)
    {
      Node materialXML = materialsXML.item(i);
      if (materialXML.getNodeType() == Node.ELEMENT_NODE)
      {
        Element materialElement = (Element) materialXML;
        String name = materialElement.getAttribute("id");
        String archetype = materialElement.getAttribute("archetype");

        // load properties
        Element properties = (Element) materialElement.getElementsByTagName("properties").item(0);
        boolean transparency = Boolean.valueOf(properties.getElementsByTagName("transparency").item(0).getTextContent());

        NodeList archetypeDefinitions = materialElement.getElementsByTagName("archetype");
        switch (archetype)
        {
          case "MTL_ARCHETYPE_FLAT_COLOR":
            MaterialArchetype flat = new PBRMaterialFlat().load((Element) archetypeDefinitions.item(0));
            flat.properties().transparency = transparency;
            AssetManager.loadMaterial(name, flat);
            break;
          case "MTL_ARCHETYPE_TEXTURE":
            MaterialArchetype textured = new PBRMaterialTextured().load((Element) archetypeDefinitions.item(0));
            textured.properties().transparency = transparency;
            AssetManager.loadMaterial(name, textured);
            break;
        }
      }
    }
  }

  public static MaterialArchetype getMaterial(String name)
  {
    return AssetManager.materials.getOrDefault(name, null);
  }

  public static <T extends MaterialArchetype> MaterialArchetype getMaterial(String name, Class<T> archetype)
  {
    if (AssetManager.materials.containsKey(name))
    {
      return archetype.cast(AssetManager.materials.get(name));
    }

    MaterialArchetype material = null;
    try
    {
      material = archetype.newInstance();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }

    AssetManager.materials.put(name, material);
    return archetype.cast(material);
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
