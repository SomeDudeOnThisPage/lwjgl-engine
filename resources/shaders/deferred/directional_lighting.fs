#include "shared.h"
#include <deferred/lighting.h>
#include <directional_light.h>
#include <shadow.h>

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
  // read gbuffer
  vec3  position  = texture(u_gbuffer.position, i.uv).rgb;
  vec3  normal    = normalize(texture(u_gbuffer.normal, i.uv).rgb);
  vec3  albedo    = texture(u_gbuffer.albedo, i.uv).rgb;
  float ao        = texture(u_gbuffer.roughness_metallic_ao, i.uv).r;
  float roughness = texture(u_gbuffer.roughness_metallic_ao, i.uv).g;
  float metallic  = texture(u_gbuffer.roughness_metallic_ao, i.uv).b;
  float emission  = texture(u_gbuffer.roughness_metallic_ao, i.uv).a;

  // calculate lighting
  vec3 F0 = vec3(0.04f);
  F0 = mix(F0, albedo, metallic);

  vec3 dv_view = normalize(-u_view_position.xyz - position);
  vec3 dv_light = normalize(-u_view_position.xyz - vec3(0.0f));

  vec3 lighting = vec3(0.0f);

  for (int i = 0; i < u_num_directional_lights; i++)
  {
    vec3 light = lighting_directional_pbr(u_directional_lights[i], F0, position, normal, albedo, roughness, metallic, dv_view);

    if (i == 0 && u_directional_lights[i].shadow != -1)
    {
      // retreive shadow coordinate by multiplying with the lights' view space matrix
      vec4 shadow_coordinate = u_lsm * vec4(position, 1.0f);

      float shadow = shd_shadow(shadow_coordinate, dot(normal, dv_light), shd_map(0));
      light *= (1.0f - shadow);
    }

    lighting += light;
  }

  // add ambient modifier (once)
  lighting += vec3(0.03) * albedo * (0.1 * ao);

  gl_FragColor = vec4(lighting, 1.0f);
}