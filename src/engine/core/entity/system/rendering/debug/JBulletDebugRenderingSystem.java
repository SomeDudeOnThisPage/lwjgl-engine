package engine.core.entity.system.rendering.debug;

import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import engine.EngineSettings;
import engine.core.Input;
import engine.core.entity.Entity;
import engine.core.entity.component.EntityComponent;
import engine.core.entity.system.IRenderSystem;
import engine.core.entity.system.UpdateSystem;
import engine.core.physics.JBulletDebugRenderer;
import engine.core.rendering.RenderStage;
import engine.core.physics.PhysicsEngine;
import engine.core.scene.Scene;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_DIVIDE;

public class JBulletDebugRenderingSystem extends UpdateSystem implements IRenderSystem
{
  private static final int KEY = GLFW_KEY_KP_DIVIDE;

  public JBulletDebugRenderer renderer;
  private int current = 0;
  private int[] modes = {
    btIDebugDraw.DebugDrawModes.DBG_NoDebug,
    btIDebugDraw.DebugDrawModes.DBG_DrawWireframe,
    btIDebugDraw.DebugDrawModes.DBG_DrawAabb,
    btIDebugDraw.DebugDrawModes.DBG_DrawWireframe | btIDebugDraw.DebugDrawModes.DBG_DrawAabb,
    btIDebugDraw.DebugDrawModes.DBG_DrawContactPoints | btIDebugDraw.DebugDrawModes.DBG_DrawConstraints | btIDebugDraw.DebugDrawModes.DBG_DrawConstraintLimits
  };
  private boolean pressed = false;

  @Override
  public RenderStage priority()
  {
    return RenderStage.AFTER;
  }

  @Override
  public void render(Scene scene, ArrayList<Entity> entities)
  {
    this.renderer.begin();
    scene.physics().world().debugDrawWorld();
    this.renderer.end();
  }

  @Override
  public <T extends EntityComponent> Class<T>[] components() { return new Class[0]; }

  @Override
  public void update(Scene scene, ArrayList<Entity> entities)
  {
    if (Input.keyDown(KEY) && Input.keyDown(EngineSettings.KEY_DEBUG) && !this.pressed)
    {
      this.pressed = true;
      this.current++;
      this.renderer.setDebugMode(this.modes[this.current % this.modes.length]);
    }
    else if (!Input.keyDown(KEY) && Input.keyDown(EngineSettings.KEY_DEBUG) && this.pressed)
    {
      this.pressed = false;
    }
  }

  @Override
  public void added(Entity entity) {}

  /**
   * Attaching this {@link UpdateSystem} to a {@link Scene} with an attached JBullet {@link PhysicsEngine} attaches
   * a {@link JBulletDebugRenderer} to the {@link PhysicsEngine}.
   * <p>Note that the implementation of the {@link
   * JBulletDebugRenderer} is very inefficient and should only be used to debug the {@link PhysicsEngine}, and not
   * be attached to a final {@link Scene}.</p>
   * @param engine The JBullet {@link PhysicsEngine} to hook the debugger onto.
   */
  public JBulletDebugRenderingSystem(PhysicsEngine engine)
  {
    this.renderer = new JBulletDebugRenderer();
    this.renderer.setDebugMode(this.current);
    engine.world().setDebugDrawer(this.renderer);
  }
}
