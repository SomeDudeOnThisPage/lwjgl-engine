package engine.asset;

import engine.Console;
import engine.Engine;
import engine.asset.load.AssetFactory;
import engine.util.EngineINI;
import engine.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * singlemtomn
 */
public final class AssetManager
{
  public static final class Path
  {
    public static String resolve(String path)
    {
      EngineINI.Path paths = EngineINI.getInstance().path;
      String resources = paths.root + "/" + paths.resources + "/";

      for (String folder : paths.resource_folders)
      {
        File tmp = new File(resources + folder + "/" + path);
        System.err.println(resources + folder + "/" + path);
        if (tmp.exists())
        {
          return resources + folder + "/" + path;
        }
      }

      throw new AssetFactory.AssetLoadingException("failed to resolve path '" + path + "'");
    }
  }

  private static final boolean UNSAFE = true; // todo: config
  private static AssetManager instance;

  static
  {
    Console.addCommand("asset", (args -> AssetManager.instance.loading.add(args[0])));
  }

  private final HashMap<Class<? extends Asset>, AssetFamily<? extends Asset>> families;
  private final HashMap<Class<? extends Asset>, AssetFactory<? extends Asset>> factories;

  private final Queue<Map.Entry<Class<? extends Asset>, String>> removal;
  private final Queue<String> loading;

  private final ReloadWatchService service;

  public static AssetManager getInstance()
  {
    if (AssetManager.instance == null)
    {
      AssetManager.instance = new AssetManager();
    }

    return AssetManager.instance;
  }

  public <T extends Asset> boolean registeredFamily(Class<T> family)
  {
    return this.families.containsKey(family);
  }

  public <T extends Asset> void registerFamily(AssetFamily<T> family)
  {
    this.families.put(family.family(), family);
  }

  public <T extends Asset> AssetFamily<T> getFamily(Class<T> family)
  {
    // gemnerimncs
    return (AssetFamily<T>) this.families.get(family);
  }

  public Collection<Class<? extends Asset>> getFamilyClasses()
  {
    return this.families.keySet();
  }

  public <T extends Asset> void registerFactory(Class<T> family, AssetFactory<T> factory)
  {
    this.factories.put(family, factory);
  }

  public void registerWatchedAssetDefinitionFile(String path)
  {
    this.service.register(path);
  }

  /**
   * Convenience method to check both if a required family, as well as an asset in that family exists.
   * @param id The ID of the asset.
   * @param family The family of the asset.
   * @return exists
   */
  public <T extends Asset> boolean exists(String id, Class<T> family)
  {
    if (!this.registeredFamily(family))
    {
      return false;
    }

    return this.getFamily(family).contains(id);
  }

  public <T extends Asset> void load(T asset, String key, Class<T> type)
  {
    // should no applicable family exist, register a family of the type of asset created
    // note that registering families implicitly BEFORE loading assets is to be preferred,
    // as families can then contain multiple shared subclasses of an asset type
    // this behaviour is disabled in safe mode
    if (AssetManager.UNSAFE && this.getFamily(type) == null)
    {
      Engine.Log.warning("[ASSETMANAGER] unsafe asset family creation with family type '" + type.getSimpleName() + "'");
      this.registerFamily(new AssetFamily<>(type));
    }

    AssetFamily<T> family = this.getFamily(type);
    family.put(key, asset);
    asset.key(key);

    Engine.Log.info("[ASSETMANAGER] [LOADING] asset " + key + " in family " + type.getSimpleName());
  }

