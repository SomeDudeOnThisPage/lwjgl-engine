#define DEFERRED_TEX_POSITION                       0
#define DEFERRED_TEX_NORMAL                         1
#define DEFERRED_TEX_ALBEDO                         2
#define DEFERRED_TEX_AO_ROUGHNESS_METALLIC_EMISSIVE 3

void deferred_store_position(in vec3 position)
{
  gl_FragData[DEFERRED_TEX_POSITION].rgb = position;
}

void deferred_store_normal(in vec3 normal)
{
  gl_FragData[DEFERRED_TEX_NORMAL].rgb = normal;
}

void deferred_store_albedo(in vec3 albedo)
{
  gl_FragData[DEFERRED_TEX_ALBEDO].rgb = albedo;
}

void deferred_store_ao(in float ao)
{
  gl_FragData[DEFERRED_TEX_AO_ROUGHNESS_METALLIC_EMISSIVE].r = ao;
}

void deferred_store_roughness(in float roughness)
{
  gl_FragData[DEFERRED_TEX_AO_ROUGHNESS_METALLIC_EMISSIVE].g = roughness;
}

void deferred_store_metallic(in float metallic)
{
  gl_FragData[DEFERRED_TEX_AO_ROUGHNESS_METALLIC_EMISSIVE].b = metallic;
}

void deferred_store_emissive(in float emissive)
{
  gl_FragData[DEFERRED_TEX_AO_ROUGHNESS_METALLIC_EMISSIVE].a = emissive;
}