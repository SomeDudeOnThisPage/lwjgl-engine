#include "shared.h"

layout (location = 0) in vec3 v_position;
layout (location = 1) in vec2 v_texture;
layout (location = 2) in vec3 v_normal;

out VS_OUT
{
  vec3 f_position;
  vec2 f_texture;
  vec3 v_normal;
} o;

//out vec3 f_position;
//out vec2 f_texture;

uniform mat4 u_model;

void main()
{
  // output vertex position
  //f_position = (u_model * vec4(v_position, 1.0)).xyz;
  o.f_position = (u_model * vec4(v_position, 1.0)).xyz;
  o.v_normal = v_normal;
  // output texture coordinates
  //f_texture = v_texture;
  o.f_texture = v_texture;
  // vertex position
  gl_Position = u_projection * u_view * u_model * vec4(v_position, 1.0f);
}