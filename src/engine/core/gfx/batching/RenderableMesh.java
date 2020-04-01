package engine.core.gfx.batching;

import engine.core.gfx.material.Material;
import engine.core.gfx.VertexArray;

/**
 * This object links static mesh prefabs ({@link VertexArray}s) to {@link Material}s.
 */
public class RenderableMesh
{
  private Material material;
  private VertexArray mesh;

  public VertexArray getMesh()
  {
    return this.mesh;
  }

  public void setMaterial(Material material)
  {
    this.material = material;
  }

  public RenderableMesh(VertexArray mesh, Material material)
  {
    this.mesh = mesh;
    this.material = material;
  }
}
