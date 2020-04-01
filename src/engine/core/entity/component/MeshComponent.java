package engine.core.entity.component;

import engine.core.gfx.VertexArray;
import engine.core.gfx.batching.AssetManager;
import engine.core.gfx.material.Material;
import engine.util.Assimp;

/**
 * Mesh component used for renderable entities.
 * Note that a mesh component also includes texture positions, normals and indices.
 */
public class MeshComponent extends EntityComponent
{
  public VertexArray[] mesh;
  public Material[] material;

  public MeshComponent(VertexArray[] vao)
  {
    this.mesh = vao;
    this.material = new Material[this.mesh.length];
  }

  public MeshComponent(VertexArray vao)
  {
    this.mesh = new VertexArray[] { vao };
    this.material = new Material[this.mesh.length];
  }

  public MeshComponent(String mesh)
  {
    this.mesh = AssetManager.getMesh(mesh);
    this.material = new Material[this.mesh.length];
  }
}