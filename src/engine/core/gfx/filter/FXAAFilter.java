package engine.core.gfx.filter;

import engine.Engine;

public final class FXAAFilter extends Filter
{
  @Override
  public void apply()
  {
    this.setUniform("u_fxaaOn", (Engine.FXAA ? 1 : 0));

    super.apply();
  }

  public FXAAFilter()
  {
    super("fxaa");
  }
}
