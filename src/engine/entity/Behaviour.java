package engine.entity;

import engine.scene.Scene;

public abstract class Behaviour extends EntityComponent
{
  public abstract void update(Scene scene);
}