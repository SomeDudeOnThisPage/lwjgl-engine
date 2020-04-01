#include <point_light.h>
#include <directional_light.h>

const float PI = 3.14159265359;

float DistributionGGX(vec3 N, vec3 H, float roughness)
{
  float a      = roughness*roughness;
  float a2     = a*a;
  float NdotH  = max(dot(N, H), 0.0);
  float NdotH2 = NdotH*NdotH;

  float num   = a2;
  float denom = (NdotH2 * (a2 - 1.0) + 1.0);
  denom = PI * denom * denom;

  return num / denom;
}

float GeometrySchlickGGX(float NdotV, float roughness)
{
  float r = (roughness + 1.0);
  float k = (r*r) / 8.0;

  float num   = NdotV;
  float denom = NdotV * (1.0 - k) + k;

  return num / denom;
}
float GeometrySmith(vec3 N, vec3 V, vec3 L, float roughness)
{
  float NdotV = max(dot(N, V), 0.0);
  float NdotL = max(dot(N, L), 0.0);
  float ggx2  = GeometrySchlickGGX(NdotV, roughness);
  float ggx1  = GeometrySchlickGGX(NdotL, roughness);

  return ggx1 * ggx2;
}

vec3 fresnelSchlick(float cosTheta, vec3 F0)
{
  return F0 + (1.0 - F0) * pow(1.0 - cosTheta, 5.0);
}

vec3 lighting_directional_pbr(DirectionalLight_t light, vec3 F0, vec3 f_position, vec3 f_normal, vec3 f_albedo, float f_roughness, float f_metallic, vec3 dv_view)
{
  vec3 dv_light = normalize(-light.dir.xyz);
  vec3 dv_half = normalize(dv_view + dv_light);

  //float distance = length(light.pos.xyz - f_position);
  //float attenuation = 1.0 / (light.clq.x + light.clq.y * (distance) + light.clq.z * (distance * distance));
  vec3 radiance = light.col.xyz;// * attenuation;

  float ndf = DistributionGGX(f_normal, dv_half, f_roughness);
  float g = GeometrySmith(f_normal, dv_view, dv_light, f_roughness);
  vec3 f = fresnelSchlick(max(dot(dv_half, dv_view), 0.0), F0);

  vec3 kS = f;
  vec3 kD = vec3(1.0) - kS;
  kD *= 1.0 - f_metallic;

  vec3 numerator = ndf * g * f;
  float denominator = 4.0 * max(dot(f_normal, dv_view), 0.0) * max(dot(f_normal, dv_light), 0.0);
  vec3 specular = numerator / max(denominator, 0.04) * radiance;

  // addEntity to outgoing radiance Lo
  float NdotL = max(dot(f_normal, dv_light), 0.0);

  return (kD * f_albedo / PI + specular) * radiance * NdotL;
}

vec3 lighting_point_pbr(PointLight_t light, vec3 F0, vec3 f_position, vec3 f_normal, vec3 f_albedo, float f_roughness, float f_metallic, vec3 dv_view)
{
  vec3 dv_light = normalize(light.pos.xyz - f_position);
  vec3 dv_half = normalize(dv_view + dv_light);

  float distance = length(light.pos.xyz - f_position);
  //float attenuation = max(0.0, 1.0 - dot(distance, distance));
  //1.0 - ((distance * distance) / (light.clq.z * light.clq.z));
  //float attenuation = (light.clq.x + light.clq.y * (distance) + light.clq.z * (distance * distance));
  float attenuation = clamp(1.0 - (/*light.clq.x + light.clq.y * (distance) + */light.clq.z * (distance * distance)),
  0.0, 1.0) / (distance * distance);

  /*if (distance > light.clq.x)
  {
    attenuation = 0.0f;
  }*/

  vec3 radiance = light.col.xyz * attenuation;

  float ndf = DistributionGGX(f_normal, dv_half, f_roughness);
  float g = GeometrySmith(f_normal, dv_view, dv_light, f_roughness);
  vec3 f = fresnelSchlick(max(dot(dv_half, dv_view), 0.0), F0);

  vec3 kS = f;
  vec3 kD = vec3(1.0) - kS;
  kD *= 1.0 - f_metallic;

  vec3 numerator = ndf * g * f;
  float denominator = 4.0 * max(dot(f_normal, dv_view), 0.0) * max(dot(f_normal, dv_light), 0.0);
  vec3 specular = numerator / max(denominator, 0.01);

  // add to outgoing radiance Lo
  float NdotL = max(dot(f_normal, dv_light), 0.0);

  return (kD * f_albedo / PI + specular) * (radiance) * NdotL;
}