#include "shared.h"

layout (location = 0) in vec3 v_position;
//layout (location = 1) in vec2 v_uv;
//layout (location = 2) in vec3 v_normal;
layout (location = 1) in mat4 v_model;

void main()
{
  gl_Position = u_projection * u_view * v_model * vec4(v_position, 1.0f);
}