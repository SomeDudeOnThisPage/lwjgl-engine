in vec2 quad_texture_coordinates;

uniform sampler2D u_shadow_map;

void main()
{
  gl_FragColor = vec4(texture(u_shadow_map, quad_texture_coordinates).xyz, 1.0f);
}