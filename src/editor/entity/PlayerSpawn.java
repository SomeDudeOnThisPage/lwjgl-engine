package editor.entity;

import engine.entity.component.MaterialComponent;
import engine.entity.component.Mesh;
import engine.entity.Entity;
import engine.entity.component.Transform;

public class PlayerSpawn extends Entity
{
  public PlayerSpawn()
  {
    super("player-spawn");
    this.addComponent(new Transform());
    this.addComponent(new Mesh("cube.Cube"));
    this.addComponent(new MaterialComponent("wood.phong"));
  }
}
