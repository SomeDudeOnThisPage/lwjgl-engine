package engine.util;

import java.util.ArrayList;
import java.util.List;

public class DeferredArrayList<T> extends ArrayList<T>
{
  private List<T> tbr;
  private List<T> tba;

  public void update()
  {
    for (T element : this.tbr)
    {
      super.remove(element);
    }

    for (T element : this.tba)
    {
      super.add(element);
    }
  }

  public DeferredArrayList()
  {
    super();

    this.tbr = new ArrayList<>();
    this.tba = new ArrayList<>();
  }
}
