/*
  Add custom shared shader code here.
  Everything in here will be included in every shader program created.
  Make sure to not override anything inside shared.h.
*/

/*
  Lights are stored in the corresponding uniform buffer.
  They can be accessed via gl_InstanceID, as bounding spheres are rendered in an instanced manner.
*/
/*layout (std140, binding = 1) uniform ub_point_lights
{
  PointLight_t u_point_lights[256]; // 256 * 16 * 3
  int u_num_point_lights; // deprecated
};*/

/*out VS_OUT
{
  vec2 uv;
} o;

uniform PointLight_t light;

void main()
{
  //PointLight_t light = u_point_lights[gl_InstanceID];

  float maximum = max(max(light.col.x, light.col.y), light.col.z);
  float radius =(-light.clq.x +  sqrt(light.clq.x * light.clq.x - 4 * light.clq.z * (light.clq.y - (256.0 / 5.0) * maximum))) / (2 * light.clq.z);

  mat4 transform = mat4(
    vec4(radius, 0, 0, 0),
    vec4(0, radius, 0, 0),
    vec4(0, 0, radius, 0),
    vec4(light.pos.x, light.pos.y, light.pos.z, 1)
  );

  o.uv = v_uv;
  vec4 position = u_projection * u_view * transform * vec4(v_position, 1.0f);
  gl_Position = position;
}*/