uniform sampler2D u_shadow_map_2D_0;
uniform sampler2D u_shadow_map_2D_1;
uniform sampler2D u_shadow_map_2D_2;
uniform sampler2D u_shadow_map_2D_3;
uniform sampler2D u_shadow_map_2D_4;
uniform sampler2D u_shadow_map_2D_5;
uniform sampler2D u_shadow_map_2D_6;
uniform sampler2D u_shadow_map_2D_7;

sampler2D shd_map(int map)
{
  switch(map)
  {
    case 0:
      return u_shadow_map_2D_0;
    case 1:
      return u_shadow_map_2D_1;
    case 2:
      return u_shadow_map_2D_2;
    case 3:
      return u_shadow_map_2D_3;
    case 4:
      return u_shadow_map_2D_4;
    case 5:
      return u_shadow_map_2D_5;
    case 6:
      return u_shadow_map_2D_6;
    case 7:
      return u_shadow_map_2D_7;
    default:
      return u_shadow_map_2D_0;
  }

  return u_shadow_map_2D_0;
}

float linear_step(float low, float high, float v)
{
  return clamp((v - low) / (high - low), 0.0f, 1.0f);
}

float shd_sample_variance_directional(vec2 uv, float current, in sampler2D map)
{
  vec2 moments = texture(map, uv).xy;

  float p = step(current, moments.x);
  float variance = max(moments.y - moments.x * moments.x, 0.000002f);

  float d =  current - moments.x;
  float p_max = linear_step(0.2f, 1.0f, variance / (variance + d * d));

  return min(max(p, p_max), 1.0f);
}

float shd_shadow(vec4 position, float normal_light, in sampler2D map)
{
  vec3 projected = position.xyz / position.w;
  projected = projected * 0.5 + 0.5;

  if(projected.z > 1.0)
  {
    return 0.0f;
  }

  float o_shadow = 0.0f;
  float current = projected.z;

  o_shadow = shd_sample_variance_directional(projected.xy, current, map);

  return max(0.0f, 1.0f - o_shadow);
}