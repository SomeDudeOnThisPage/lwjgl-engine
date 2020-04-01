#include "shared.h"

layout (location = 0) in vec3 v_position;
layout (location = 1) in vec2 v_texture;
layout (location = 2) in vec3 v_normal;
layout (location = 3) in vec3 v_tangent;
layout (location = 4) in vec3 v_bitangent;

out VS_OUT
{
  vec3 f_position;
  vec2 v_texture;
  mat3 tbn;
} o;

uniform mat4 u_model;

void main()
{
  vec3 tbn_tangent = normalize(vec3(u_model * vec4(normalize(v_tangent), 0.0)));
  vec3 tbn_bitangent = normalize(vec3(u_model * vec4(normalize(v_bitangent), 0.0)));
  vec3 tbn_normal = normalize(vec3(u_model * vec4(normalize(v_normal), 0.0)));
  o.tbn = mat3(tbn_tangent, tbn_bitangent, tbn_normal);

  o.f_position = (u_model * vec4(v_position, 1.0)).xyz;
  o.v_texture = v_texture;

  gl_Position = u_projection * u_view * u_model * vec4(v_position, 1.0f);
}