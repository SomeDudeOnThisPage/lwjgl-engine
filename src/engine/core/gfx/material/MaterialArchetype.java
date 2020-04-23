package engine.core.gfx.material;

import engine.core.gfx.Shader;
import engine.core.gfx.texture.Texture;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.w3c.dom.Element;

import static org.lwjgl.opengl.GL33C.*;

public abstract class MaterialArchetype
{
  private static final int MAX_TEXTURES = glGetInteger(GL_MAX_TEXTURE_IMAGE_UNITS);

  private Vector3f ambient;
  private Vector3f diffuse;
  private Vector3f specular;
  private float shininess;

  private boolean transparent;

  protected Texture[] textures;

  protected Shader shader;

  /**
   * This method should set the values of {@code this} to the values defined in the {@code <archetype>}-tag in a
   * {@code .mat} file. The method should return {@code this}.
   * <p>See {@link PBRMaterialFlat#load(Element)} for reference.</p>
   * @param xml The XML-{@code <archetype>} element.
   * @return {@code this}.
   */
  public abstract MaterialArchetype load(@NotNull Element xml);

  public boolean transparent()
  {
    return this.transparent;
  }

  /**
   * Adds a texture map to the material.
   */
  public void addMap(int index, Texture texture)
  {
    if (index >= MaterialArchetype.MAX_TEXTURES)
    {
      System.err.println("cannot bind texture to index " + index + " - no more than " + MAX_TEXTURES + " texture samplers allowed");
    }

    this.textures[index] = texture;
  }

  public Shader shader()
  {
    return this.shader;
  }

  public void bind() {}

  /**
   * Sets the required uniforms in the shader for material calculation.
   * Access them as follows:<br>
   * uniform struct<br>
   * {<br>
   *   vec3 ambient;<br>
   *   vec3 diffuse;<br>
   *   vec3 specular;<br>
   *   float shininess;<br>
   * } material;<br>
   * @param shader Shader to set the uniforms of
   */
  public void bind(Shader shader)
  {
    if (shader.getCurrentMaterial() != this)
    {
      shader.setUniform("u_material.ambient", this.ambient);
      shader.setUniform("u_material.diffuse", this.diffuse);
      shader.setUniform("u_material.specular", this.specular);
      shader.setUniform("u_material.shininess", this.shininess);

      for (int i = 0; i < textures.length; i++)
      {
        if (textures[i] != null)
        {
          textures[i].bind(i);
          shader.setUniform("u_material.texture" + i, i);
        }
      }

      shader.setCurrentMaterial(this);
    }
  }

  public void unbind()
  {
    for (int i = 0; i < textures.length; i++)
    {
      if (textures[i] != null)
      {
        textures[i].unbind(i);
      }
    }
  }

  public MaterialArchetype() {}

  public MaterialArchetype(Vector3f ambient, Vector3f diffuse, Vector3f specular, float shininess)
  {
    this.ambient = ambient;
    this.diffuse = diffuse;
    this.specular = specular;
    this.shininess = shininess;

    this.textures = new Texture[MAX_TEXTURES];

    this.transparent = false;
  }
}