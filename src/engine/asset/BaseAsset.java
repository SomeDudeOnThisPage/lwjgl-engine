package engine.asset;

public abstract class BaseAsset implements Asset
{
  private int references = 0;
  private String key;
  private String source;
  private Class<? extends Asset> family;

  /**
   * This should really be package private, but I'm bad at design.
   */
  @Override
  public final void reference()
  {
    this.references += 1;
  }

  /**
   * This should really be package private, but I'm bad at design.
   */
  @Override
  public final void dereference()
  {
    if (this.references <= 1)
    {
      // queue for removal in asset manager
      // AssetManager.getInstance().remove(this);
      this.references = 0;
    }
    else
    {
      this.references -= 1;
    }
  }

  /**
   * This should really be package private, but I'm bad at design.
   */
  @Override
  public final void references(int references)
  {
    this.references = references;
  }

  /**
   * Returns the amount of references to this asset, i.e. how many times it is currently requested, but not released.
   * @return Amount of references to this asset.
   */
  @Override
  public final int references()
  {
    return this.references;
  }

  @Override
  public final void key(String key)
  {
    this.key = key;
  }

  @Override
  public final String key()
  {
    return this.key;
  }

  @Override
  public final void family(Class<? extends Asset> family)
  {
    this.family = family;
  }

  @Override
  public final Class<? extends Asset> family()
  {
    return this.family;
  }

  @Override
  public void source(String src)
  {
    this.source = src;
  }

  @Override
  public String source()
  {
    return this.source;
  }

  /**
   * This method should dispose of any allocated resources by this {@link Asset}.
   * <p>
   *   Note that, ideally, this method should be called by an asset management system of some sort,
   *   on the (rendering APIs') main thread.
   * </p>
   */
  public abstract void dispose();

  public BaseAsset()
  {
    //this.key = key;
  }
}
