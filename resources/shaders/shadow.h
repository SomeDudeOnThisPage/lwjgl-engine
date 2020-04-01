float shd_shadow(vec4 position, float normal_light, in sampler2D map)
{
  vec3 projected = position.xyz / position.w;
  projected = projected * 0.5 + 0.5;

  if(projected.z > 1.0)
  {
    return 0.0f;
  }

  float current = projected.z;
  float bias = max(0.005 * (1.0 - normal_light), 0.0005);
  float closest = texture(map, projected.xy).r;

  float o_shadow = 0.0;
  vec2 t_size = 2.0f / textureSize(map, 0);

  for(int x = -2; x <= 2; ++x)
  {
    for(int y = -2; y <= 2; ++y)
    {
      float pcf = texture(map, projected.xy + vec2(x, y) * t_size).r;
      o_shadow += current - bias > pcf ? 1.0 : 0.0;
    }
  }
  o_shadow /= 25.0;

  // float bias = max(0.05 * (1.0 - normal_light), 0.005);
  // float o_shadow = current - bias > closest  ? 1.0 : 0.0;

  return o_shadow;
}