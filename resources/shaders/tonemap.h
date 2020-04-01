const float gamma = 2.2f;
const float exposure = 1.115f;

vec3 tm_exposure(vec3 color)
{
  return color * exposure;
}

vec3 tm_gamma(vec3 color)
{
  vec3 ret = pow(color, vec3(1.0f / gamma));
  return ret;
}

vec3 tm_burgess(vec3 color)
{
  vec3 maxc = max(vec3(0.0f), color - 0.004);
  vec3 ret = (maxc * (6.2 * maxc + 0.05f)) / (maxc * (6.2 * maxc + 1.7) + 0.06);
  return ret;
}

vec3 tm_reinhard(vec3 color)
{
  return color / (1.2f + color);
}