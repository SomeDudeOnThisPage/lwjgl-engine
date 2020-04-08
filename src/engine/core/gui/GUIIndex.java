package engine.core.gui;

import java.util.HashMap;

/**
 * This class is used to create an index of {@link GUIElement}s linked to their {@link String} identifiers.
 */
public class GUIIndex
{
  private HashMap<String, GUIElement> index;

  public GUIIndex()
  {
    this.index = new HashMap<>();
  }
}
