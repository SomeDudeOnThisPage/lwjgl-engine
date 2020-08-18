package engine.asset;

import java.util.Collection;
import java.util.HashMap;

public class AssetFamily<T extends Asset>
{
  private final HashMap<String, T> assets;
  private final Class<T> family;

  public boolean contains(String id)
  {
    return this.assets.containsKey(id);
  }

  public T get(String key)
  {
    return this.assets.get(key);
  }

  public T request(String key)
  {
    T asset = this.assets.get(key);
    asset.reference();
    return asset;
  }

  public Collection<T> get()
  {
    return this.assets.values();
  }

  public void put(String key, T asset)
  {
    int references = 0;
    if (this.assets.containsKey(key))
    {
      T old = this.assets.get(key);

      // replace the asset by first copying the references and disposing of the old asset
      references = old.references();
      old.dispose();
    }

    this.assets.put(key, asset);
    asset.references(references); // copy references
    asset.family(this.family);
  }

  public void remove(String key)
  {
    this.assets.remove(key);
  }

  public final Class<T> family()
  {
    return this.family;
  }

  public final void dispose()
  {
    for (T asset : this.assets.values())
    {
      asset.dispose();
    }
  }

  public AssetFamily(Class<T> family)
  {
    this.assets = new HashMap<>();
    this.family = family;
  }
}
