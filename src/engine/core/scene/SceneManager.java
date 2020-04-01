package engine.core.scene;

public class SceneManager
{
  private Scene active;

  public Scene getScene()
  {
    return this.active;
  }

  public synchronized void setScene(Scene scene)
  {
    this.terminate();

    // initialize new scene
    this.active = scene;

    // internal initialization
    this.active.initialize();

    // enter hook
    this.active.onEnter();
  }

  public synchronized void render()
  {
    if (this.active.initialized())
    {
      this.active.render();
    }
  }

  public synchronized void update(double dt)
  {
    if (this.active.initialized())
    {
      this.active.update(dt);
    }
  }

  public synchronized void terminate()
  {
    if (this.active != null)
    {
      // exit hook
      this.active.onExit();

      // internal termination
      this.active.terminate();
    }
  }
}
