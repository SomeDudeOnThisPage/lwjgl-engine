package engine.core.entity.component.shadow;

import engine.core.entity.component.EntityComponent;

/**
 * Slap this component on entities from which a shadow should originate (i.e. light sources).
 */
public class ShadowSourceComponent extends EntityComponent
{
  /**
   * Disable deactivation depending on distance.
   * Note that a shadow-map will then ALWAYS be rendered, potentially decreasing performance.
   * Recommended to only set true for directional light sources with infinite range.
   */
  public boolean ALWAYS_ACTIVE;

  /**
   * Value used in distance-calculation of the {@link engine.core.entity.system.rendering.shadow.ShadowMapSystem}
   * to turn shadows on or off.
   * If {@link ShadowSourceComponent#ALWAYS_ACTIVE} has been set, this has no effect.
   */
  public float distance;

  public ShadowSourceComponent(float distance)
  {
    this.distance = distance;
  }
}