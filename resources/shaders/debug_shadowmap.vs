layout (location = 0) in vec3 v_position;
layout (location = 1) in vec2 v_texture;

out vec2 quad_texture_coordinates;

void main()
{
  quad_texture_coordinates = v_texture;
  gl_Position = vec4(v_position.x, v_position.y, 0.0, 1.0);
}