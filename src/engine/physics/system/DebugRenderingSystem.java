package engine.physics.system;

import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import engine.entity.*;
import engine.physics.BulletPhysics;
import engine.physics.DebugRenderer;
import engine.platform.input.Input;
import engine.render.ForwardRenderer;
import engine.render.RenderStage;
import engine.render.deferred.DeferredRenderer;
import engine.scene.Scene;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_DIVIDE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Q;

public class DebugRenderingSystem extends EntityCollection implements EntitySystem, RenderSystem<DeferredRenderer>
{
  private static final int KEY = GLFW_KEY_KP_DIVIDE;

  public DebugRenderer renderer;
  private Scene scene;
  private int current = 0;
  private final BulletPhysics.World world;

  private final int[] modes = {
    btIDebugDraw.DebugDrawModes.DBG_NoDebug,
    btIDebugDraw.DebugDrawModes.DBG_DrawWireframe,
    btIDebugDraw.DebugDrawModes.DBG_DrawAabb | btIDebugDraw.DebugDrawModes.DBG_DrawNormals,
    btIDebugDraw.DebugDrawModes.DBG_DrawWireframe | btIDebugDraw.DebugDrawModes.DBG_DrawAabb,
    btIDebugDraw.DebugDrawModes.DBG_DrawContactPoints | btIDebugDraw.DebugDrawModes.DBG_DrawConstraints | btIDebugDraw.DebugDrawModes.DBG_DrawConstraintLimits
  };
  private boolean pressed = false;

  @Override
  public <T extends EntityComponent> Class<T>[] components()
  {
    return new Class[0];
  }

  @Override
  public EntityComponentSystem.SystemPriority priority()
  {
    return EntityComponentSystem.SystemPriority.LEVEL_5;
  }

  @Override
  public RenderStage stage()
  {
    return RenderStage.AFTER;
  }

  @Override
  public void update(Scene scene)
  {
    if (Input.keyDown(KEY) && Input.keyDown(GLFW_KEY_Q) && !this.pressed)
    {
      this.pressed = true;
      this.current++;
      this.renderer.setDebugMode(this.modes[this.current % this.modes.length]);
    }
    else if (!Input.keyDown(KEY) && Input.keyDown(GLFW_KEY_Q) && this.pressed)
    {
      this.pressed = false;
    }
  }

  @Override
  public void render(DeferredRenderer pipeline)
  {
    this.renderer.begin(this.scene.getEntity("editor-camera"), this.scene.viewport());
    this.world.getWorld().debugDrawWorld();
    this.renderer.end();
  }

  public DebugRenderingSystem(BulletPhysics.World world, Scene scene)
  {
    this.renderer = new DebugRenderer();
    this.renderer.setDebugMode(this.current);
    this.world = world;
    this.scene = scene;
    world.getWorld().setDebugDrawer(this.renderer);
  }
}