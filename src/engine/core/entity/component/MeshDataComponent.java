package engine.core.entity.component;

import engine.core.gfx.Mesh;

public class MeshDataComponent extends EntityComponent
{
  public Mesh data;

  public MeshDataComponent(Mesh data)
  {
    this.data = data;
  }
}
