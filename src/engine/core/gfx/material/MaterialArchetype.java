package engine.core.gfx.material;

import engine.core.entity.Entity;
import engine.core.gfx.Shader;
import engine.core.rendering.GBuffer;
import org.jetbrains.annotations.*;
import org.w3c.dom.Element;

public abstract class MaterialArchetype
{
  /**
   * The {@link MaterialProperties} data-class holds a set of generic material properties used by different rendering
   * systems.
   * <p>These are loaded by default from the {@code <properties>-tag}</p>
   */
  public static final class MaterialProperties
  {
    /**
     * Does this material posess transparency (no full-opacity in textures, but and value > 0 and < 100).
     * Necessary for deferred rendering pipelines.
     */
    public boolean transparency;
  }

  /**
   * The set of {@link MaterialProperties} of this material.
   */
  protected MaterialProperties properties;

  /**
   * The {@link Shader} writing the {@link MaterialArchetype}s' values to the {@link GBuffer} when rendering an
   * {@link Entity} whose's {@link engine.core.entity.component.MeshComponent} uses this {@link MaterialArchetype}.
   */
  protected Shader shader;

  /**
   * This method should set the values of {@code this} to the values defined in the {@code <archetype>}-tag in a
   * {@code .mat} file. The method should return {@code this}.
   * <p>See {@link PBRMaterialFlat#load(Element)} for reference.</p>
   * @param xml The XML-{@code <archetype>} element.
   * @return This method should return {@code this}, the {@link MaterialArchetype} this method belongs to, in order to
   * enable factory-behaviour (chaining calls).
   */
  public abstract MaterialArchetype load(@NotNull Element xml);

  /**
   * This method should bind the {@link MaterialArchetype}s' uniform values to its' ({@link MaterialArchetype#shader})
   * for rendering. Obviously, the shader itself should be bound, after this method has executed, too.
   * <p>See {@link PBRMaterialFlat#bind()} for reference.</p>
   */
  public abstract void bind();

  /**
   * Returns the {@link MaterialProperties} of this material.
   * @return The {@link MaterialProperties} of this material.
   */
  public final MaterialProperties properties()
  {
    if (this.properties == null)
    {
      this.properties = new MaterialProperties();
    }

    return this.properties;
  }

  /**
   * This method returns the {@link MaterialArchetype}s' {@link Shader};
   * @return The {@link MaterialArchetype}s' {@link Shader}.
   */
  public final Shader shader()
  {
    return this.shader;
  }

  /**
   * Unbinds the material. This can be overridden by custom material archetypes.
   */
  public void unbind()
  {
    this.shader.unbind();
  }

  /**
   * Empty constructor to allow for some polymorphism bullshit.
   */
  public MaterialArchetype() {}
}
