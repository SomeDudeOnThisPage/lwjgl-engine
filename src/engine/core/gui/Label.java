package engine.core.gui;

import engine.core.scene.SceneGraph;
import org.joml.Vector4f;

import static org.lwjgl.nanovg.NanoVG.*;

public class Label extends GUIElement
{
  protected String text;
  protected Font font;
  protected int align;

  public void align(int align)
  {
    this.align = align;
  }

  public int align()
  {
    return this.align;
  }

  public void font(Font font)
  {
    this.font = font;
  }

  public Font font()
  {
    return this.font;
  }

  public void text(String text)
  {
    this.text = text;
  }

  public String text()
  {
    return this.text;
  }

  @Override
  public void render(long vg)
  {
    Vector4f transform = SceneGraph.constructTransform(this);

    nvgFontSize(vg, 16.0f);

    this.font.bind();

    nvgTextAlign(vg, this.align);
    nvgFillColor(vg, GUI.rgba(0xff, 0xff, 0xff, 255, GUI.color));
    nvgTextBounds(vg, transform.x, transform.y, this.text, new float[] {10, 10, 25, 25});
    nvgText(vg, transform.x, transform.y, this.text);
    super.render(vg);
  }

  public Label(String id, String text, Font font, int align)
  {
    super(id);
    this.text = text;
    this.font = font;
    this.align = align;
  }
}
