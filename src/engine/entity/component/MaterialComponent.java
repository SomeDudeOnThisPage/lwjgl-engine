package engine.entity.component;

import engine.entity.EditorField;
import engine.entity.EntityComponent;
import engine.gfx.material.Material;

import java.util.ArrayList;
import java.util.Arrays;

import static engine.Engine.*;

public class MaterialComponent extends EntityComponent
{
  @EditorField
  public final ArrayList<String> material;

  public boolean remapped;

  public void map(int index, String material)
  {
    // release old material
    AssetManager.release(this.material.get(index), Material.class);

    // set new material
    this.material.set(index, material);

    // request new material
    AssetManager.request(material, Material.class);

    this.remapped = true;
  }

  public String get(int index)
  {
    if (this.material.get(index) != null)
    {
      return this.material.get(index);
    }

    // default index
    return this.material.get(0);
  }

  @Override
  public void onComponentAttached()
  {
    for (String mtl : this.material)
    {
      // request all assets once
      AssetManager.request(mtl, Material.class);
    }
  }

  @Override
  public void onComponentRemoved()
  {
    for (String mtl : this.material)
    {
      AssetManager.release(mtl, Material.class);
    }
  }

  public MaterialComponent(String... materials)
  {
    this.material = new ArrayList<>();
    this.material.addAll(Arrays.asList(materials));
    this.remapped = false;
  }
}
