package engine.core.entity.component;

import engine.core.gfx.Shader;

public class ShaderComponent extends EntityComponent
{
  public Shader shader;

  public ShaderComponent(String shader)
  {
    this.shader = Shader.getInstance(shader);
  }
}
