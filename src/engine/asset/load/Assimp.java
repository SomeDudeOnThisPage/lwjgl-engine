package engine.asset.load;

import com.badlogic.gdx.physics.bullet.collision.PHY_ScalarType;
import com.badlogic.gdx.physics.bullet.collision.btBvhTriangleMeshShape;
import com.badlogic.gdx.physics.bullet.collision.btIndexedMesh;
import com.badlogic.gdx.physics.bullet.collision.btTriangleIndexVertexArray;
import engine.gfx.buffer.IndexBuffer;
import engine.gfx.buffer.VertexArray;
import engine.gfx.buffer.VertexBufferLayout;
import engine.gfx.buffer.VertexDataType;
import engine.gfx.opengl.buffer.GLIndexBuffer;
import engine.gfx.opengl.buffer.GLVertexArray;
import engine.gfx.opengl.buffer.GLVertexBuffer;
import engine.util.ArrayUtil;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static org.lwjgl.assimp.Assimp.*;
import static org.lwjgl.opengl.GL42C.*;

import static engine.Engine.*;

public class Assimp
{
  private static final class Loader implements Runnable
  {
    @Override
    public void run()
    {

    }
  }

  private static synchronized VertexArray processMesh(AIMesh aimesh, int index)
  {
    VertexArray mesh = new GLVertexArray();

    ArrayList<Float> vertices = new ArrayList<>();
    ArrayList<Float> textures = new ArrayList<>();
    ArrayList<Float> normals = new ArrayList<>();
    ArrayList<Float> tangents = new ArrayList<>();
    ArrayList<Float> bitangents = new ArrayList<>();
    ArrayList<Integer> indices = new ArrayList<>();

    ArrayList<Float> data = new ArrayList<>();

    AIVector3D.Buffer aiVertices = aimesh.mVertices();
    while (aiVertices.remaining() > 0)
    {
      AIVector3D aiVertex = aiVertices.get();
      vertices.add(aiVertex.x());
      vertices.add(aiVertex.y());
      vertices.add(aiVertex.z());
    }

    AIVector3D.Buffer aiTextures = aimesh.mTextureCoords(0);
    if (aiTextures != null)
    {
      while (aiTextures.remaining() > 0)
      {
        AIVector3D aiTexture = aiTextures.get();
        textures.add(aiTexture.x());
        textures.add(1 - aiTexture.y());
      }
    }

    AIVector3D.Buffer aiNormals = aimesh.mNormals();

    if (aiNormals != null)
    {
      while (aiNormals.remaining() > 0)
      {
        AIVector3D aiNormal = aiNormals.get();
        normals.add(aiNormal.x());
        normals.add(aiNormal.y());
        normals.add(aiNormal.z());
      }
    }

    int numFaces = aimesh.mNumFaces();
    AIFace.Buffer aiFaces = aimesh.mFaces();

    for (int i = 0; i < numFaces; i++)
    {
      AIFace aiFace = aiFaces.get();
      IntBuffer buffer = aiFace.mIndices();
      while (buffer.remaining() > 0)
      {
        indices.add(buffer.get());
      }
    }

    AIVector3D.Buffer aiTangents = aimesh.mTangents();
    if (aiTangents != null)
    {
      while (aiTangents.remaining() > 0)
      {
        AIVector3D aiTangent = aiTangents.get();
        tangents.add(aiTangent.x());
        tangents.add(aiTangent.y());
        tangents.add(aiTangent.z());
      }
    }


    AIVector3D.Buffer aiBitangents = aimesh.mTangents();
    if (aiBitangents != null)
    {
      while (aiBitangents.remaining() > 0)
      {
        AIVector3D aiBitangent = aiBitangents.get();
        bitangents.add(aiBitangent.x());
        bitangents.add(aiBitangent.y());
        bitangents.add(aiBitangent.z());
      }
    }

    VertexBufferLayout layout = new VertexBufferLayout();

    if (vertices.size() > 0)
    {
      layout.addElement(new VertexBufferLayout.BufferElement("v_position", VertexDataType.FLOAT3));
    }

    if (textures.size() > 0)
    {
      layout.addElement(new VertexBufferLayout.BufferElement("v_texture",  VertexDataType.FLOAT2));
    }

    if (normals.size() > 0)
    {
      layout.addElement(new VertexBufferLayout.BufferElement("v_normal",   VertexDataType.FLOAT3));
    }

    if (tangents.size() > 0)
    {
      layout.addElement(new VertexBufferLayout.BufferElement("v_tangent", VertexDataType.FLOAT3));
    }

    if (bitangents.size() > 0)
    {
      layout.addElement(new VertexBufferLayout.BufferElement("v_bitangent", VertexDataType.FLOAT3));
    }

    // interleave vertex data into data array according to the layout
    int counter2 = 0;
    for (int i = 0; i < vertices.size(); i += 3)
    {
      data.add(vertices.get(i));
      data.add(vertices.get(i + 1));
      data.add(vertices.get(i + 2));

      if (textures.size() > 0)
      {
        data.add(textures.get(counter2));
        data.add(textures.get(counter2 + 1));
      }

      data.add(normals.get(i));
      data.add(normals.get(i + 1));
      data.add(normals.get(i + 2));

      if (tangents.size() > 0)
      {
        data.add(tangents.get(i));
        data.add(tangents.get(i + 1));
        data.add(tangents.get(i + 2));
      }

      if (bitangents.size() > 0)
      {
        data.add(bitangents.get(i));
        data.add(bitangents.get(i + 1));
        data.add(bitangents.get(i + 2));
      }

      counter2 += 2;
    }

    GLVertexBuffer vbo = new GLVertexBuffer(GL_ARRAY_BUFFER, GL_FLOAT, GL_STATIC_DRAW);
    vbo.layout(layout);
    vbo.data(ArrayUtil.toBoxedArrayF(data));
    mesh.addVertexBuffer(vbo);

    IndexBuffer ibo = new GLIndexBuffer(GL_STATIC_DRAW, ArrayUtil.toBoxedArrayI(indices));
    mesh.setIndexBuffer(ibo);

    return mesh;
  }

