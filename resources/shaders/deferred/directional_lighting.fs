#include "shared.h"
#include <deferred/lighting.h>
#include <directional_light.h>

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

void main()
{
  vec3  position  = texture(u_gbuffer.position, i.uv).rgb;
  vec3  normal    = normalize(texture(u_gbuffer.normal, i.uv).rgb);
  vec3  albedo    = texture(u_gbuffer.albedo, i.uv).rgb;
  float ao        = texture(u_gbuffer.roughness_metallic_ao, i.uv).r;
  float roughness = texture(u_gbuffer.roughness_metallic_ao, i.uv).g;
  float metallic  = texture(u_gbuffer.roughness_metallic_ao, i.uv).b;
  float emission  = texture(u_gbuffer.roughness_metallic_ao, i.uv).a;

  vec3 F0 = vec3(0.04f);
  F0 = mix(F0, albedo, metallic);

  vec3 dv_view = normalize(-u_view_position.xyz - position);
  vec3 lighting = vec3(0.0f);

  for (int i = 0; i < u_num_directional_lights; i++)
  {
    lighting += lighting_directional_pbr(u_directional_lights[i], F0, position, normal, albedo, roughness, metallic, dv_view);
    lighting += vec3(0.03) * albedo * (0.1 * ao);
  }

  gl_FragColor = vec4(lighting, 1.0f);
}