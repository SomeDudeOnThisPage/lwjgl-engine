#include "shared.h"
#include <material.h>
#include <deferred/lighting.h>
//#include <point_light.h>
#include <tonemap.h>
#include <shadow.h>

const bool hdr_enabled = true;

in vec2 quad_texture_coordinates;

uniform struct
{
  sampler2D position;
  sampler2D normal;
  sampler2D albedo;
  sampler2D specular;
} u_gbuffer;

uniform sampler2D u_shadow_depth;
uniform mat4 u_model_lightspace;

void main()
{
  vec3 position = texture(u_gbuffer.position, quad_texture_coordinates).rgb;
  vec3 normal = normalize(texture(u_gbuffer.normal, quad_texture_coordinates).rgb);
  vec3 albedo = texture(u_gbuffer.albedo, quad_texture_coordinates).rgb;
  float ao = texture(u_gbuffer.specular, quad_texture_coordinates).r;
  float roughness = texture(u_gbuffer.specular, quad_texture_coordinates).g;
  float metallic = texture(u_gbuffer.specular, quad_texture_coordinates).b;

  vec4 shadow_coordinate = u_model_lightspace * vec4(position, 1.0f);

  vec3 dv_view = normalize(-u_view_position.xyz - position);
  vec3 dv_light = normalize(-u_view_position.xyz - vec3(0.0f));

  float shadow = shd_shadow(shadow_coordinate, dot(normal, dv_light), u_shadow_depth);//texture(u_shadow_depth, shadow_coordinate.xy).z;
  vec3 lighting = vec3(0.0f, 0.0f, 0.0f);

  vec3 F0 = vec3(0.04f);
  F0 = mix(F0, albedo, metallic);

  for (int i = 0; i < u_num_point_lights; i++)
  {
    lighting += lighting_point_pbr(u_point_lights[i], F0, position, normal, albedo, roughness, metallic, dv_view);
    if (i == 0)
    {
      lighting *= (1.0f - shadow);
    }
  }

  for (int i = 0; i < u_num_directional_lights; i++)
  {
    if (i == 0)
    {
      lighting *= (1.0f - shadow);
    }

    lighting += lighting_directional_pbr(u_directional_lights[i], F0, position, normal, albedo, roughness, metallic, dv_view);
  }

  lighting += vec3(0.03) * albedo * ao;

  vec3 color = tm_exposure(lighting);
  color = tm_burgess(color);////tm_reinhard(lighting);
  color = tm_gamma(color);

  gl_FragColor = vec4(color, 1.0f);
}