package engine.entity;

import engine.scene.Scene;

public interface EntitySystem
{
  EntityComponentSystem.SystemPriority priority();
  //{
    // average
  //  return EntityComponentSystem.SystemPriority.LEVEL_3;
  //}

  void update(Scene scene);
}