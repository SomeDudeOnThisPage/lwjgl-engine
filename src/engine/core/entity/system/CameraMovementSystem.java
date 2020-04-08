package engine.core.entity.system;

import engine.Engine;
import engine.core.Input;
import engine.core.Window;
import engine.core.entity.Entity;
import engine.core.entity.component.*;
import engine.core.gfx.UniformBuffer;
import engine.core.gui.GUI;
import engine.core.gui.Label;
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
  private boolean togglefs = false;
  private boolean togglewindowed = false;
  private boolean togglecursor = false;

  @Override
  public void update(Scene scene, ArrayList<Entity> entities)
  {
    if (Input.keyDown(GLFW_KEY_F))
    {
      if (!togglefs)
      {
        Engine.window.borderless();
        togglefs = true;
      }
    }
    else
    {
      togglefs = false;
    }

    if (Input.keyDown(GLFW_KEY_G))
    {
      if (!togglewindowed)
      {
        togglewindowed = true;
        Engine.window.windowed();
      }
    }
    else
    {
      togglewindowed = false;
    }

    if (Input.keyDown(GLFW_KEY_9))
    {
      if (!toggle)
      {
        Engine.FXAA = !Engine.FXAA;
        toggle = true;
      }
      ((Label) GUI.getElement("fxaa-label")).text("FXAA:\t" + (Engine.FXAA ? "ON" : "OFF"));
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
      ((Label) GUI.getElement("mode-label")).text("FXAA:\t" + (Engine.WIREFRAME ? "WIREFRAME" : "SOLID"));
    }
    else
    {
      togglewf = false;
    }

    if (Input.keyDown(GLFW_KEY_ESCAPE))
    {
      if (!togglecursor)
      {
        Engine.window.setCursorMode(Window.CURSOR.NORMAL);
        togglecursor = true;
      }
    }
    else
    {
      Engine.window.setCursorMode(Window.CURSOR.DISABLED);
      togglecursor = false;
    }
  }

  @Override
  public void render(Scene scene, ArrayList<Entity> entities)
  {
    // draft for rendering shadow maps
    // todo: some way to do cascaded shadow maps
    // todo: some way to store shadow map textures in a bindless manner
    for (Entity entity : entities)
    {
      /*
      ShadowCasterComponent source = entity.get(ShadowCasterComponent.class);
      if (source.ALWAYS_CAST || (distance_entity_to_player < some_value && current_shadow_maps < max_shadow_maps))
      {
        setup_shadow_map(source);

        // render scene with all objects that do not meet the renderflags
        // (e.g. exclude rendering of a part of a mesh that contains the light source)
        render_shadow_map(source.resolution, source.renderflags);

        store_shadow_map_texture(source.index);
      }
       */
    }
  }

  @Override
  public void added(Entity entity) {}
}