  public <T extends Asset> void load(String path)
  {
    path = AssetManager.Path.resolve(path);

    Document xml = XMLUtil.load(path); // todo: find file on project paths and load assets (for now only absolute)

    if (xml == null)
    {
      Console.error("failed to load assets - file not found");
      return;
    }

    Element root = (Element) xml.getElementsByTagName("assets").item(0);
    if (root == null)
    {
      Console.error("failed to load assets - no assets tag found");
      return;
    }

    if (root.getAttribute("reloadable") != null && Boolean.parseBoolean(root.getAttribute("reloadable")))
    {
      System.err.println("reloadable " + path);
      this.service.register(path);
    }

    NodeList children = root.getChildNodes();
    for (int i = 0; i < children.getLength(); i++)
    {
      Node child = children.item(i);
      if (child.getNodeType() != Node.ELEMENT_NODE) { continue; }

      Element element = (Element) child; // asset creation info

      String tag = element.getTagName();

      for (Map.Entry<Class<? extends Asset>, AssetFactory<? extends Asset>> kv : this.factories.entrySet())
      {
        Class<T> family = (Class<T>) kv.getKey();
        AssetFactory<? extends Asset> factory = kv.getValue();

        if (factory.tag().equals(tag))
        {
          if (!element.hasAttribute("id"))
          {
            throw new AssetFactory.AssetLoadingException("failed to load asset definition '" + element.getTagName() + "' missing attribute 'id'");
          }

          // load asset with factory
          String id = element.getAttribute("id");      // asset id
          T asset = (T) factory.load(element);         // load asset using factory (ass cast cause bad)

          asset.key(id);
          asset.source(path);

          // sanity check, should the family not exist, create it
          if (!this.families.containsKey(family))
          {
            this.registerFamily(new AssetFamily<>(family));
          }

          if (asset instanceof Reloadable && this.getFamily(family).contains(id))
          {
            // reload the asset in place (same object)
            ((Reloadable<T>) this.getFamily(family).get(id)).reload(asset);
          }
          else
          {
            // create or replace the asset
            this.load(asset, id, family);
          }
        }
      }
    }
  }

  /**
   * Requests an {@link Asset}. Note that this {@link Asset} has to be already loaded and accessible by the
   * {@link AssetManager}.
   * <p>
   *   The {@link Asset} should be released after usage using the {@link AssetManager#release(Asset, Class)} method.<br>
   *   Note that this should <b>not</b> be done in an {@link Asset}s' {@link Asset#dispose()} method, as this method
   *   is the one called once the {@link Asset} is no longer in use and its' resources should be deallocated.
   * </p>
   * @param key The {@link String} id of the {@link Asset}.
   * @param family The {@link Class} type of the {@link Asset}.
   * @param <T> {@link Class} type identifier.
   * @return The requested {@link Asset}.
   */
  public <T extends Asset> T request(String key, Class<T> family)
  {
    if (this.getFamily(family) == null)
    {
      throw new UnsupportedOperationException("cannot find asset family '" + family.getSimpleName() + "'");
    }

    T asset = this.getFamily(family).get(key);

    if (asset == null)
    {
      throw new UnsupportedOperationException("could not find asset with key '" + key + "' in asset family '" +
        family.getSimpleName() + "' - make sure the asset is loaded");
    }

    asset.reference(); // increment reference count
    return asset;
  }

  public <T extends Asset> void release(T asset)
  {
    this.release(asset.key(), asset.getClass());
  }

  public <T extends Asset> void release(String key, Class<T> family)
  {
    this.release(this.getFamily(family).get(key), family);
  }

  public <T extends Asset> void release(T asset, Class<T> family)
  {
    if (this.getFamily(family) == null)
    {
      throw new UnsupportedOperationException("cannot find asset family '" + asset.getClass().getSimpleName() + "'");
    }

    asset.dereference(); // decrement reference count
    if (asset.references() <= 0)
    {
      this.removal.add(new AbstractMap.SimpleEntry<>(family, asset.key()));
      // todo: remove (maybe if some form of lifetime has been exceeded)
    }
  }

  public void update()
  {
    // work removal queue
    while (this.removal.size() > 0)
    {
      Map.Entry<Class<? extends Asset>, String> entry = this.removal.poll();
      this.getFamily(entry.getKey()).remove(entry.getValue());
    }

    // work loading queue
    while (this.loading.size() > 0)
    {
      String path = this.loading.poll();
      this.load(path);
    }

    this.service.update();
  }

  public void remove(BaseAsset asset)
  {
    for (AssetFamily<? extends Asset> family : this.families.values())
    {
      // check family of asset
      if (asset.getClass().isInstance(family.family()))
      {
        // queue for removal
        this.removal.add(new AbstractMap.SimpleEntry<>(
          family.family(),
          asset.key()
        ));
        return;
      }
    }
  }

  /*public void test()
  {
    this.registerFamily(new AssetFamily<>(GLTexture2D.class));
    this.load(new GLTexture2D("ass", null, null, null), "texture.test", GLTexture2D.class);
    GLTexture2D b = this.getFamily(GLTexture2D.class).get("test");
  }*/

  public void dispose()
  {
    for (AssetFamily<?> family : this.families.values())
    {
      family.dispose();
    }
  }

  public AssetManager()
  {
    this.families   = new HashMap<>();
    this.factories  = new HashMap<>();
    this.removal    = new LinkedBlockingQueue<>();
    this.loading    = new LinkedBlockingQueue<>();

    // todo: create file watcher only if set in some sort of settings
    this.service = new ReloadWatchService();
  }
}
