#version 450 core

in VS_OUT
{
  vec3 v_normal;
  vec3 f_position;
} i;

const vec3 light_position = vec3(20.0f, 10.0f, 20.0f);
const vec3 light_color = vec3(255.0f, 255.0f, 255.0f);

void main()
{
  vec3 normal = normalize(i.v_normal);
  vec3 light_direction = normalize(light_position - i.f_position);

  vec3 diffuse = max(dot(normal, light_direction), 0.0) * normalize(light_color);

  gl_FragColor = vec4(diffuse, 1.0f);
}