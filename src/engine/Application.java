package engine;

public abstract class Application
{
  void initialize()
  {

    this.init();
  }
  protected abstract void init();
  protected abstract void tick();
  protected abstract void render();
}
