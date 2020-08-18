package engine.entity;

import engine.render.Pipeline;
import engine.render.RenderStage;

public interface RenderSystem<P extends Pipeline>
{
  RenderStage stage();
  void render(P pipeline);
}
