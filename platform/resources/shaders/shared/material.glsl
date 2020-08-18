struct MaterialPhong
{
  vec3 ambient;
  vec3 diffuse;
  vec3 specular;
  float shininess;
};

struct MaterialPhongTextured
{
  vec3 ambient;
  sampler2D diffuse;
  sampler2D specular;
  float shininess;
};