#include "shared.h"
#include <material.h>

in VS_OUT
{
  vec3 v_position;
  vec2 v_texture;
  vec3 v_normal;
  mat3 tbn;
} i;

/*struct
{
  vec4 color;         // 16
  float ao;           //  4
  float roughness;    //  4
  float metallic;     //  4
  float emissive;     //  4
} PBRMaterial_Flat_t; // 32 per material

layout (std140, binding = 4) uniform ub_material_pbrflat
{
  PBRMaterial_Flat_t u_materials[1024];
};*/

// uniform int u_material;

uniform struct
{
  vec3 color;
  float ao;
  float roughness;
  float metallic;
  float emissive;
} u_material;

void main()
{
  vec3 normal = vec3(128, -128, 128);
  normal = normalize(normal * 2.0 - 1.0);
  normal = normalize(i.tbn * normal);

  // fragment position data
  gl_FragData[DEF_POSITION_TEXTURE_SLOT].rgb = i.v_position;

  // fragment normal data
  gl_FragData[DEF_NORMAL_TEXTURE_SLOT].rgb = normal;

  // albedo texture data
  gl_FragData[DEF_ALBEDO_TEXTURE_SLOT].rgb = u_material.color;

  gl_FragData[DEF_R_AO_G_ROUGHNESS_B_METALLIC_TEXTURE_SLOT].r = u_material.ao;
  gl_FragData[DEF_R_AO_G_ROUGHNESS_B_METALLIC_TEXTURE_SLOT].g = u_material.roughness;
  gl_FragData[DEF_R_AO_G_ROUGHNESS_B_METALLIC_TEXTURE_SLOT].b = u_material.metallic;
  gl_FragData[DEF_R_AO_G_ROUGHNESS_B_METALLIC_TEXTURE_SLOT].a = u_material.emissive;

  // unused
  gl_FragData[DEF_POSITION_TEXTURE_SLOT].a = 1.0f;
  gl_FragData[DEF_NORMAL_TEXTURE_SLOT].a = 1.0f;
  gl_FragData[DEF_ALBEDO_TEXTURE_SLOT].a = 1.0f;
}