  public static void loadMeshesAbsolute(String path)
  {
    System.out.println(path);
    AIScene scene = aiImportFile(path, aiProcess_ImproveCacheLocality |
      aiProcess_JoinIdenticalVertices |
      aiProcess_Triangulate |
      aiProcess_CalcTangentSpace);

    if (scene == null)
    {
      throw new UnsupportedOperationException("scene is null");
    }

    int numMeshes = scene.mNumMeshes();
    PointerBuffer aiMeshes = scene.mMeshes();

    for (int i = 0; i < numMeshes; i++)
    {
      assert aiMeshes != null;

      AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
      VertexArray mesh = processMesh(aiMesh, i);

      AIString aiName = aiMesh.mName();
      aiMesh.mName(aiName);

      byte[] s = new byte[(int) aiName.length()];
      aiName.data().get(s);

      String name = new File(path).getName();
      name = name.substring(0, name.indexOf("."));

      AssetManager.load(mesh, name + "." + new String(s, StandardCharsets.UTF_8), VertexArray.class);
    }
  }

  public static void loadMeshes(String path)
  {
    Assimp.loadMeshesAbsolute("platform/resources/models/" + path);
  }

  public static btBvhTriangleMeshShape loadTriMesh(String path, int num)
  {
    AIScene scene = aiImportFile(path, aiProcess_ImproveCacheLocality |
      aiProcess_JoinIdenticalVertices |
      aiProcess_Triangulate |
      aiProcess_CalcTangentSpace);

    btTriangleIndexVertexArray shape = new btTriangleIndexVertexArray();
    assert scene != null;

    PointerBuffer aiMeshes = scene.mMeshes();

    for (int i = 0; i < num; i++)
    {
      assert aiMeshes != null;
      AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));

      ByteBuffer vertices = BufferUtils.createByteBuffer(aiMesh.mNumVertices() * 3 * Float.BYTES).order(ByteOrder.nativeOrder());

      AIVector3D.Buffer aiVertices = aiMesh.mVertices();

      while (aiVertices.remaining() > 0)
      {
        AIVector3D aiVertex = aiVertices.get();
        vertices.putFloat(aiVertex.x());
        vertices.putFloat(aiVertex.y());
        vertices.putFloat(aiVertex.z());
      }

      int numFaces = aiMesh.mNumFaces();
      AIFace.Buffer aiFaces = aiMesh.mFaces();
      ArrayList<Integer> inds = new ArrayList<>();

      for (int j = 0; j < numFaces; j++)
      {
        AIFace aiFace = aiFaces.get();
        IntBuffer buffer = aiFace.mIndices();
        while (buffer.remaining() > 0)
        {
          inds.add(buffer.get());
        }
      }

      ByteBuffer indices = BufferUtils.createByteBuffer(inds.size() * 4).order(ByteOrder.nativeOrder());

      for (Integer in : inds)
      {
        indices.putInt(in);
      }

      vertices.flip();
      indices.flip();

      //buffers.add(vertices);
      //buffers.add(indices);

      btIndexedMesh indexed = new btIndexedMesh();
      indexed.setIndexType(PHY_ScalarType.PHY_INTEGER);
      indexed.setNumTriangles(indices.limit() / 4 / 3);
      indexed.setNumVertices(vertices.limit() / 4 / 3);
      indexed.setVertexStride(3 * Float.BYTES);
      indexed.setTriangleIndexStride(3 * /* Integer.BYTES == Float.BYTES */ Float.BYTES);
      indexed.setVertexBase(vertices);
      indexed.setTriangleIndexBase(indices);

      shape.addIndexedMesh(indexed, PHY_ScalarType.PHY_INTEGER);
    }

    return new btBvhTriangleMeshShape(shape, true);
  }
}
