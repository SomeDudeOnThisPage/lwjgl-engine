package engine.asset.load;

import engine.asset.Asset;
import org.w3c.dom.Element;

public interface AssetFactory<T extends Asset>
{
  class AssetLoadingException extends RuntimeException
  {
    public AssetLoadingException(String e)
    {
      super(e);
    }
  }

  String tag();
  T load(Element xml) throws AssetLoadingException;
}
