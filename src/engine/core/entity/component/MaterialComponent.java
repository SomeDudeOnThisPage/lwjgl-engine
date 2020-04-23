package engine.core.entity.component;

import engine.core.gfx.material.MaterialArchetype;

public class MaterialComponent extends EntityComponent
{
  public MaterialArchetype material;

  public MaterialComponent()
  {
    this("white");
  }

  public MaterialComponent(MaterialArchetype material)
  {
    this.material = material;
  }

  public MaterialComponent(String texture)
  {
    /*this.material = new MaterialArchetype(
      new Vector3f(0.1f, 0.1f, 0.1f),
      new Vector3f(1.0f, 1.0f, 1.0f),
      new Vector3f(1.0f, 1.0f, 1.0f),
      0.4f
    );

    this.material.addMap(0, new Texture(texture));*/
  }
}
