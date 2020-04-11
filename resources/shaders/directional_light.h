struct DirectionalLight_t
{
  vec4 pos;
  vec4 dir;
  vec4 col;
  int shadow;
};

uniform sampler2DArray u_directional_shadows;
uniform mat4 u_lsm;

layout (std140, binding = 2) uniform ub_directional_lights
{
  DirectionalLight_t u_directional_lights[256];
  int u_num_directional_lights;
};