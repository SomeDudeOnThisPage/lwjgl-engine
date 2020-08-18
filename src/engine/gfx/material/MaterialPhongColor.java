package engine.gfx.material;

import engine.entity.EditorField;
import engine.gfx.ShaderProgram;
import org.joml.Vector3f;
import org.w3c.dom.Element;

import static engine.Engine.*;

public class MaterialPhongColor extends MaterialArchetype
{
  @EditorField("color")
  private final Vector3f ambient;

  @EditorField("color")
  private final Vector3f diffuse;

  @EditorField("color")
  private final Vector3f specular;

  private final float shininess;

  @Override
  public void bind()
  {
    this.shader.bind();
    this.shader.setUniform("u_material.ambient", this.ambient);
    this.shader.setUniform("u_material.diffuse", this.diffuse);
    this.shader.setUniform("u_material.specular", this.specular);
    this.shader.setUniform("u_material.shininess", this.shininess);
  }

  @Override
  public void unbind()
  {
    this.shader.unbind();
  }

  @Override
  public void dispose()
  {
    AssetManager.release(this.shader, ShaderProgram.class);
  }

  @Override
  public void load(Element xml) {}

  public MaterialPhongColor(Vector3f ambient, Vector3f diffuse, Vector3f specular, float shininess)
  {
    this.shader = AssetManager.request("phong_color", ShaderProgram.class);
    this.ambient = ambient;
    this.diffuse = diffuse;
    this.specular = specular;
    this.shininess = shininess;
  }
}
