package engine.util;

import com.badlogic.gdx.physics.bullet.collision.PHY_ScalarType;
import com.badlogic.gdx.physics.bullet.collision.btIndexedMesh;
import com.badlogic.gdx.physics.bullet.collision.btTriangleIndexVertexArray;
import engine.core.gfx.Mesh;
import engine.core.gfx.VertexArray;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import java.nio.*;
import java.util.ArrayList;

import static org.lwjgl.assimp.Assimp.*;

public class Assimp
{
  private static ArrayList<btIndexedMesh> meshes = new ArrayList<>();
  private static ArrayList<btTriangleIndexVertexArray> shapes = new ArrayList<>();

  public static void terminate()
  {
    for (btIndexedMesh mesh : meshes)
    {
      mesh.dispose();
    }

    for (btTriangleIndexVertexArray array : shapes)
    {
      array.dispose();
    }
  }

  private static synchronized Mesh processMesh(AIMesh aimesh)
  {
    Mesh mesh = new Mesh();

    ArrayList<Float> vertices = new ArrayList<>();
    ArrayList<Float> textures = new ArrayList<>();
    ArrayList<Float> normals = new ArrayList<>();
    ArrayList<Float> tangents = new ArrayList<>();
    ArrayList<Float> bitangents = new ArrayList<>();
    ArrayList<Integer> indices = new ArrayList<>();

    AIVector3D.Buffer aiVertices = aimesh.mVertices();
    while (aiVertices.remaining() > 0)
    {
      AIVector3D aiVertex = aiVertices.get();
      vertices.add(aiVertex.x());
      vertices.add(aiVertex.y());
      vertices.add(aiVertex.z());
    }

    AIVector3D.Buffer aiTextures = aimesh.mTextureCoords(0);
    assert aiTextures != null;

    while (aiTextures.remaining() > 0)
    {
      AIVector3D aiTexture = aiTextures.get();
      textures.add(aiTexture.x());
      textures.add(1 - aiTexture.y());
    }

    AIVector3D.Buffer aiNormals = aimesh.mNormals();
    assert aiNormals != null;

    while (aiNormals.remaining() > 0)
    {
      AIVector3D aiNormal = aiNormals.get();
      normals.add(aiNormal.x());
      normals.add(aiNormal.y());
      normals.add(aiNormal.z());
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
    while (aiTangents.remaining() > 0)
    {
      AIVector3D aiTangent = aiTangents.get();
      tangents.add(aiTangent.x());
      tangents.add(aiTangent.y());
      tangents.add(aiTangent.z());
    }

    AIVector3D.Buffer aiBitangents = aimesh.mTangents();
    while (aiBitangents.remaining() > 0)
    {
      AIVector3D aiBitangent = aiBitangents.get();
      bitangents.add(aiBitangent.x());
      bitangents.add(aiBitangent.y());
      bitangents.add(aiBitangent.z());
    }

    mesh.vertices(Utils.toPrimitiveF(vertices));
    mesh.indices(Utils.toPrimitiveI(indices));

    return mesh;
  }

  private static synchronized VertexArray processMesh(AIMesh aimesh, int index)
  {
    VertexArray mesh = new VertexArray();

    ArrayList<Float> vertices = new ArrayList<>();
    ArrayList<Float> textures = new ArrayList<>();
    ArrayList<Float> normals = new ArrayList<>();
    ArrayList<Float> tangents = new ArrayList<>();
    ArrayList<Float> bitangents = new ArrayList<>();
    ArrayList<Integer> indices = new ArrayList<>();

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
    while (aiTangents.remaining() > 0)
    {
      AIVector3D aiTangent = aiTangents.get();
      tangents.add(aiTangent.x());
      tangents.add(aiTangent.y());
      tangents.add(aiTangent.z());
    }

    AIVector3D.Buffer aiBitangents = aimesh.mTangents();
    while (aiBitangents.remaining() > 0)
    {
      AIVector3D aiBitangent = aiBitangents.get();
      bitangents.add(aiBitangent.x());
      bitangents.add(aiBitangent.y());
      bitangents.add(aiBitangent.z());
    }

    mesh.addAttribute(VertexArray.VERTEX_3F_POINTER, 3, Utils.toPrimitiveF(vertices));
    mesh.addAttribute(VertexArray.TEXTURE_2F_POINTER, 2, Utils.toPrimitiveF(textures));
    mesh.addAttribute(VertexArray.NORMAL_3F_POINTER, 3, Utils.toPrimitiveF(normals));
    mesh.addAttribute(VertexArray.TANGENT_3F_POINTER, 3, Utils.toPrimitiveF(tangents));
    mesh.addAttribute(VertexArray.BITANGENT_3F_POINTER, 3, Utils.toPrimitiveF(bitangents));
    mesh.addIndices(Utils.toPrimitiveI(indices));

    return mesh;
  }

  private static final ArrayList<AIScene> scenes = new ArrayList<>();
  private static final ArrayList<ByteBuffer> buffers = new ArrayList<>();

  public static btTriangleIndexVertexArray load_collision_mesh_tri(String path)
  {
    AIScene scene = aiImportFile("resources/models/" + path + ".obj", aiProcess_ImproveCacheLocality |
      aiProcess_JoinIdenticalVertices |
      aiProcess_Triangulate |
      aiProcess_CalcTangentSpace);

    scenes.add(scene);

    btTriangleIndexVertexArray shape = new btTriangleIndexVertexArray();

    assert scene != null;
    int numMeshes = scene.mNumMeshes();
    PointerBuffer aiMeshes = scene.mMeshes();

    for (int i = 0; i < numMeshes; i++)
    {
      assert aiMeshes != null;
      AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
      Mesh mesh = processMesh(aiMesh);

      ByteBuffer vertices = BufferUtils.createByteBuffer(mesh.vertices().length * Float.BYTES).order(ByteOrder.nativeOrder());
      ByteBuffer indices = BufferUtils.createByteBuffer(mesh.indices().length * 4).order(ByteOrder.nativeOrder());

      for (float v : mesh.vertices())
      {
        vertices.putFloat(v);
      }

      for (int in : mesh.indices())
      {
        indices.putInt(in);
      }

      vertices.flip();
      indices.flip();

      buffers.add(vertices);
      buffers.add(indices);

      btIndexedMesh indexed = new btIndexedMesh();
      indexed.setIndexType(PHY_ScalarType.PHY_INTEGER);
      indexed.setNumTriangles(mesh.indices().length / 3);
      indexed.setNumVertices(mesh.vertices().length / 3);
      indexed.setVertexStride(3 * Float.BYTES);
      indexed.setTriangleIndexStride(3 * /* Integer.BYTES == Float.BYTES */ Float.BYTES);
      indexed.setVertexBase(vertices);
      indexed.setTriangleIndexBase(indices);

      meshes.add(indexed);

      shape.addIndexedMesh(indexed, PHY_ScalarType.PHY_INTEGER);
    }

    shapes.add(shape);

    return shape;
  }

  public static VertexArray[] load_static(String path)
  {
    AIScene scene = aiImportFile("resources/models/" + path + ".obj", aiProcess_ImproveCacheLocality |
                                                                      aiProcess_JoinIdenticalVertices |
                                                                      aiProcess_Triangulate |
                                                                      aiProcess_CalcTangentSpace);

    assert scene != null;
    int numMaterials = scene.mNumMaterials();
    PointerBuffer aiMaterials = scene.mMaterials();
    //ArrayList<MaterialArchetype> materials = new ArrayList<>();

    ArrayList<String> materials = new ArrayList<>();

    for (int i = 0; i < numMaterials; i++)
    {
      assert aiMaterials != null;
      AIMaterial aiMaterial = AIMaterial.create(aiMaterials.get(i));
      AIString name = AIString.create();
      aiGetMaterialString(aiMaterial, AI_MATKEY_NAME, aiTextureType_NONE, 0, name);
      materials.add(name.dataString());
    }

    int numMeshes = scene.mNumMeshes();
    PointerBuffer aiMeshes = scene.mMeshes();
    VertexArray[] meshes = new VertexArray[numMeshes];

    for (int i = 0; i < numMeshes; i++)
    {
      assert aiMeshes != null;

      AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
      VertexArray mesh = processMesh(aiMesh, i);
      meshes[i] = mesh;
      meshes[i].setInitialMaterial(materials.get(i));
    }

    return meshes;
  }
}
