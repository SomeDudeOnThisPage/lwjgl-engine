#include "shared.h"
#include <material.h>

in GS_OUT
{
  vec3 f_position;
  vec2 f_texture;
  mat3 v_tbn;
  vec3 v_normal;
} i;

/*in VS_OUT
{
  vec3 f_position;
  vec2 v_texture;
} i;*/

in vec3 fs_position;
in vec2 fs_texture;

uniform Material_t u_material;

uniform PBR_Material_t u_pbr_material;

void main()
{
  vec3 normal = texture(u_pbr_material.normal, i.f_texture).rgb;
  normal = normalize(normal * 2.0 - 1.0);
  normal = normalize(i.v_tbn * normal);

  //normal = i.v_normal;

  // fragment position data
  gl_FragData[DEF_POSITION_TEXTURE_SLOT].rgb = i.f_position;

  // fragment normal data
  gl_FragData[DEF_NORMAL_TEXTURE_SLOT].rgb = normal;

  // albedo texture data
  gl_FragData[DEF_ALBEDO_TEXTURE_SLOT].rgb = texture(u_pbr_material.albedo, i.f_texture).rgb;

  gl_FragData[DEF_R_AO_G_ROUGHNESS_B_METALLIC_TEXTURE_SLOT].r = texture(u_pbr_material.ambient_occlusion, i.f_texture).r;
  gl_FragData[DEF_R_AO_G_ROUGHNESS_B_METALLIC_TEXTURE_SLOT].g = texture(u_pbr_material.roughness, i.f_texture).r;
  gl_FragData[DEF_R_AO_G_ROUGHNESS_B_METALLIC_TEXTURE_SLOT].b = texture(u_pbr_material.metallic, i.f_texture).r;
  //gl_FragData[DEF_R_AO_G_ROUGHNESS_B_METALLIC_TEXTURE_SLOT].b = 1.0f;

  // unused
  gl_FragData[DEF_POSITION_TEXTURE_SLOT].a = 1.0f;
  gl_FragData[DEF_NORMAL_TEXTURE_SLOT].a = 1.0f;
  gl_FragData[DEF_ALBEDO_TEXTURE_SLOT].a = 1.0f;
  gl_FragData[DEF_R_AO_G_ROUGHNESS_B_METALLIC_TEXTURE_SLOT].a = 1.0f;
}