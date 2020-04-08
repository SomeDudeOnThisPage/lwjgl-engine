package engine.core.entity.component;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import engine.Engine;
import engine.core.Input;
import engine.core.Window;
import engine.core.entity.component.physics.CollisionShapeComponent;
import engine.core.scene.Player;
import engine.core.scene.Scene;
import engine.util.settings.EngineSetting;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector2d;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_Q;

public class FPCMouseOrientationComponent extends ScriptComponent
{
  private static final Vector3f ANGLE_UP = new Vector3f(0.0f, 1.0f, 0.0f);
  private static final Vector3f ANGLE_RIGHT = new Vector3f(1.0f, 0.0f, 0.0f);

  private float pitch;
  private float yaw;

  private float sensitivity = 0.25f;

  @Override
  public void onComponentAttached()
  {
    this.pitch = 0;
    this.yaw = 0;
  }

  @Override
  public void update(Scene scene)
  {
    // update camera orientation based on input
    Vector2d drag = new Vector2d();
    if (Engine.window.getCursorMode() == Window.CURSOR.NORMAL)
    {
      drag.set(Input.getDrag());
    }
    else
    {
      drag.set(Input.getMovement());
    }

    this.pitch -= (float) drag.x * sensitivity;
    this.yaw -= (float) drag.y * sensitivity;

    // reset position to parent transform
    if (Input.keyDown(GLFW_KEY_Q))
    {
      this.entity.get(TransformComponent.class).position.mul(0.0f);
      this.entity.get(TransformComponent.class).position.y = 0.5f;
    }

    this.entity.get(TransformComponent.class).position.sub(new Vector3f(0.0f, 0.0f, (float) Input.getScroll().y));

    Quaternionf p = new Quaternionf().fromAxisAngleDeg(ANGLE_UP, this.pitch);
    Quaternionf y = new Quaternionf().fromAxisAngleDeg(ANGLE_RIGHT, this.yaw);

    p.mul(y);

    Matrix4 out = new Matrix4();
    scene.get("player").get(CollisionShapeComponent.class).body.getWorldTransform(out);

    // todo: short term memory-efficient JOML->GDX math-primitives conversion with stack allocation...

    Vector3 translation = new Vector3();
    out.getTranslation(translation);

    Matrix4f jomlr = new Matrix4f()
      .rotate(new Quaternionf().fromAxisAngleDeg(ANGLE_UP, this.pitch))
      .setTranslation(translation.x, translation.y, translation.z);

    Matrix4 rout = new Matrix4()
      .set(jomlr.get(new float[16]));

    scene.get("player").get(CollisionShapeComponent.class).body.setWorldTransform(rout);
    ((Player) scene.get("player")).getCamera().get(TransformComponent.class).rotation = y;
  }
}