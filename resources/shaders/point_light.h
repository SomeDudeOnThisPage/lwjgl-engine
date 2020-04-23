//#define MAX_SHADOW_MAPS ENGINE_IMPORT_VALUE maxshadowmaps
// better syntax, to be implemented: '#import variablename : DEFINITION'
// or: '#import variablename', setting the definition to the variable name directly
// #import maxshadowmaps : MAX_SHADOW_MAPS

#define MAX_POINT_LIGHTS ENGINE_IMPORT_VALUE_INTEGER <MaxPointLights>

/**
  struct PointLight_t
  A PointLight_t contains required data to render a light radiating in all directions from a set point in space.
  Position, color, and the constant-linear-quadratic-factor are three-component vectors padded to four-component
  vectors in order to enable easy storage in uniform buffer objects. The fourth component of these vectors is to
  be assumed to be undefined.
*/
struct PointLight_t
{
  vec4 pos;
  vec4 col;
  vec4 clq;
  // int shadow; // todo
};

/*
// possibly forego using bindless textures in favour of compatability?
// and set some int in a point-lights struct, ignoring shadows if it's -1?
// BETTER: ARRAY TEXTURES!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! todo: read more about it...
// problem: all shadow maps would need to have the same resolution...
*/

layout (std140, binding = 1) uniform ub_point_lights
{
  PointLight_t u_point_lights[MAX_POINT_LIGHTS]; // 256 * 16 * 3
  int u_num_point_lights;
};