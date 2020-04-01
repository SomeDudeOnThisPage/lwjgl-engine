#include "shared.h"
#include <material.h>

in VS_OUT
{
  vec3 f_position;
  vec2 v_texture;
  mat3 tbn;
} i;

uniform PBR_Material_t u_pbr_material;

void main()
{
  vec3 normal = texture(u_pbr_material.normal, i.v_texture).rgb;
  normal = normalize(normal * 2.0 - 1.0);
  normal = normalize(i.tbn * normal);

  // fragment position data
  gl_FragData[DEF_POSITION_TEXTURE_SLOT].rgb = i.f_position;

  // fragment normal data
  gl_FragData[DEF_NORMAL_TEXTURE_SLOT].rgb = normal;

  // albedo texture data
  gl_FragData[DEF_ALBEDO_TEXTURE_SLOT].rgb = texture(u_pbr_material.albedo, i.v_texture).rgb;

  gl_FragData[DEF_R_AO_G_ROUGHNESS_B_METALLIC_TEXTURE_SLOT].r = texture(u_pbr_material.ambient_occlusion, i.v_texture).r;
  gl_FragData[DEF_R_AO_G_ROUGHNESS_B_METALLIC_TEXTURE_SLOT].g = texture(u_pbr_material.roughness, i.v_texture).r;
  gl_FragData[DEF_R_AO_G_ROUGHNESS_B_METALLIC_TEXTURE_SLOT].b = texture(u_pbr_material.metallic, i.v_texture).r;
  gl_FragData[DEF_R_AO_G_ROUGHNESS_B_METALLIC_TEXTURE_SLOT].a = texture(u_pbr_material.emission, i.v_texture).r;

  // unused
  gl_FragData[DEF_POSITION_TEXTURE_SLOT].a = 1.0f;
  gl_FragData[DEF_NORMAL_TEXTURE_SLOT].a = 1.0f;
  gl_FragData[DEF_ALBEDO_TEXTURE_SLOT].a = 1.0f;
}