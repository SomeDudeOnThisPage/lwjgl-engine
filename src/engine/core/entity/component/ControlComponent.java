package engine.core.entity.component;

public class ControlComponent extends EntityComponent
{
  public boolean controllable;

  public ControlComponent(boolean controllable)
  {
    this.controllable = controllable;
  }
}