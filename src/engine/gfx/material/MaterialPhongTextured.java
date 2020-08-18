package engine.gfx.material;

import engine.gfx.ShaderProgram;
import engine.gfx.Texture2D;
import org.joml.Vector3f;
import org.w3c.dom.Element;

import static engine.Engine.*;

public class MaterialPhongTextured extends MaterialArchetype
{
  private Texture2D diffuse;
  private Texture2D specular;

  private Vector3f ambient;
  private float shininess;

  @Override
  public void load(Element xml) {}

  @Override
  public void dispose()
  {
    AssetManager.release(this.diffuse, Texture2D.class);
    AssetManager.release(this.specular, Texture2D.class);
  }

  @Override
  public void bind()
  {
    this.shader.bind();

    this.diffuse.bind(0);
    this.specular.bind(1);

    this.shader.setUniform("u_material.diffuse", 0);
    this.shader.setUniform("u_material.specular", 1);

    this.shader.setUniform("u_material.ambient", this.ambient);
    this.shader.setUniform("u_material.shininess", this.shininess);
  }

  @Override
  public void unbind()
  {
    this.specular.unbind();
    this.diffuse.unbind();
    this.shader.unbind();
  }

  public MaterialPhongTextured(String diffuse, String specular, Vector3f ambient, float shininess)
  {
    if (!AssetManager.getFamily(ShaderProgram.class).contains("phong.textured"))
    {
      AssetManager.load("colors_phong.mtl");
    }

    this.shader = AssetManager.request("phong.textured", ShaderProgram.class);
    this.diffuse = AssetManager.request(diffuse, Texture2D.class);
    this.specular = AssetManager.request(specular, Texture2D.class);

    this.ambient = ambient;
    this.shininess = shininess;
  }
}
