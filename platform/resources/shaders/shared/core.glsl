#ifndef CORE
#define CORE

#define ATTRIBUTE_POSITION_LOCATION 0
#define ATTRIBUTE_NORMAL_LOCATION 1
#define ATTRIBUTE_TEXTURE_LOCATION 2
#define ATTRIBUTE_COLOR_LOCATION 2

#endif

layout (std140, binding = 0) uniform ub_PIPELINE_CORE
{
  mat4 u_projection;
  mat4 u_view;
  vec4 u_view_position;
};