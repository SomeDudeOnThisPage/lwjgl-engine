in vec3 fragment_position;

uniform samplerCube skybox;

void main()
{
  gl_FragColor = texture(skybox, fragment_position);
}