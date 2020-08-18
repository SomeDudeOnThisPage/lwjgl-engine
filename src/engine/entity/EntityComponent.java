package engine.entity;

import java.util.EnumSet;

public abstract class EntityComponent
{
  public enum Flags
  {
    NO_REMOVE,
    INACTIVE
  }

  private final EnumSet<Flags> flags = EnumSet.noneOf(Flags.class);

  protected Entity entity;

  public final EnumSet<Flags> flags()
  {
    return this.flags;
  }

  public final EntityComponent flag(Flags flag)
  {
    if (!this.flags.contains(flag))
    {
      this.flags.add(flag);
    }

    return this;
  }

  public final boolean flagged(Flags... flags)
  {
    for (Flags flag : flags)
    {
      if (!this.flags.contains(flag))
      {
        return false;
      }
    }
    return true;
  }

  public final EntityComponent unflag(Flags flag)
  {
    this.flags.remove(flag);
    return this;
  }

  /**
   * Useful to dispose of any {@link engine.asset.Asset}s bound to this {@link EntityComponent}.
   */
  public void onComponentRemoved() {}

  /**
   * This method should be used instead of a constructor, as the {@link Entity} member variable of this
   * component will already have been set.
   */
  public void onComponentAttached() {}

  public void onComponentDestroyed() {}
}