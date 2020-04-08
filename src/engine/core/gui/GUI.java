package engine.core.gui;

import engine.Engine;
import engine.util.settings.Settings;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4i;
import org.lwjgl.nanovg.NVGColor;

import java.util.HashMap;

import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVGGL3.*;
import static org.lwjgl.opengl.GL43C.*;

public class GUI
{
  protected static HashMap<String, GUIElement> index = new HashMap<>();
  private static long vg = -1;
  public static NVGColor color;

  private static GUIElement root;

  public static GUIElement getElement(String identifier)
  {
    return GUI.index.get(identifier);
  }

  public static long vg()
  {
    if (GUI.vg == -1)
    {
      System.err.println("attempted to render GUI without prior initialization");
      System.exit(-1);
    }

    return GUI.vg;
  }
  public static void render()
  {
    glDisable(GL_CULL_FACE);
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    glEnable(GL_STENCIL_TEST);

    glViewport(0, 0, Engine.window.getWidth(), Engine.window.getHeight());
    nvgBeginFrame(vg,
      Engine.window.getWidth(),
      Engine.window.getHeight(),
      1
    );

    GUI.root.render(vg);

    nvgEndFrame(vg);
    nvgReset(vg);

    glEnable(GL_DEPTH_TEST);
    glDisable(GL_STENCIL_TEST);
    glEnable(GL_CULL_FACE);
    glCullFace(GL_BACK);
    glDisable(GL_BLEND);
  }

  public static NVGColor rgba(int r, int g, int b, int a, NVGColor colour) {
    colour.r(r / 255.0f);
    colour.g(g / 255.0f);
    colour.b(b / 255.0f);
    colour.a(a / 255.0f);

    return colour;
  }

  public static void initialize()
  {
    vg = nvgCreate(NVG_STENCIL_STROKES | (Settings.getb("Multisample") ? NVG_ANTIALIAS : NVG_ZERO));
    Font.load(Font.FONT_IDENTIFIER_DEFAULT, Font.FONT_DEFAULT);

    color = NVGColor.create();

    GUI.root = new GUIElement("root");
    GUI.root.constraints().position(GUIConstraints.Position.ABSOLUTE);
    GUI.root.size(Engine.window.getWidth(), Engine.window.getHeight());

    Panel panel = new Panel(
      "debug-panel",
      new Vector2f(0, 0),
      new Vector2i(250, 500),
      new Vector4i(50, 50, 50, 150),
      new GUIConstraints(
        new float[] { 5.0f, 5.0f, 5.0f, 5.0f },
        new float[] { 5.0f, 5.0f, 5.0f, 5.0f },
        GUIConstraints.Position.ABSOLUTE,
        GUIConstraints.Size.ABSOLUTE)
    );

    Label fps = new Label("fps-label", "FPS: %%%", Font.getDefault(), NVG_ALIGN_LEFT | NVG_ALIGN_TOP);
    fps.position(0, 0);
    fps.constraints().position(GUIConstraints.Position.RELATIVE);
    panel.add(fps);

    Label fxaa = new Label("fxaa-label", "FXAA: OFF", Font.getDefault(), NVG_ALIGN_LEFT | NVG_ALIGN_TOP);
    fxaa.position(0, 15);
    fxaa.constraints().position(GUIConstraints.Position.RELATIVE);
    panel.add(fxaa);

    Label mode = new Label("mode-label", "Mode: SOLID", Font.getDefault(), NVG_ALIGN_LEFT | NVG_ALIGN_TOP);
    mode.position(0, 30);
    mode.constraints().position(GUIConstraints.Position.RELATIVE);
    panel.add(mode);

    GUI.root.add(panel);
  }

  public static void onScreenSizeChanged(int x, int y)
  {
    GUI.root.size.x = x;
    GUI.root.size.y = y;
  }
}