float shd_shadow(vec4 position, float normal_light, int layer)
{
  vec3 projected = position.xyz / position.w;
  projected = projected * 0.5 + 0.5;

  if(projected.z > 1.0)
  {
    return 0.0f;
  }

  float o_shadow = 0.0f;
  float current = projected.z;
  float bias = max(0.0005f * (1.0 - normal_light), 0.0005f);
  float closest = texture(u_directional_shadows, vec3(projected.xy, 0)).r;

  vec3 t_size = 2.0f / textureSize(u_directional_shadows, 0);
  for(int x = -1; x <= 1; ++x)
  {
    for(int y = -1; y <= 1; ++y)
    {
      float pcf = texture(u_directional_shadows, vec3(projected.xy + vec2(x, y) * t_size.xy, layer)).r;
      o_shadow += current - bias > pcf ? 1.0 : 0.0;
    }
  }
  o_shadow /= 9.0;

  // float bias = max(0.05 * (1.0 - normal_light), 0.005);
  //float o_shadow = current - bias > closest ? 1.0 : 0.0;

  return o_shadow;
}