package engine.core.entity.system;

import engine.Engine;
import engine.core.Input;
import engine.core.entity.Entity;
import engine.core.entity.component.*;
import engine.core.gfx.UniformBuffer;
import engine.core.rendering.Renderer;
import engine.core.rendering.RenderStage;
import engine.core.scene.Scene;
import engine.core.scene.SceneGraph;
import org.joml.*;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;

public class CameraMovementSystem extends UpdateSystem implements IRenderSystem
{
  @Override
  public Class[] components()
  {
    return new Class[]
    {
      TransformComponent.class,
      ProjectionComponent.class,
      ControlComponent.class
    };
  }

  @Override
  public RenderStage priority()
  {
    return RenderStage.BEFORE;
  }

  private boolean toggle = false;
  private boolean togglewf = false;

  @Override
  public void update(Scene scene, ArrayList<Entity> entities)
  {
    if (Input.keyDown(GLFW_KEY_9))
    {
      if (!toggle)
      {
        Engine.FXAA = !Engine.FXAA;
        System.out.println("fxaa " + Engine.FXAA);
        toggle = true;
      }
    }
    else
    {
      toggle = false;
    }

    if (Input.keyDown(GLFW_KEY_8))
    {
      if (!togglewf)
      {
        Engine.WIREFRAME = !Engine.WIREFRAME;
        System.out.println("wireframe " + Engine.WIREFRAME);
        togglewf = true;
      }
    }
    else
    {
      togglewf = false;
    }

    for (Entity camera : entities)
    {
    }
  }

  @Override
  public void render(Scene scene, ArrayList<Entity> entities)
  {
    Renderer renderer = scene.getRenderer();

    for (Entity camera : entities)
    {
    }
  }

  @Override
  public void added(Entity entity) {}
}