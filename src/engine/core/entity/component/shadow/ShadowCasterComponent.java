package engine.core.entity.component.shadow;

import engine.core.entity.component.EntityComponent;

import java.util.ArrayList;

/**
 * Slap this component on entities that should cast a shadow.
 */
public class ShadowCasterComponent extends EntityComponent
{
  /**
   * If a component of a {@link engine.core.entity.component.MeshComponent} should not cast a shadow, the index of this
   * meshes' component is set in this list. Use {@link ShadowCasterComponent#disabled(int mesh)} as a useful shortcut
   * for querying.
   */
  public ArrayList<Boolean> disabled;

  public boolean disabled(int mesh)
  {
    return this.disabled.get(mesh) != null;
  }

  public ShadowCasterComponent()
  {
    this.disabled = new ArrayList<>();
    this.disabled.add(3, true);
  }
}