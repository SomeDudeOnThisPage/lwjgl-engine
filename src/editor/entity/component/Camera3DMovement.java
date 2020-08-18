package editor.entity.component;

import engine.Console;
import engine.entity.Behaviour;
import engine.entity.EditorComponent;
import engine.entity.EditorField;
import engine.entity.component.Camera3D;
import engine.entity.component.Transform;
import engine.gfx.buffer.UniformBuffer;
import engine.gfx.uniform.BufferedUniform;
import engine.platform.input.Input;
import engine.scene.Scene;
import engine.scene.SceneGraph;
import org.joml.*;

import java.lang.Math;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_CONTROL;

@EditorComponent
public class Camera3DMovement extends Behaviour
{
  @EditorField
  public float sensitivity = 1.0f;

  @EditorField
  public float speed = 1.0f;

  private float pitch;
  private float yaw;

  @Override
  public void onComponentAttached()
  {
    this.pitch = 0.0f;
    this.yaw = 0.0f;
  }

  @Override
  public void update(Scene scene)
  {
    // check for sensitivity / speed changes
    if ((Float) Console.getConVar("editor_camera_sensitivity").get() != this.sensitivity)
    {
      this.sensitivity = (Float) Console.getConVar("editor_camera_sensitivity").get();
    }

    if ((Float) Console.getConVar("editor_camera_speed").get() != this.speed)
    {
      this.speed = (Float) Console.getConVar("editor_camera_speed").get();
    }

    Transform transform = this.entity.getComponent(Transform.class);
    Vector3f forward = transform.rotation.positiveZ(new Vector3f()).normalize().mul(this.speed * 0.01f);
    Vector3f left = transform.rotation.positiveX(new Vector3f()).normalize().mul(this.speed * 0.01f);
    Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f).mul(this.speed * 0.01f);

    if (Input.keyDown(GLFW_KEY_LEFT_SHIFT))
    {
      forward.mul(5);
      left.mul(5);
      up.mul(5);
    }

    if (Input.keyDown(GLFW_KEY_W))
    {
      transform.position.add(forward);
    }

    if (Input.keyDown(GLFW_KEY_S))
    {
      transform.position.add(forward.negate());
    }

    if (Input.keyDown(GLFW_KEY_A))
    {
      transform.position.add(left);
    }

    if (Input.keyDown(GLFW_KEY_D))
    {
      transform.position.add(left.negate());
    }

    if (Input.keyDown(GLFW_KEY_SPACE))
    {
      transform.position.add(up.negate());
    }

    if (Input.keyDown(GLFW_KEY_LEFT_CONTROL))
    {
      transform.position.add(up);
    }

    // mouse look
    Vector2d drag = Input.getDrag();

    this.yaw   += (float) drag.x * sensitivity / 10.0f;
    this.pitch += (float) drag.y * sensitivity / 10.0f;

    transform.rotation.identity()
      .rotateX((float) Math.toRadians(this.pitch))
      .rotateY((float) Math.toRadians(this.yaw));

    // update uniform buffer
    UniformBuffer ubo = scene.renderer().ubo();
    BufferedUniform<Matrix4f> u_projection = (BufferedUniform<Matrix4f>) ubo.getUniform("u_projection");
    u_projection.set(this.entity.getComponent(Camera3D.class).construct(scene.viewport()));
    ubo.update("u_projection");

    BufferedUniform<Matrix4f> u_view = (BufferedUniform<Matrix4f>) ubo.getUniform("u_view");
    u_view.set(SceneGraph.transform(this.entity));
    ubo.update("u_view");

    BufferedUniform<Vector4f> u_view_position = (BufferedUniform<Vector4f>) ubo.getUniform("u_view_position");
    Vector3f position = SceneGraph.transform(this.entity).getTranslation(new Vector3f()).negate();
    u_view_position.set(new Vector4f(position.x, position.y, position.z, 0.0f));
    ubo.update("u_view_position");
  }
}
