#version 450 core

#include <shared.core.glsl>
#include <shared.light.glsl>
#include <shared.material.glsl>

in VS_OUT
{
  vec3 v_normal;
  vec3 f_position;
  vec2 f_texture;
} i;

uniform MaterialPhongTextured u_material;

void main()
{
  vec3 normal = normalize(i.v_normal);
  vec3 diffuse = vec3(0.0f);
  vec3 specular = vec3(0.0f);
  vec3 ambient = vec3(0.0f);

  vec3 mtlDiffuse = texture2D(u_material.diffuse, i.f_texture).rgb;
  vec3 mtlSpecular = texture2D(u_material.specular, i.f_texture).rgb;

  for (int k = 0; k < u_num_directional_lights; k++)
  {
    DirectionalLight dlight = u_directional_light[k];
    //ambient += u_material.ambient.xyz;

    vec3 light_direction = normalize(dlight.dir.xyz);

    vec3 view_direction = normalize(u_view_position.xyz - i.f_position);
    vec3 reflect_direction = reflect(light_direction, normal);
    float spec = pow(max(dot(view_direction, normalize(reflect_direction)), 0.0), u_material.shininess * 128.0f);
    specular += dlight.col.xyz * (spec * mtlSpecular);

    diffuse += (max(dot(normal, normalize(light_direction)), 0.0) * normalize(dlight.col.xyz) * normalize(mtlDiffuse));
  }

  for (int j = 0; j < u_num_point_lights; j++)
  {
    // no ambient light for point lights

    PointLight light = u_point_light[j];
    vec3 light_direction = normalize(light.pos.xyz - i.f_position);
    float distance = length(light.pos.xyz - i.f_position);
    float attenuation = 1.0 / (light.clq.x + light.clq.y * distance + light.clq.z * (distance * distance));

    vec3 view_direction = normalize(u_view_position.xyz - i.f_position);
    vec3 reflect_direction = reflect(light_direction, normal);
    float spec = pow(max(dot(view_direction, normalize(reflect_direction)), 0.0), u_material.shininess * 128.0f);

    specular += light.col.xyz * (spec * mtlSpecular) * attenuation;
    diffuse += max(dot(normal, normalize(light_direction)), 0.0) * normalize(light.col.xyz) * mtlDiffuse * attenuation;
  }

  gl_FragColor = vec4(ambient + diffuse + specular, 1.0f);
}