package engine.core.gui;

import org.joml.Vector2f;
import org.joml.Vector2i;

import java.util.ArrayList;

public class GUIElement
{
  protected GUIElement parent;
  protected ArrayList<GUIElement> children = new ArrayList<>();

  protected GUIConstraints constraints = new GUIConstraints();

  protected Vector2f position = new Vector2f();

  protected Vector2i size = new Vector2i();

  protected void index(String id)
  {
    if (GUI.index.containsKey(id))
    {
      System.err.println("cannot index GUI element with id '" + id + "' - cannot override index");
      return;
    }
    GUI.index.put(id, this);
  }

  public GUIElement size(int x, int y)
  {
    this.size.set(x, y);
    return this;
  }

  public Vector2i size()
  {
    return this.size;
  }

  public GUIElement parent()
  {
    return this.parent;
  }

  public ArrayList<GUIElement> getChildren()
  {
    return this.children;
  }

  public GUIConstraints constraints()
  {
    return this.constraints;
  }

  public GUIElement position(float x, float y)
  {
    this.position.set(x, y);
    return this;
  }

  public Vector2f position()
  {
    return this.position;
  }

  public void add(GUIElement child)
  {
    child.parent = this;
    this.children.add(child);
  }

  public void render(long vg)
  {
    for (GUIElement element : this.children)
    {
      element.render(vg);
    }
  }

  public GUIElement(String id)
  {
    GUI.index.put(id, this);
  }
}
