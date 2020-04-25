package engine.core.entity.component;

import engine.core.gfx.VertexArray;
import engine.core.assetmanager.AssetManager;
import engine.core.gfx.material.MaterialArchetype;

import static org.lwjgl.opengl.GL11C.GL_BACK;

/**
 * Mesh component used for renderable entities.
 * Note that a mesh component also includes texture positions, normals and indices.
 */
public class MeshComponent extends EntityComponent
{
  public VertexArray[] mesh;
  public MaterialArchetype[] material;

  public int culling;

  public MeshComponent(VertexArray[] vao)
  {
    this.mesh = vao;
    this.material = new MaterialArchetype[this.mesh.length];

    // map initial materials
    for (int i = 0; i < this.mesh.length; i++)
    {
      if (this.mesh[i].getInitialMaterial() != null)
      {
        this.material[i] = AssetManager.getMaterial(this.mesh[i].getInitialMaterial());
        continue;
      }

      this.material[i] = AssetManager.getMissingMaterial();
    }

    this.culling = GL_BACK;
  }

  public MeshComponent(VertexArray vao)
  {
    this.mesh = new VertexArray[] { vao };
    this.material = new MaterialArchetype[this.mesh.length];
    this.culling = GL_BACK;
  }

  public MeshComponent(String mesh)
  {
    this.mesh = AssetManager.getMesh(mesh);
    this.material = new MaterialArchetype[this.mesh.length];
    this.culling = GL_BACK;
  }
}