#include "shared.h"

layout (location = 0) in vec3 v_position;

uniform mat4 u_model_lightspace;

void main()
{
  gl_Position = u_model_lightspace * vec4(v_position, 1.0);
}