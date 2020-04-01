#include "shared.h"

layout (triangles) in;
layout (triangle_strip, max_vertices = 3) out;

out GS_OUT
{
  vec3 f_position;
  vec2 f_texture;
  mat3 v_tbn;
  vec3 v_normal;
} o;

in VS_OUT
{
  vec3 f_position;
  vec2 f_texture;
  vec3 v_normal;
} i[3];

uniform mat4 u_model;

vec3 surface_normal()
{
  vec3 tangent1 = gl_in[1].gl_Position.xyz - gl_in[0].gl_Position.xyz;
  vec3 tangent2 = gl_in[2].gl_Position.xyz - gl_in[0].gl_Position.xyz;
  vec3 normal = cross(tangent1, tangent2);

  return normalize(normal);
}

vec3 tangent(vec3 v0, vec3 v1, vec3 v2, vec2 uv0, vec2 uv1, vec2 uv2)
{
  // edges of the face/triangle
  vec3 e1 = v1 - v0;
  vec3 e2 = v2 - v0;

  vec2 deltaUV1 = uv1 - uv0;
  vec2 deltaUV2 = uv2 - uv0;

  float r = 1.0 / (deltaUV1.x * deltaUV2.y - deltaUV1.y * deltaUV2.x);

  return normalize((e1 * deltaUV2.y - e2 * deltaUV1.y)*r);
}

void main()
{
  for(int j = 0; j < 3; j++)
  {
    vec3 normal = normalize(u_model * vec4(i[j].v_normal, 1.0f)).xyz;
    //vec3 normal = surface_normal();
    vec3 tangent = tangent(i[0].f_position, i[1].f_position, i[2].f_position, i[0].f_texture, i[1].f_texture, i[2].f_texture);
    vec3 bitangent = normalize(cross(normal, tangent));
    o.v_tbn = mat3(tangent, bitangent, normal);

    o.f_position = i[j].f_position;
    o.f_texture = i[j].f_texture;
    o.v_normal = normalize(u_model * vec4(i[j].v_normal, 1.0f)).xyz;

    gl_Position = gl_in[j].gl_Position;
    EmitVertex();
  }

  EndPrimitive();
}