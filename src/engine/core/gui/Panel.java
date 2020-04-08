package engine.core.gui;

import engine.core.scene.SceneGraph;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;
import org.joml.Vector4i;

import static org.lwjgl.nanovg.NanoVG.*;

public class Panel extends GUIElement
{
  private Vector4i color;

  @Override
  public void render(long vg)
  {
    Vector4f transform = SceneGraph.constructTransform(this);

    nvgSave(vg);

    nvgBeginPath(vg);
    nvgFillColor(vg, GUI.rgba(color.x, color.y, color.z, color.w, GUI.color));
    nvgTranslate(vg, transform.x, transform.y);
    nvgRect(vg, transform.x, transform.y, transform.z, transform.w);
    nvgFill(vg);

    nvgRestore(vg);

    super.render(vg);
  }

  public Panel(String id, Vector2f position, Vector2i size, Vector4i color, GUIConstraints constraints)
  {
    super(id);
    this.position = position;
    this.size = size;
    this.color = color;
    this.constraints = constraints;
  }
}
