package engine.core.entity.component;

public class RotationComponent extends EntityComponent
{
  public float speed;

  public RotationComponent(float speed)
  {
    this.speed = speed;
  }
}