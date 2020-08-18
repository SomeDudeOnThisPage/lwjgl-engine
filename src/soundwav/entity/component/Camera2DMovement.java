package soundwav.entity.component;

import engine.entity.Behaviour;
import engine.entity.component.Transform;
import engine.platform.input.Input;
import engine.scene.Scene;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public class Camera2DMovement extends Behaviour
{
  private final Vector2f move;

  @Override
  public void update(Scene scene)
  {
    // reset movement vector
    this.move.zero();

    // update movement vector based on input
    if (Input.keyDown(GLFW_KEY_W))
    {
      this.move.add(1.0f, 0.0f);
    }

    if (Input.keyDown(GLFW_KEY_S))
    {
      this.move.sub(1.0f, 0.0f);
    }

    if (Input.keyDown(GLFW_KEY_A))
    {
      this.move.add(0.0f, 1.0f);
    }

    if (Input.keyDown(GLFW_KEY_D))
    {
      this.move.sub(0.0f, 1.0f);
    }

    this.entity.getComponent(Transform.class).position.add(this.move.x, this.move.y, 0.0f);
  }

  public Camera2DMovement()
  {
    this.move = new Vector2f();
  }
}
