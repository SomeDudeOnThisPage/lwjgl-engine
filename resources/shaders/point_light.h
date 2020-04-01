struct PointLight_t
{
  vec4 pos;
  //vec4 amb;
  vec4 col;
  vec4 clq;
};

layout (std140, binding = 1) uniform ub_point_lights
{
  PointLight_t u_point_lights[256]; // 256 * 16 * 3
  int u_num_point_lights;
};

/*struct PointLight_t
{
  vec3 pos;
//vec4 amb;
  vec3 col;
  vec3 clq;
};*/