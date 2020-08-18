package engine.exception;

import engine.gfx.Bindable;

public class GLException extends RuntimeException
{
  private final int source;
  private final String type;
  private final String method;

  @Override
  public String toString()
  {
    return "OpenGL Exception in GLAsset with id '" + this.source + "' of type '" + this.type +
      "'\n\tProblematic method: '" + this.method + "'\n\tMessage: '" + this.getMessage() + "'";
  }

  public GLException(Bindable source, String method, String error)
  {
    super(error);
    this.source = source.id();
    this.type = source.getClass().getName();
    this.method = method;
  }
}
