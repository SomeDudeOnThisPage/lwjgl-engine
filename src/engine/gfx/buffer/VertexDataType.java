package engine.gfx.buffer;

import static org.lwjgl.opengl.GL42C.*;

public enum VertexDataType
{
  @SuppressWarnings("unused") INT1( 4, 1, GL_INT),
  @SuppressWarnings("unused") INT2( 8, 2, GL_INT),
  @SuppressWarnings("unused") INT3(12, 3, GL_INT),
  @SuppressWarnings("unused") INT4(16, 4, GL_INT),

  @SuppressWarnings("unused") FLOAT1(4,  1, GL_FLOAT),
  @SuppressWarnings("unused") FLOAT2(8,  2, GL_FLOAT),
  @SuppressWarnings("unused") FLOAT3(12, 3, GL_FLOAT),
  @SuppressWarnings("unused") FLOAT4(16, 4, GL_FLOAT),

  @SuppressWarnings("unused") MATRIX2F(16,  4, GL_FLOAT),
  @SuppressWarnings("unused") MATRIX3F(36,  9, GL_FLOAT),
  @SuppressWarnings("unused") MATRIX4F(64, 16, GL_FLOAT),

  @SuppressWarnings("unused") MATRIX2I(16,  4, GL_INT),
  @SuppressWarnings("unused") MATRIX3I(36,  9, GL_INT),
  @SuppressWarnings("unused") MATRIX4I(64, 16, GL_INT),

  @SuppressWarnings("unused") BOOLEAN(1, 1, GL_INT);

  private final int size;
  private final int components;
  private final int gl;

  @SuppressWarnings("unused")
  public int size()
  {
    return this.size;
  }

  @SuppressWarnings("unused")
  public int components()
  {
    return this.components;
  }

  @SuppressWarnings("unused")
  public int gltype()
  {
    return this.gl;
  }

  VertexDataType(int size, int components, int gl)
  {
    this.size = size;
    this.components = components;
    this.gl = gl;
  }
}
