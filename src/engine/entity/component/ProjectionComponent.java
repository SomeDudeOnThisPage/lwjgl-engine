package engine.entity.component;

import engine.entity.EntityComponent;
import engine.render.Viewport;
import org.joml.Matrix4f;

public abstract class ProjectionComponent extends EntityComponent
{
  public abstract Matrix4f construct(Viewport viewport);
}
