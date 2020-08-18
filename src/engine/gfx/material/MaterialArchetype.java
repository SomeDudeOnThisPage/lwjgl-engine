package engine.gfx.material;

import engine.gfx.BaseAssetBindable;
import engine.gfx.ShaderProgram;
import org.w3c.dom.Element;

public abstract class MaterialArchetype extends BaseAssetBindable implements Material
{
  protected ShaderProgram shader;

  @Override
  public final int id()
  {
    // pretty ugly, but an ID is essential for the Bindable interface, and it makes most sense to utilize
    // the bindable interface here as well, although we throw an error when trying to query the ID.
    // have I mentioned that I am bad at design?
    throw new UnsupportedOperationException("GLMaterial is Bindable, but does not possess an ID");
  }

  @Override
  public final ShaderProgram shader()
  {
    return this.shader;
  }

  public abstract void load(Element xml);
}
