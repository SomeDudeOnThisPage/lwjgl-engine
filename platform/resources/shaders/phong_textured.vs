#version 450 core

#include <shared.core.glsl>
#include <shared.model.glsl>
#include <shared.time.glsl>

layout (location = ATTRIBUTE_POSITION_LOCATION) in vec3 v_position;
layout (location = ATTRIBUTE_TEXTURE_LOCATION)  in vec2 v_texture;
layout (location = ATTRIBUTE_NORMAL_LOCATION)   in vec3 v_normal;

out VS_OUT
{
  vec3 v_normal;
  vec3 f_position;
  vec2 f_texture;
} o;

void main()
{
  vec3 ppos = v_position;
  ppos.y += sin(ppos.x * u_time * 2.0f) * 0.5f;

  o.v_normal = mat3(transpose(inverse(u_model))) * v_normal;
  o.f_position = (u_model * vec4(v_position, 1.0f)).xyz;
  o.f_texture = v_texture;
  gl_Position = u_projection * u_view * u_model * vec4(ppos, 1.0f);
}