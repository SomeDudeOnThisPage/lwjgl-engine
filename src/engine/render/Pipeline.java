package engine.render;

import engine.entity.Camera;
import engine.gfx.buffer.UniformBuffer;
import engine.scene.Scene;

public interface Pipeline
{
  UniformBuffer ubo();
  void render(Scene scene, Viewport viewport);
  Camera camera();
  void camera(Camera camera);
}
