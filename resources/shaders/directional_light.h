struct DirectionalLight_t
{
  vec4 pos;
  vec4 dir;
  // vec4 amb;
  vec4 col;
  vec4 clq;
};

layout (std140, binding = 2) uniform ub_directional_lights
{
  DirectionalLight_t u_directional_lights[256];
  int u_num_directional_lights;
};