package engine.core.gfx.filter;

import engine.Engine;
import engine.core.rendering.DeferredRenderer;

public final class ToneMapFilter extends Filter
{
  @Override
  public void apply()
  {
    ((DeferredRenderer) Engine.scene_manager.getScene().getRenderer()).getGBuffer().bind(this);
    super.apply();
  }

  /**
   * Use {@link Filter#getInstance(String)} and cast it to your desired {@link Filter} subclass.
   */
  public ToneMapFilter()
  {
    super("tonemap");
  }
}
