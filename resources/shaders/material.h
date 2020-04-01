#define GENERIC_MATERIAL_TEXTURES 8

#define PBR_ALBEDO_TEXTURE_SLOT 0
#define PBR_NORMAL_MAP_SLOT 1
#define PBR_METALLIC_MAP_SLOT 2
#define PBR_ROUGHNESS_TEXTURE_SLOT 3

#define DEF_POSITION_TEXTURE_SLOT 0
#define DEF_NORMAL_TEXTURE_SLOT 1
#define DEF_ALBEDO_TEXTURE_SLOT 2
#define DEF_R_AO_G_ROUGHNESS_B_METALLIC_TEXTURE_SLOT 3

struct Material_t
{
  vec3 ambient;
  vec3 diffuse;
  vec3 specular;
  float shininess;

  sampler2D texture[GENERIC_MATERIAL_TEXTURES];
};

struct PBR_Material_t
{
  sampler2D albedo;
  sampler2D normal;
  sampler2D metallic;
  sampler2D roughness;
  sampler2D ambient_occlusion;
  sampler2D emission;
};

