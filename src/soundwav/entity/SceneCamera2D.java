package soundwav.entity;

import editor.entity.component.Camera2D;
import engine.entity.Camera;
import engine.entity.component.Transform;

public class SceneCamera2D extends Camera
{
  public SceneCamera2D()
  {
    this.identifier = "editor-camera";
    this.projection = new Camera2D();
    this.transform = new Transform();

    this.addComponent(this.projection);
    this.addComponent(this.transform);
  }
}
