package engine.core.gfx.batching;

import engine.core.gfx.material.Material;
import engine.core.gfx.Mesh;
import org.joml.Matrix4f;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL44C.*;

public class DeferredMeshBatcher
{
  /**
   * Total maximum batch size.
   */
  private static final int SIZE = (4194304) / 4;

  /**
   * Maximum amount of (instanced) models in one draw call.
   */
  private static final int MAX_INSTANCE_DRAW_CALLS = 512;

  /**
   * Total amount of floats on a per-vertex basis.
   * Three for position, three for normals, three for the tangent and two for UV-Coordinates.
   */
  private static final int FLOATS_PER_VERTEX = 3 + 3 + 3 + 2;

  /**
   * Total amount of floats on a per-object basis.
   * Sixteen floats for the model matrix.
   */
  private static final int FLOATS_PER_OBJECT = 16;

  private static int base = 0;
  private static int instance = 0;

  private static IntBuffer indirect;
  private static FloatBuffer vertices; //BufferUtils.createFloatBuffer(25 * 3 * 100);
  private static FloatBuffer uv;// = BufferUtils.createFloatBuffer(100 * 2 * 100);
  private static FloatBuffer normals;// = BufferUtils.createFloatBuffer(100 * 3 * 100);
  private static FloatBuffer models;

  private static IntBuffer elements;

  private static int vao;
  private static int indirectVBO;
  private static int vertexVBO;
  private static int modelVBO;
  private static int indexVBO;

