package engine.core.entity.component.terrain;

import engine.core.entity.component.EntityComponent;
import engine.core.gfx.VertexArray;
import org.joml.Vector2i;

public class TerrainComponent extends EntityComponent
{
  public HeightField heights;
  public VertexArray mesh;

  public TerrainComponent(String terrain)
  {
    this.heights = new HeightField(terrain, 256, 256);
    this.mesh = TerrainGenerator.generate(this.heights, new Vector2i(255, 255));
  }
}