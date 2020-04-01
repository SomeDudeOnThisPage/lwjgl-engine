package engine.core.entity.component;

import engine.core.gfx.Cubemap;
import engine.core.gfx.VertexArray;
import engine.util.Assimp;

public class SkyboxComponent extends EntityComponent
{
  // constant cube skybox
  public static final VertexArray mesh = Assimp.load_static("cube")[0];

  public Cubemap texture;

  public SkyboxComponent(String path)
  {
    this.texture = new Cubemap("skybox/" + path);
  }
}
