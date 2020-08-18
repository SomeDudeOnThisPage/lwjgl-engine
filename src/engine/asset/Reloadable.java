package engine.asset;

public interface Reloadable<T extends Asset>
{
  /**
   * The {@code reload} method will receive an {@link Asset} of the specified type, and should copy all required
   * data from the "new" {@link Asset} to this already existing {@link Asset}. Make sure to dispose of anything that
   * is not needed anymore!
   * @param asset The new {@link Asset} to be copied.
   */
  void reload(T asset);
}
