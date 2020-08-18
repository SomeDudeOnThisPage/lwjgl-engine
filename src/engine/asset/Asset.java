package engine.asset;

public interface Asset
{
  void reference();
  void dereference();
  int references();
  void references(int references);

  String key();
  void key(String key);

  void family(Class<? extends Asset> family);
  Class<? extends Asset> family();

  void source(String src);
  String source();

  void dispose();
}
