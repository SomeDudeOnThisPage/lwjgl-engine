package engine.core.gfx.batching;

import engine.core.gfx.material.MaterialArchetype;
import engine.core.gfx.VertexArray;

/**
 * This object links static mesh prefabs ({@link VertexArray}s) to {@link MaterialArchetype}s.
 */
public class RenderableMesh
{
  private MaterialArchetype material;
  private VertexArray mesh;

  public VertexArray getMesh()
  {
    return this.mesh;
  }

  public void setMaterial(MaterialArchetype material)
  {
    this.material = material;
  }

  public RenderableMesh(VertexArray mesh, MaterialArchetype material)
  {
    this.mesh = mesh;
    this.material = material;
  }
}
