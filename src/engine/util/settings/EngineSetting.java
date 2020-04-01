package engine.util.settings;

public class EngineSetting<T>
{
  private T value;
  private boolean uniform;

  public void set(T value)
  {
    this.value = value;
  }

  public T get()
  {
    return this.value;
  }

  public EngineSetting(T value, boolean uniform)
  {
    this.value = value;
    this.uniform = uniform;
  }
}
