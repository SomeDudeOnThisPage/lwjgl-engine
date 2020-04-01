package engine.core.entity.component;

import engine.core.gfx.material.Material;
import engine.core.gfx.texture.Texture;
import org.joml.Vector3f;

public class MaterialComponent extends EntityComponent
{
  public Material material;

  public MaterialComponent()
  {
    this("white");
  }

  public MaterialComponent(Material material)
  {
    this.material = material;
  }

  public MaterialComponent(String texture)
  {
    this.material = new Material(
      new Vector3f(0.1f, 0.1f, 0.1f),
      new Vector3f(1.0f, 1.0f, 1.0f),
      new Vector3f(1.0f, 1.0f, 1.0f),
      0.4f
    );

    this.material.addMap(0, new Texture(texture));
  }
}
