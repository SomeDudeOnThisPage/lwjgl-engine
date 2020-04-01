package engine.core.entity.system;
import engine.core.entity.Entity;
import engine.core.rendering.DeferredRenderer;
import engine.core.rendering.RenderStage;
import engine.core.scene.Scene;

import java.util.ArrayList;

/**
 * The {@link IRenderSystem} interface presents an extension to an {@link UpdateSystem} of the
 * {@link engine.core.entity.EntityComponentSystem}. It can be used to hook a method to a custom
 * {@link engine.core.rendering.Renderer}s' rendering pipeline. The implementation of the
 * {@link engine.core.rendering.Renderer} dictates when, and if at all, the {@link IRenderSystem#render(Scene, ArrayList)}
 * method will be called.
 */
public interface IRenderSystem
{
  /**
   * This method must return a valid {@link RenderStage}, during which the {@link IRenderSystem#render(Scene, ArrayList)}
   * method should be called. The callback itself has to be implemented in the (custom)
   * {@link engine.core.rendering.Renderer} implementation that the current scene is using.
   * The {@link engine.core.rendering.Renderer} has to call the {@link engine.core.entity.EntityComponentSystem}s
   * render method with the corresponding {@link RenderStage}. See {@link DeferredRenderer#render(Scene)} for more details.
   * @return The {@link RenderStage} in which the {@link IRenderSystem#render(Scene, ArrayList)} method should be called.
   */
  RenderStage priority();

  /**
   * The rendering callback called during the {@link RenderStage} set in the {@link IRenderSystem#priority()} method.
   * @param scene The {@link Scene} which this method should render certain parts of.
   * @param entities An {@link ArrayList} containing all the {@link Entity} handles this
   *                 system needs to update.
   */
  void render(Scene scene, ArrayList<Entity> entities);
}
