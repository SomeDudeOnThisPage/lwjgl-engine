package engine.core.gui;

public class GUITextProperties
{
  public static final class Alignment
  {
    public static final int LEFT     = 1;
    public static final int CENTER   = 1<<1;
    public static final int RIGHT    = 1<<2;
    public static final int TOP      = 1<<3;
    public static final int MIDDLE   = 1<<4;
    public static final int BOTTOM   = 1<<5;
    public static final int BASELINE = 1<<6;
  }

  private Font font;
  private float size;
  private int alignment;
}