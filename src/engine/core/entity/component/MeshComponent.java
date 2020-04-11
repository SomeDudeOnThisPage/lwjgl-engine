package engine.core.entity.component;

import engine.core.gfx.VertexArray;
import engine.core.gfx.batching.AssetManager;
import engine.core.gfx.material.Material;
import engine.util.Assimp;

import static org.lwjgl.opengl.GL11C.GL_BACK;

/**
 * Mesh component used for renderable entities.
 * Note that a mesh component also includes texture positions, normals and indices.
 */
public class MeshComponent extends EntityComponent
{
  public VertexArray[] mesh;
  public Material[] material;

  public int culling;

  public MeshComponent(VertexArray[] vao)
  {
    this.mesh = vao;
    this.material = new Material[this.mesh.length];
    this.culling = GL_BACK;
  }

  public MeshComponent(VertexArray vao)
  {
    this.mesh = new VertexArray[] { vao };
    this.material = new Material[this.mesh.length];
    this.culling = GL_BACK;
  }

  public MeshComponent(String mesh)
  {
    this.mesh = AssetManager.getMesh(mesh);
    this.material = new Material[this.mesh.length];
    this.culling = GL_BACK;
  }
}