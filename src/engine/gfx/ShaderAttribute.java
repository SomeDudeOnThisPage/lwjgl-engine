package engine.gfx;

import engine.gfx.buffer.VertexArray;
import engine.gfx.buffer.VertexDataType;

/**
 * The {@link ShaderAttribute} locations of this engine are set statically, in this file.
 * Doing it this way reduces the amount of needed glGetAttribLocation calls, and allows any {@link ShaderProgram} to be
 * used with any {@link VertexArray}. Note that any enum set here must mirror the values set in
 * {@code resources/shader/shared/core.glsl}, and vice versa.
 */
public enum ShaderAttribute
{
  // these values should mirror the values set in resources/shader/shared/core.glsl
  V_POSITION( "v_position",  0, VertexDataType.FLOAT3),
  V_NORMAL(   "v_normal",    1, VertexDataType.FLOAT3),
  V_TEXTURE(  "v_texture",   2, VertexDataType.FLOAT2),
  V_COLOR(    "v_color",     2, VertexDataType.FLOAT3),
  V_TANGENT(  "v_tangent",   3, VertexDataType.FLOAT3),
  V_BITANGENT("v_bitangent", 4, VertexDataType.FLOAT3);

  private final String name;
  private final int location;
  private final VertexDataType data;

  public String getName()
  {
    return this.name;
  }

  public int getLocation()
  {
    return this.location;
  }

  public VertexDataType getDataType()
  {
    return this.data;
  }

  ShaderAttribute(String name, int location, VertexDataType data)
  {
    this.name = name;
    this.location = location;
    this.data = data;
  }
}
