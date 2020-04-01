#include "shared.h"

layout (location = 0) in vec3 v_position;

out vec3 fragment_position;

uniform mat4 u_model;

void main()
{
  // remove translation of positions
  mat4 view_centered = mat4(mat3(u_view));

  fragment_position = v_position;
  gl_Position = u_projection * view_centered * u_model * vec4(v_position, 1.0f);
}