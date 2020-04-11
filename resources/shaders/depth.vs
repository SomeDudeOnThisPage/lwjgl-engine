#include "shared.h"

layout (location = 0) in vec3 v_position;

uniform mat4 u_light_space;
uniform mat4 u_model;

out VS_OUT
{
  vec2 uv;
} o;

void main()
{
  /*u_projection * u_view * u_model * vec4(v_position, 1.0);*/
  gl_Position = u_light_space * u_model * vec4(v_position, 1.0);
}