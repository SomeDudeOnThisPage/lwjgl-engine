package engine.gfx;

import engine.asset.BaseAsset;

public abstract class BaseAssetBindable extends BaseAsset implements Bindable
{
  protected int id;

  @Override
  public int id()
  {
    return this.id;
  }
}
