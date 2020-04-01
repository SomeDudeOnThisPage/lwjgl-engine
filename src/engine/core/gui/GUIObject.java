package engine.core.gui;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import java.util.ArrayList;

public class GUIObject
{
  private Vector2f position;
  private Vector2f size;

  private ArrayList<GUIObject> children = new ArrayList<>();
  private GUIObject parent;

  public GUIObject getParent()
  {
    return this.parent;
  }

  public Vector2f getPosition()
  {
    return this.position;
  }

  protected GUIObject()
  {
    this(0, 0, 0, 0);
  }

  protected GUIObject(float px, float py, float sx, float sy)
  {
    this.position = new Vector2f(px, py);
    this.size = new Vector2f(sx, sy);
  }
}