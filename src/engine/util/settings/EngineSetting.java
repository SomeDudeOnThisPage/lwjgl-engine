package engine.util.settings;

public class EngineSetting<T>
{
  private T value;
  private Class<T> type;

  public void set(T value)
  {
    this.value = value;
  }

  public T get()
  {
    return this.type.cast(this.value);
  }

  public EngineSetting(T value, Class<T> type)
  {
    this.value = value;
    this.type = type;
  }
}
