// todo: make these configurable dynamically
#define DIRECTIONAL_LIGHT_UNIFORM_BUFFER_LOCATION 1
#define POINT_LIGHT_UNIFORM_BUFFER_LOCATION 2
#define CONE_LIGHT_UNIFORM_BUFFER_LOCATION 3

// todo: make these configurable dynamically
#define DIRECTIONAL_LIGHT_MAX_INSTANCES 64
#define POINT_LIGHT_MAX_INSTANCES 512
#define CONE_LIGHT_MAX_INSTANCES 512

struct DirectionalLight
{
  vec3 dir;
  vec3 col;
  // vec3 clq; // directional lights are currently global
};

struct PointLight
{
  vec4 pos;
  vec4 col;
  vec4 clq;
};

// struct ConeLight
// {
//   vec4 what;
//   vec4 the;
//   vec4 fuck;
// };

layout (std140, binding = DIRECTIONAL_LIGHT_UNIFORM_BUFFER_LOCATION) uniform ub_DIRECTIONAL_LIGHTS
{
  DirectionalLight u_directional_light[DIRECTIONAL_LIGHT_MAX_INSTANCES];
  uint u_num_directional_lights;
};

layout (std140, binding = POINT_LIGHT_UNIFORM_BUFFER_LOCATION) uniform ub_POINT_LIGHTS
{
  PointLight u_point_light[POINT_LIGHT_MAX_INSTANCES];
  uint u_num_point_lights;
};

// layout (std140, binding = CONE_LIGHT_UNIFORM_BUFFER_LOCATION) uniform ub_CONE_LIGHTS
// {
//   ConeLight u_cone_lights[CONE_LIGHT_MAX_INSTANCES];
//   uint u_num_cone_lights;
// };