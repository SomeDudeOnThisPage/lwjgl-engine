#include <tonemap.h>

in VS_OUT
{
  vec2 uv;
} i;

uniform struct
{
  sampler2D position;
  sampler2D normal;
  sampler2D albedo;
  sampler2D roughness_metallic_ao;
} u_gbuffer;

uniform sampler2D color;

void main()
{
  float emissive = texture2D(u_gbuffer.roughness_metallic_ao, i.uv).a;
  vec3 albedo = texture2D(u_gbuffer.albedo, i.uv).rgb;

  vec3 mapped = texture(color, i.uv).rgb;
  mapped += emissive * albedo;

  // apply exposure
  mapped = tm_exposure(mapped);

  // apply tone mapping
  mapped = tm_reinhard(mapped);//tm_burgess(mapped);

  // apply gamma correction
  mapped = tm_gamma(mapped);

  gl_FragColor = vec4(mapped, 1.0f);
}