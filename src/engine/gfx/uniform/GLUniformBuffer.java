package engine.gfx.uniform;

import engine.Engine;
import engine.asset.AssetManager;
import engine.gfx.BaseAssetBindable;
import engine.gfx.buffer.UniformBuffer;
import engine.gfx.opengl.GLState;
import engine.util.Memory;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.HashMap;

import static org.lwjgl.opengl.GL42C.*;

public class GLUniformBuffer extends BaseAssetBindable implements UniformBuffer
{
  /** String identifiers mapped to BufferedUniform instances. */
  private final HashMap<String, BufferedUniform<?>> uniforms;

  /** String identifiers mapped to the absolute locations in this buffer. */
  private final HashMap<String, Long> locations;

  private final int binding;
  private int size;

  @Override
  public int size()
  {
    throw new UnsupportedOperationException("cannot query size of uniform buffer");
  }

  @Override
  public void bind()
  {
    if (GLState.buffer(GL_UNIFORM_BUFFER, this.id))
    {
      glBindBufferBase(GL_UNIFORM_BUFFER, this.binding, this.id);
    }
  }

  @Override
  public void unbind()
  {
    GLState.buffer(GL_UNIFORM_BUFFER, GL_NONE);
  }

  @Override
  public BufferedUniform<?> getUniform(String uniform)
  {
    if (!this.uniforms.containsKey(uniform))
    {
      throw new UnsupportedOperationException("cannot retrieve non-existent uniform '" + uniform + "' from " +
        "UniformBuffer '" + this + "'");
    }

    return this.uniforms.get(uniform);
  }

  /**
   * Moves the data stored in a {@link BufferedUniform} into the actual {@link UniformBuffer Uniform-Buffer-Object}.
   * This can be used to flush updated data from the {@link BufferedUniform}s to the UBO to be used in shaders.
   * @param index The {@link String} name of the {@link BufferedUniform uniform} variable.
   */
  public void update(String index)
  {
    BufferedUniform<?> uniform = this.uniforms.getOrDefault(index, null);
    if (uniform == null)
    {
      throw new UnsupportedOperationException("cannot update non-existent uniform '" + index + "' in" +
        "UniformBuffer '" + this + "'");
    }

    this.bind();
    Long location = this.locations.get(index);

    // this isn't scalable at all, there's gotta be a better way to do this...
    // maybe visitor pattern or make the uniforms set themselves?
    if (uniform.get() instanceof Float)
    {
      Float float1 = (Float) uniform.get();
      Memory.Buffer.buffer1f.put(0, float1);
      glBufferSubData(GL_UNIFORM_BUFFER, location, Memory.Buffer.buffer1f);
    }
    else if (uniform.get() instanceof Integer)
    {
      Integer int1 = (Integer) uniform.get();
      Memory.Buffer.buffer1i.put(0, int1);
      glBufferSubData(GL_UNIFORM_BUFFER, location, Memory.Buffer.buffer1i);
    }
    else if (uniform.get() instanceof Boolean)
    {
      Boolean bool1 = (Boolean) uniform.get();
      Memory.Buffer.buffer1i.put(0, bool1 ? 1 : 0);
      glBufferSubData(GL_UNIFORM_BUFFER, location, Memory.Buffer.buffer1i);
    }
    else if (uniform.get() instanceof Matrix4f)
    {
      Matrix4f mat4 = (Matrix4f) uniform.get();
      mat4.get(Memory.Buffer.buffer16f);

      glBufferSubData(GL_UNIFORM_BUFFER, location, Memory.Buffer.buffer16f);
    }
    else if (uniform.get() instanceof Vector3f)
    {
      Vector3f vec3 = (Vector3f) uniform.get();
      vec3.get(Memory.Buffer.buffer4f);
      Memory.Buffer.buffer4f.put(3, 0.0f); // padding, this data is to be seen as invalid and should not be used
      glBufferSubData(GL_UNIFORM_BUFFER, location, Memory.Buffer.buffer4f);
    }
    else if (uniform.get() instanceof Vector4f)
    {
      Vector4f vec4 = (Vector4f) uniform.get();
      vec4.get(Memory.Buffer.buffer4f);
      glBufferSubData(GL_UNIFORM_BUFFER, location, Memory.Buffer.buffer4f);
    }
    else
    {
      throw new UnsupportedOperationException("attempted to set uniform with invalid data type - how did you even" +
        "manage this?");
    }
  }

  @Override
  public void dispose()
  {
    glDeleteBuffers(this.id);
    Engine.Log.info("disposed of GL UniformBuffer with ID '" + this.id + "'");
  }

  /**
   * Recursively indexes buffered uniforms.
   * @param root The current root string.
   * @param uniform The current uniform.
   */
  public void index(String root, BufferedUniform<?> uniform)
  {
    if (uniform instanceof BufferedUniformStruct<?>)
    {
      BufferedUniformStruct<?> struct = (BufferedUniformStruct<?>) uniform;

      for (BufferedUniform<?> sUniform : struct.getUniforms().values())
      {
        this.index(root + uniform.name() + "." /* append '.' to mirror glsl syntax */, sUniform);
        /*
          this.uniforms.put(root + uniform.name() + "." + sUniform.name(), sUniform);
          this.locations.put(root + uniform.name() + "." + sUniform.name(), (long) this.size);
          System.out.println(root + uniform.name() + "." + sUniform.name() + " " + this.size);
          this.size += sUniform.alignment;
        */
      }
    }
    else if (uniform instanceof BufferedUniformArray<?>)
    {
      int i = 0;
      for (BufferedUniform<?> aUniform : ((BufferedUniformArray<?>) uniform).getUniforms())
      {
        this.index(root + uniform.name() + "[" + (i++) + "]", aUniform);
      }
    }
    else
    {
      this.uniforms.put(root + uniform.name(), uniform);
      this.locations.put(root + uniform.name(), (long) this.size);
      // System.out.println(root + uniform.name() + " " + this.size + " " + uniform.get());
      this.size += uniform.alignment();
    }
  }

  public GLUniformBuffer(int binding, BufferedUniform<?>... uniforms)
  {
    this.id = glGenBuffers();
    this.binding = binding;

    this.uniforms = new HashMap<>();
    this.locations = new HashMap<>();

    this.size = 0;

    for (BufferedUniform<?> uniform : uniforms)
    {
      this.index("" /* base root is empty */, uniform);
    }

    this.bind();
    glBufferData(GL_UNIFORM_BUFFER, this.size, GL_STREAM_DRAW);
    glBindBufferBase(GL_UNIFORM_BUFFER, binding, this.id);
    this.unbind();

    // self-load to asset manager // todo
    AssetManager.getInstance().load(this, "ubo." + this, UniformBuffer.class);

    for (String uniform : this.uniforms.keySet())
    {
      // System.out.println("Initializing uniform " + uniform);
      // System.out.println("Position: " + this.locations.get(uniform));
      // System.out.println("Alignment: " + this.uniforms.get(uniform).alignment);
      // System.out.println("Data: " + this.uniforms.get(uniform).get());
      this.update(uniform);
    }
  }
}
