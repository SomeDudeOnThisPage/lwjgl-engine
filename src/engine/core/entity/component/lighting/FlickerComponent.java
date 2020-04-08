package engine.core.entity.component.lighting;

import engine.Engine;
import engine.core.entity.component.ScriptComponent;
import engine.core.scene.Scene;
import org.joml.Vector3f;

public class FlickerComponent extends ScriptComponent
{
  private Vector3f clq;

  @Override
  public void onComponentAttached()
  {
    this.clq = this.entity.get(PointLightSourceComponent.class).clq;
  }

  @Override
  public void update(Scene scene)
  {
    float quadratic = clq.z;
    quadratic += 0.00005f * Math.sin(10 * Engine.time());

    this.clq.z = quadratic;
  }
}