  /*private static MappedBuffer indirectBuffer = new MappedBuffer(GL_DRAW_INDIRECT_BUFFER);
  private static MappedBuffer vertexBuffer = new MappedBuffer(GL_ARRAY_BUFFER);
  private static MappedBuffer modelBuffer = new MappedBuffer(GL_ARRAY_BUFFER);
  private static MappedBuffer indexBuffer = new MappedBuffer(GL_ELEMENT_ARRAY_BUFFER);

  public static void initialize()
  {
    indirect = ByteBuffer.allocateDirect(MAX_INSTANCE_DRAW_CALLS * 4 * 5).asIntBuffer(); //BufferUtils.createIntBuffer(1000 * 5);
    vertices = ByteBuffer.allocateDirect(SIZE / 4).asFloatBuffer(); //BufferUtils.createFloatBuffer(SIZE);
    models = ByteBuffer.allocateDirect(SIZE / 4).asFloatBuffer();
    elements = ByteBuffer.allocateDirect(SIZE / 4).asIntBuffer();

    indirectVBO = glGenBuffers();
    vao = glGenVertexArrays();
    vertexVBO = glGenBuffers();
    modelVBO = glGenBuffers();
    indexVBO = glGenBuffers();

    // setup VAO stuff
    vao = glGenVertexArrays();
    glBindVertexArray(vao);

    vertexBuffer.bind();
    glEnableVertexAttribArray(0);
    glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

    modelBuffer.bind();
    int modelAttributeLocation = 1;
    int bytesPerFloat = 4;
    int floatsPerMat = 16;
    int stride = bytesPerFloat * floatsPerMat;

    // mat4 attribute = 4 vec4 attributes
    glVertexAttribPointer(modelAttributeLocation,     4, GL_FLOAT, false, stride, 0);
    glVertexAttribPointer(modelAttributeLocation + 1, 4, GL_FLOAT, false, stride, 4*bytesPerFloat);
    glVertexAttribPointer(modelAttributeLocation + 2, 4, GL_FLOAT, false, stride, 8*bytesPerFloat);
    glVertexAttribPointer(modelAttributeLocation + 3, 4, GL_FLOAT, false, stride, 12*bytesPerFloat);

    // one matrix per instance, not per vertex
    glVertexAttribDivisor(modelAttributeLocation,     1);
    glVertexAttribDivisor(modelAttributeLocation + 1, 1);
    glVertexAttribDivisor(modelAttributeLocation + 2, 1);
    glVertexAttribDivisor(modelAttributeLocation + 3, 1);

    glEnableVertexAttribArray(modelAttributeLocation);
    glEnableVertexAttribArray(modelAttributeLocation + 1);
    glEnableVertexAttribArray(modelAttributeLocation + 2);
    glEnableVertexAttribArray(modelAttributeLocation + 3);

    glBindVertexArray(0);
  }

  public static void begin()
  {
    //DeferredMeshBatcher.batches.clear();

    // DeferredMeshBatcher.instance = 0;
    // DeferredMeshBatcher.base = 0;

    vertices = vertexBuffer.mapf(25 * 3 * 100);
    elements = indexBuffer.mapi(25 * 3 * 100);
    models = modelBuffer.mapf(25 * 3 * 100);
    indirect = indirectBuffer.mapi(512);

    glBindVertexArray(vao);

    //vertexVBO.bind();
    glBindBuffer(GL_ARRAY_BUFFER, vertexVBO);
    glEnableVertexAttribArray(0);
    glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

    glBindBuffer(GL_ARRAY_BUFFER, modelVBO);

    int modelAttributeLocation = 1;
    int bytesPerFloat = 4;
    int floatsPerMat = 16;
    int stride = bytesPerFloat * floatsPerMat;

    // mat4 attribute = 4 vec4 attributes
    glVertexAttribPointer(modelAttributeLocation,     4, GL_FLOAT, false, stride, 0);
    glVertexAttribPointer(modelAttributeLocation + 1, 4, GL_FLOAT, false, stride, 4*bytesPerFloat);
    glVertexAttribPointer(modelAttributeLocation + 2, 4, GL_FLOAT, false, stride, 8*bytesPerFloat);
    glVertexAttribPointer(modelAttributeLocation + 3, 4, GL_FLOAT, false, stride, 12*bytesPerFloat);

    // one matrix per instance, not per vertex
    glVertexAttribDivisor(modelAttributeLocation,     1);
    glVertexAttribDivisor(modelAttributeLocation + 1, 1);
    glVertexAttribDivisor(modelAttributeLocation + 2, 1);
    glVertexAttribDivisor(modelAttributeLocation + 3, 1);

    glEnableVertexAttribArray(modelAttributeLocation);
    glEnableVertexAttribArray(modelAttributeLocation + 1);
    glEnableVertexAttribArray(modelAttributeLocation + 2);
    glEnableVertexAttribArray(modelAttributeLocation + 3);

    try
    {
      glBindBuffer(GL_DRAW_INDIRECT_BUFFER, indirectVBO);
      glBufferData(GL_DRAW_INDIRECT_BUFFER, SIZE, GL_DYNAMIC_DRAW);
      indirect = glMapBufferRange(GL_DRAW_INDIRECT_BUFFER, 0, SIZE,
        GL_MAP_WRITE_BIT |
        GL_MAP_UNSYNCHRONIZED_BIT
      ).asIntBuffer();
    }
    catch(Exception e)
    {
      System.err.println("fatal memory error");
      System.err.println(glGetError());
      System.exit(-1);
    }

    try
    {
      glBindBuffer(GL_ARRAY_BUFFER, vertexVBO);
      glBufferData(GL_ARRAY_BUFFER, SIZE, GL_DYNAMIC_DRAW);
      vertices = glMapBufferRange(GL_ARRAY_BUFFER, 0, SIZE,
        GL_MAP_WRITE_BIT |
        GL_MAP_UNSYNCHRONIZED_BIT
      ).asFloatBuffer();
    }
    catch(Exception e)
    {
      System.err.println("fatal memory error");
      System.err.println(glGetError());
      System.exit(-1);
    }

    try
    {
      glBindBuffer(GL_ARRAY_BUFFER, modelVBO);
      glBufferData(GL_ARRAY_BUFFER, SIZE, GL_DYNAMIC_DRAW);
      models = glMapBufferRange(GL_ARRAY_BUFFER, 0, SIZE,
        GL_MAP_WRITE_BIT |
          GL_MAP_UNSYNCHRONIZED_BIT
      ).asFloatBuffer();
    }
    catch(Exception e)
    {
      System.err.println("fatal memory error");
      System.err.println(glGetError());
      System.exit(-1);
    }

    try
    {
      glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexVBO);
      glBufferData(GL_ELEMENT_ARRAY_BUFFER, SIZE, GL_DYNAMIC_DRAW);
      elements = glMapBufferRange(GL_ELEMENT_ARRAY_BUFFER, 0, SIZE,
        GL_MAP_WRITE_BIT |
        GL_MAP_UNSYNCHRONIZED_BIT
      ).asIntBuffer();
    }
    catch(Exception e)
    {
      System.err.println("fatal memory error");
      System.err.println(glGetError());
      System.exit(-1);
    }
  }

  public static void end()
  {
    /*vertexBuffer.unmap();
    modelBuffer.unmap();
    indexBuffer.unmap();
    indirectBuffer.unmap();

    glBindVertexArray(vao);
    glEnableVertexAttribArray(0);
    glEnableVertexAttribArray(1);
    glEnableVertexAttribArray(2);
    glEnableVertexAttribArray(3);
    glEnableVertexAttribArray(4);

    vertexBuffer.bind();
    modelBuffer.bind();
    indexBuffer.bind();
    indirectBuffer.bind();
    glBindBuffer(GL_ARRAY_BUFFER, vertexVBO);
    glUnmapBuffer(GL_ARRAY_BUFFER);

    glBindBuffer(GL_ARRAY_BUFFER, modelVBO);
    glUnmapBuffer(GL_ARRAY_BUFFER);

    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexVBO);
    glUnmapBuffer(GL_ELEMENT_ARRAY_BUFFER);

    glBindBuffer(GL_DRAW_INDIRECT_BUFFER, indirectVBO);
    glUnmapBuffer(GL_DRAW_INDIRECT_BUFFER);

    indirect.position(0);
    models.position(0);
    vertices.position(0);
    elements.position(0);
  }

  public static int getNumDrawCalls()
  {
    return DeferredMeshBatcher.instance;
  }

  public static void batch(Mesh mesh, Matrix4f model, Material material, int instances)
  {
    // sanity checks
    if (vertices.position() + mesh.vertices().length > SIZE / 4) { return; }
    if (indirect.position() + 5 > MAX_INSTANCE_DRAW_CALLS * 4 * 5) { return; }
    if (elements.position() + mesh.indices().length > SIZE / 4) { return; }
    if (models.position() + 16 > SIZE / 4) { return; }

    // create draw command data
    DeferredMeshBatcher.indirect.put(mesh.indices().length);          // GLuint vertexCount
    DeferredMeshBatcher.indirect.put(instances);                      // GLuint instanceCount
    DeferredMeshBatcher.indirect.put(0);                              // GLuint firstIndex
    DeferredMeshBatcher.indirect.put(DeferredMeshBatcher.base);       // GLuint baseVertex
    DeferredMeshBatcher.indirect.put(DeferredMeshBatcher.instance);   // GLuint baseInstance

    DeferredMeshBatcher.base += mesh.vertexCount();                   // increment base vertex by the number of vertices of the mesh
    DeferredMeshBatcher.instance += 1;                                // increment by instance or by 1??? todo: test this more

    // buffer vertices data
    DeferredMeshBatcher.vertices.put(mesh.vertices());

    //DeferredMeshBatcher.uv.put(mesh.textures());
    //DeferredMeshBatcher.normals.put(mesh.normals());

    // buffer index data
    DeferredMeshBatcher.elements.put(mesh.indices());

    // buffer model matrix data
    DeferredMeshBatcher.models.put(model.get(new float[16]));
  }

  public static void terminate()
  {
    /*glBindBuffer(GL_ARRAY_BUFFER, vertexVBO);
    glUnmapBuffer(GL_ARRAY_BUFFER);

    glBindBuffer(GL_ARRAY_BUFFER, modelVBO);
    glUnmapBuffer(GL_ARRAY_BUFFER);

    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexVBO);
    glUnmapBuffer(GL_ELEMENT_ARRAY_BUFFER);

    glBindBuffer(GL_DRAW_INDIRECT_BUFFER, indirectVBO);
    glUnmapBuffer(GL_DRAW_INDIRECT_BUFFER);

    glBindBuffer(GL_ARRAY_BUFFER, 0);
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    glBindBuffer(GL_DRAW_INDIRECT_BUFFER, 0);
  }*/


