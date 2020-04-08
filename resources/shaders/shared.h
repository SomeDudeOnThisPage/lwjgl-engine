#define LIGHTS_MAX 256

uniform float u_time;

/*
  Constant uniform shader data.
  This struct contains all commonly used, shared shader variables like view / projection matrices,
  global (sun) light data, other light data and convenience variables like the view position in 3D space.
*/
layout (std140, binding = 7) uniform ub_layout_default
{
  mat4 u_projection;      // camera projection matrix              // 64
  mat4 u_view;            // camera view matrix                    // 64
  vec4 u_view_position;                                            // 16
  vec4 u_screen_size;                                              // 16
};