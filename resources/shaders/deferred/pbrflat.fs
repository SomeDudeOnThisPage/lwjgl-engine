#include "shared.h"
#include <material.h>

in VS_OUT
{
  vec3 v_position;
  vec2 v_texture;
  vec3 v_normal;
  mat3 tbn;
} i;

// one PBRFlat material consumes 8 uniform components, of which we have 65536 (GL_MAX_UNIFORM_BUFFER_SIZE)
// assuming 65536 gives us backwards-compatability up to the NVidia Geforce GTX 660
// todo: some more research, maybe this limit has to be lowered...
// 4 + 1 + 1 + 1 + 1
// this means we can have a total of (65536 / 8) = 8192 PBRFlat materials bound at once
// todo: put all PBRFlat materials in one uniform buffer, and just upload one integer to index the material to the shader
// probably put the material index in a uniform buffer aswell mapping them to the material index
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