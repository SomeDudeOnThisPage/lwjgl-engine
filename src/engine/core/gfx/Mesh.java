package engine.core.gfx;

import engine.core.gfx.material.MaterialArchetype;

public class Mesh
{
  private float[] vertices;
  private float[] textures;
  private float[] normals;
  private float[] tangents;
  private float[] bitangents;

  private int[] indices;

  private MaterialArchetype material;

  public float[] vertices()
  {
    return this.vertices;
  }

  public void vertices(float[] vertices)
  {
    this.vertices = vertices;
  }

  public float[] textures()
  {
    return this.textures;
  }

  public void textures(float[] textures)
  {
    this.textures = textures;
  }

  public float[] normals()
  {
    return this.normals;
  }

  public void normals(float[] normals)
  {
    this.normals = normals;
  }

  public float[] tangents()
  {
    return this.tangents;
  }

  public void tangents(float[] tangents)
  {
    this.tangents = tangents;
  }

  public float[] bitangents()
  {
    return this.bitangents;
  }

  public void bitangents(float[] bitangents)
  {
    this.bitangents = bitangents;
  }

  public int[] indices()
  {
    return this.indices;
  }

  public void indices(int[] indices)
  {
    this.indices = indices;
  }

  public int vertexCount()
  {
    return this.vertices.length / 3;
  }


}