    /*this.directional = new TextureArray2D(
      Settings.geti("ShadowMapResolution"),
      Settings.geti("ShadowMapResolution"),
      Settings.geti("Max2DShadowMaps"),
      new TextureFilterBilinear(),
      new TextureWrap(GL_CLAMP_TO_BORDER),
      new TextureFormat(GL_RGBA32F, GL_RGBA, GL_FLOAT)
    );

    this.directional.bind();
    glTexParameterfv(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_BORDER_COLOR, borderColor);

    this.directional.bind();
    glTexParameterfv(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_BORDER_COLOR, borderColor);

    TextureArray2D depth = new TextureArray2D(
      Settings.geti("ShadowMapResolution"),
      Settings.geti("ShadowMapResolution"),
      Settings.geti("Max2DShadowMaps"),
      new TextureFilterLinear(),
      new TextureWrap(GL_CLAMP_TO_BORDER),
      new TextureFormat(GL_DEPTH_COMPONENT32, GL_DEPTH_COMPONENT, GL_FLOAT)
    );

    depth.bind();
    glTexParameterfv(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_BORDER_COLOR, borderColor);

    // create buffers
    this.maps2D = BufferUtils.createIntBuffer(Settings.geti("Max2DShadowMaps") + 1);
    for (int i = 0; i < Settings.geti("Max2DShadowMaps"); i++)
    {
      this.maps2D.put(GL_COLOR_ATTACHMENT0 + i);
      this.directional.bind();
      glFramebufferTextureLayer(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0 + i, this.directional.getID(), 0, i);

      depth.bind();
      //glFramebufferTexture3D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D_ARRAY, depth.getID(), 0, 0);
      glFramebufferTextureLayer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, depth.getID(),0, i);
      System.err.println(i);
    }
    // temporary blur texture
    this.blur_temp.bind();
    glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT8, GL_TEXTURE_2D, this.blur_temp.getID(), 0);

    System.err.println(glCheckFramebufferStatus(GL_FRAMEBUFFER) == GL_FRAMEBUFFER_COMPLETE);

    this.maps2D.put(GL_COLOR_ATTACHMENT8);
    this.maps2D.flip();

    this.directional.unbind();
    depth.unbind();*/
}
