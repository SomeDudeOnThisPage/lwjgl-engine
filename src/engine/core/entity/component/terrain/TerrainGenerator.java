package engine.core.entity.component.terrain;

import engine.core.gfx.VertexArray;
import org.joml.*;

public class TerrainGenerator
{
  public static final float mul = 20.0f;

  public static int[] generateIndices(int size)
  {
    int[] indices = new int[(size) * (size) * 6];
    int n = 0;
    int lod = 1;
    for (int i = 0; i < size - 1; i++)
    {
      for (int j = 0; j < size - 1; j++)
      {
        int tl = i * size + j;                               // global top-right index
        int tr = tl + lod;                                   // global top-left index
        int bl = (i + lod) * size + j;                       // global bottom-left index
        int br = bl + lod;                                   // global bottom-right index

        indices[n++] = tl;
        indices[n++] = tr;
        indices[n++] = bl;
        indices[n++] = tr;
        indices[n++] = br;
        indices[n++] = bl;

        //System.out.println(tl + " " + tr + " " + bl + " " + tr + " " + br + " " + bl);
      }
    }

    return indices;
  }

  public static float[] generateVertices(HeightField heightfield, Vector2i size)
  {
    float[] vertices = new float[size.x * size.y * 3];
    int v = 0;
    for (int x = 0; x < size.x; x++)
    {
      for (int y = 0; y < size.y; y++)
      {
        vertices[v++] = x;
        vertices[v++] = heightfield.getHeight(x, y) * mul;
        vertices[v++] = y;
        //System.out.println(x + " " + heightfield.getHeight(x, y) * (255.0f / 2.0f) + " " + y);
      }
    }

    return vertices;
  }

  public static float[] generateTextureCoordinates(Vector2i size)
  {
    float[] textures = new float[size.x * size.y * 2];
    int t = 0;

    for (int i = 0; i < size.x; i++)
    {
      for (int j = 0; j < size.y; j++)
      {
        textures[t++] = (float) j / (size.y - 1) * 32.0f;
        textures[t++] = (float) i / (size.x - 1) * 32.0f;

        //System.out.println(i + " " + (float) j / (size - 1) + " " + j + " " + (float) i / (size - 1));
      }
    }

    return textures;
  }

  public static float[] generateNormals(HeightField heightfield, Vector2i size)
  {
    float[] normals = new float[size.x * size.y * 3];
    int n = 0;
    for (int x = 0; x < size.x; x++)
    {
      for (int y = 0; y < size.y; y++)
      {
        float heightL = heightfield.getHeight(x - 1, y) * mul;
        float heightR = heightfield.getHeight(x + 1, y) * mul;
        float heightU = heightfield.getHeight(x, y - 1) * mul;
        float heightD = heightfield.getHeight(x, y + 1) * mul;

        Vector3f normal = new Vector3f(heightL - heightR, 2.0f, heightU - heightD).normalize();

        normals[n++] = normal.x;
        normals[n++] = normal.y;
        normals[n++] = normal.z;
      }
    }

    return normals;
  }

  public static float[] generateTangents(float[] vertices, float[] textures, int[] indices, boolean bitangents)
  {
    float[] tangents = new float[2 * 2 * 3];
    int t = 0;
    int uv = 0;
    System.out.println("LE" + indices.length + " V" + vertices.length);
    for (int i = 0; i < indices.length; i+=3)
    {
      int index = indices[i];
      Vector3f v1 = new Vector3f(vertices[index], vertices[index] + 1, vertices[index] + 2);
      Vector3f v2 = new Vector3f(vertices[index + 1], vertices[index + 1] + 1, vertices[index + 1] + 2);
      Vector3f v3 = new Vector3f(vertices[index + 2], vertices[index + 2] + 1, vertices[index + 2] + 2);

      Vector2f uv1 = new Vector2f(textures[index], textures[index] + 1);
      Vector2f uv2 = new Vector2f(textures[index + 1], textures[index + 1] + 1);
      Vector2f uv3 = new Vector2f(textures[index + 2], textures[index + 2] + 1);

      Vector3f tangent = new Vector3f();
      if (bitangents)
      {
        GeometryUtils.bitangent(v1, uv1, v2, uv2, v3, uv3, tangent);
      }
      else
      {
        GeometryUtils.tangent(v1, uv1, v2, uv2, v3, uv3, tangent);
        //System.out.println(v1 + " " + v2 + " " + v3);
        //System.out.println(uv1 + " " + uv2 + " " + uv3);
        //System.out.println(tangent);
      }

      tangents[t++] = tangent.x;
      tangents[t++] = tangent.y;
      tangents[t++] = tangent.z;
    }

    return tangents;
  }

  public static VertexArray generate(HeightField heights, Vector2i size)
  {
    VertexArray mesh = new VertexArray();
    mesh.addAttribute(0, 3, TerrainGenerator.generateVertices(heights, size));
    mesh.addAttribute(1, 2, TerrainGenerator.generateTextureCoordinates(size));
    mesh.addAttribute(2, 3, TerrainGenerator.generateNormals(heights, size));
    mesh.addIndices(TerrainGenerator.generateIndices(size.x));

    return mesh;
  }
}
