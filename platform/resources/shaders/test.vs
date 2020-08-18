#version 450 core

#include <shared.core.glsl>

layout (location = ATTRIBUTE_POSITION_LOCATION) in vec3 v_position;
layout (location = ATTRIBUTE_NORMAL_LOCATION)   in vec3 v_normal;

uniform mat4 u_model;

out VS_OUT
{
  vec3 v_normal;
  vec3 f_position;
} o;

void main()
{
  o.v_normal = mat3(transpose(inverse(u_model))) * v_normal;
  o.f_position = (u_model * vec4(v_position, 1.0f)).xyz;
  gl_Position = u_projection * u_view * u_model * vec4(v_position, 1.0f);
}