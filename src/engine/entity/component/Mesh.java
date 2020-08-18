package engine.entity.component;

import engine.entity.EditorComponent;
import engine.entity.EditorField;
import engine.entity.EntityComponent;
import engine.gfx.buffer.VertexArray;

import java.util.ArrayList;
import java.util.Arrays;

import static engine.Engine.*;

@EditorComponent
public class Mesh extends EntityComponent
{
  @EditorField
  public final ArrayList<String> mesh;

  @Override
  public void onComponentAttached()
  {
    for (String vao : this.mesh)
    {
      // request all assets once
      AssetManager.request(vao, VertexArray.class);
    }
  }

  @Override
  public void onComponentRemoved()
  {
    for (String vao : this.mesh)
    {
      AssetManager.release(vao, VertexArray.class);
    }
  }

  public Mesh(String... meshes)
  {
    this.mesh = new ArrayList<>();
    this.mesh.addAll(Arrays.asList(meshes));
  }
}