package engine.core.gfx.filter;

import engine.Engine;
import engine.core.rendering.DeferredRenderer;

public class ShadowBlitFilter extends Filter
{
  @Override
  public void apply()
  {
    ((DeferredRenderer) Engine.scene_manager.getScene().getRenderer()).getSBuffer().bind(this);
    super.apply();
  }

  public ShadowBlitFilter()
  {
    super("shadow");
  }
}
