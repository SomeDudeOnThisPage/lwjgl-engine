package engine.core.gui;

public class GUIConstraints
{
  public enum Position
  {
    ABSOLUTE,
    RELATIVE
  }

  public enum Size
  {
    /** Set size as defined in attributes */
    ABSOLUTE,
    /** Calculate size depending on margin and padding */
    FIT_TO_SIZE
  }

  private float[] margin;
  private float[] padding;

  private Position position;
  private Size size;

  public float[] padding()
  {
    return this.padding;
  }

  public float[] margin()
  {
    return this.margin;
  }

  public Size size()
  {
    return this.size;
  }

  public void size(Size size)
  {
    this.size = size;
  }

  public Position position()
  {
    return this.position;
  }

  public void position(Position position)
  {
    this.position = position;
  }

  public GUIConstraints()
  {
    this(
      new float[] { 0.0f, 0.0f, 0.0f, 0.0f },
      new float[] { 0.0f, 0.0f, 0.0f, 0.0f },
      GUIConstraints.Position.RELATIVE,
      GUIConstraints.Size.FIT_TO_SIZE
    );
  }

  public GUIConstraints(float[] padding, float[] margin, Position p, Size s)
  {
    this.position = p;
    this.size = s;
    this.margin = margin;
    this.padding = padding;
  }
}
