package engine.core.gfx.batching;

import engine.core.gfx.material.MaterialArchetype;
import engine.core.gfx.VertexArray;

import java.util.ArrayList;
import java.util.HashMap;

public class MaterialIndex
{
  private HashMap<MaterialArchetype, ArrayList<VertexArray>> index;

  public void map(RenderableMesh mesh)
  {

  }

  public MaterialIndex()
  {
    this.index = new HashMap<>();
  }
}
