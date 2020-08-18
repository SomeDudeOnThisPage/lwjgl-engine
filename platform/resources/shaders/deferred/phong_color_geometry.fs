#version 450 core

#include <shared.deferred.core.glsl>
#include <shared.material.glsl>

in VS_OUT
{
  vec3 v_normal;
  vec3 f_position;
} i;

uniform MaterialPhong u_material;

void main()
{
  //deferred_store_position(i.f_position);
  //deferred_store_normal(i.v_normal);
  //deferred_store_albedo(u_material.diffuse);

  gl_FragColor = vec4(0.0f, 1.0f, 0.0f, 1.0f);
}