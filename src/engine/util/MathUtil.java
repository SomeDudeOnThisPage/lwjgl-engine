package engine.util;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import engine.Engine;
import engine.entity.Entity;
import engine.entity.component.Camera3D;
import engine.scene.Scene;
import engine.scene.SceneGraph;
import org.jetbrains.annotations.NotNull;
import org.joml.*;

import java.util.HashSet;

public class MathUtil
{
  /** Internal buffer to reduce memory allocation when converting between libGDX {@link Vector3} and JOML {@link Vector3f}. */
  private static final Vector3f buffer_joml_vec3 = new Vector3f();

  /** Internal buffer to reduce memory allocation when converting between libGDX {@link Vector3} and JOML {@link Vector3f}. */
  private static final Vector3 buffer_lgdx_vec3 = new Vector3();

  /** Internal buffer to reduce memory allocation when converting between libGDX {@link Vector3} and JOML {@link Vector3f}. */
  private static final Quaternionf buffer_joml_quat = new Quaternionf();

  /** Internal buffer to reduce memory allocation when converting between libGDX {@link Vector3} and JOML {@link Vector3f}. */
  private static final Quaternion buffer_lgdx_quat = new Quaternion();

  /** Internal buffer to reduce memory allocation when converting between libGDX {@link Matrix4} and JOML {@link Matrix4f}. */
  private static final float[] buffer_mat4 = new float[16];

  /** Internal buffer to reduce memory allocation when converting between libGDX {@link Matrix4} and JOML {@link Matrix4f}. */
  private static final Matrix4f buffer_joml_mat4 = new Matrix4f();

  /** Internal buffer to reduce memory allocation when converting between libGDX {@link Matrix4} and JOML {@link Matrix4f}. */
  private static final Matrix4 buffer_lgdx_mat4 = new Matrix4();

  /**
   * Converts a libGDX {@link Vector3} to a joml {@link Vector3f}.
   * @param vec3 The libGDX {@link Vector3}.
   * @return A corresponding JOML {@link Vector3f}.
   */
  @SuppressWarnings("unused")
  public static Vector3f convert(@NotNull Vector3 vec3)
  {
    return new Vector3f(vec3.x, vec3.y, vec3.z);
  }

  /**
   * Converts a JOML {@link Vector3f} to a libGDX {@link Vector3}.
   * @param vec3 The JOML {@link Vector3f}.
   * @return A corresponding libGDX {@link Vector3}.
   */
  @SuppressWarnings("unused")
  public static Vector3 convert(@NotNull Vector3f vec3)
  {
    return new Vector3(vec3.x, vec3.y, vec3.z);
  }

  /**
   * Non-Memory-Allocating implementation of {@link MathUtil#convert(Vector3)}.
   * This method uses static, temporary memory storage. This method should only be used for short-term usage of the
   * returned vector, and not in a multithreaded context, as the data in the returned vector will be overridden upon
   * another call to this method.
   * @param vec3 The libGDX {@link Vector3}.
   * @return A corresponding JOML {@link Vector3f}.
   */
  @SuppressWarnings("unused")
  public static Vector3f u_convert(@NotNull Vector3 vec3)
  {
    return MathUtil.buffer_joml_vec3.set(vec3.x, vec3.y, vec3.z);
  }

  /**
   * Non-Memory-Allocating implementation of {@link MathUtil#convert(Vector3f)}.
   * This method uses static, temporary memory storage. This method should only be used for short-term usage of the
   * returned vector, and not in a multithreaded context, as the data in the returned vector will be overridden upon
   * another call to this method.
   * @param vec3 The JOML {@link Vector3f}.
   * @return A corresponding libGDX {@link Vector3}.
   */
  @SuppressWarnings("unused")
  public static Vector3 u_convert(@NotNull Vector3f vec3)
  {
    return MathUtil.buffer_lgdx_vec3.set(vec3.x, vec3.y, vec3.z);
  }

  /**
   * Converts a JOML {@link Matrix4f} to a libGDX {@link Matrix4}.
   * @param mat4 The JOML {@link Matrix4f}.
   * @return A corresponding libGDX {@link Matrix4}.
   */
  @SuppressWarnings("unused")
  public static Matrix4 convert(@NotNull Matrix4f mat4)
  {
    return new Matrix4().set(mat4.get(MathUtil.buffer_mat4));
  }

  /**
   * Converts a libGDX {@link Matrix4} to a JOML {@link Matrix4f}.
   * @param mat4 The libGDX {@link Matrix4}.
   * @return A corresponding JOML {@link Matrix4f}.
   */
  @SuppressWarnings("unused")
  public static Matrix4f convert(@NotNull Matrix4 mat4)
  {
    return new Matrix4f().set(mat4.getValues());
  }

  /**
   * Non-Memory-Allocating implementation of {@link MathUtil#convert(Matrix4)}.
   * This method uses static, temporary memory storage. This method should only be used for short-term usage of the
   * returned matrix, and not in a multithreaded context, as the data in the returned matrix will be overridden upon
   * another call to this method.
   * @param mat4 The libGDX {@link Matrix4}.
   * @return A corresponding JOML {@link Matrix4f}.
   */
  @SuppressWarnings("unused")
  public static Matrix4f u_convert(@NotNull Matrix4 mat4)
  {
    return MathUtil.buffer_joml_mat4.set(mat4.getValues());
  }

  /**
   * Non-Memory-Allocating implementation of {@link MathUtil#convert(Matrix4f)}.
   * This method uses static, temporary memory storage. This method should only be used for short-term usage of the
   * returned matrix, and not in a multithreaded context, as the data in the returned matrix will be overridden upon
   * another call to this method.
   * @param mat4 The JOML {@link Matrix4f}.
   * @return A corresponding libGDX {@link Matrix4}.
   */
  @SuppressWarnings("unused")
  public static Matrix4 u_convert(@NotNull Matrix4f mat4)
  {
    return MathUtil.buffer_lgdx_mat4.set(mat4.get(MathUtil.buffer_mat4));
  }

  @SuppressWarnings("unused")
  public static Quaternionf u_convert(@NotNull Quaternion quat)
  {
    return MathUtil.buffer_joml_quat.set(quat.x, quat.y, quat.z, quat.w);
  }

  @SuppressWarnings("unused")
  public static Quaternion u_convert(@NotNull Quaternionf quat)
  {
    return MathUtil.buffer_lgdx_quat.set(quat.x, quat.y, quat.z, quat.w);
  }

  public static boolean bounded(Scene scene, Vector2f pos2f)
  {
    Vector2i vp = scene.viewport().size();
    return pos2f.x < vp.x && pos2f.x > 0 && pos2f.y < vp.y && pos2f.y > 0;
  }

  public static Vector3f windowToWorld(Scene scene, Entity camera, Vector2f pos2f, float z)
  {
    Matrix4f invProjectionMatrix = new Matrix4f();
    Matrix4f invViewMatrix = new Matrix4f();
    Vector4f tmpVec = new Vector4f();

    int wdwWitdh = scene.viewport().size().x;
    int wdwHeight = scene.viewport().size().y;

    float x = (2 * pos2f.x) / (float) wdwWitdh - 1.0f;
    float y = 1.0f - (2 * pos2f.y) / (float) wdwHeight;

    invProjectionMatrix.set(camera.getComponent(Camera3D.class).construct(scene.viewport()));
    invProjectionMatrix.invert();

    tmpVec.set(x, y, z, 1.0f);
    tmpVec.mul(invProjectionMatrix);
    tmpVec.z = -1.0f;
    tmpVec.w = 0.0f;

    Matrix4f viewMatrix = SceneGraph.transform(camera);
    invViewMatrix.set(viewMatrix);
    invViewMatrix.invert();
    tmpVec.mul(invViewMatrix);

    Vector3f dir = new Vector3f(tmpVec.x, tmpVec.y, tmpVec.z);
    dir.negate();

    return dir;
  }

  /**
   * Checks if a given 2D point is on a line defined by two 2D points.
   * @param point The point to be checked.
   * @param p0 Line start.
   * @param p1 Line end.
   * @param offset An offset (in pixels) to give some leeway.
   * @return Whether the point is on the line or not.
   *
   * @throws UnsupportedOperationException The offset must not be zero.
   */
  public static boolean onLine(Vector2f point, Vector2f p0, Vector2f p1, float offset)
  {
    if (offset == 0.0f)
    {
      throw new UnsupportedOperationException("offset cannot be zero - onLine would always return false due" +
        "to rounding errors");
    }

    // this works by measuring the distance between p0 and p1, and comparing the distance from point to p0 and
    // from point to p1 respectively.
    float p0p1 = p0.distance(point) + p1.distance(point);
    float distance = p0.distance(p1);
    return p0p1 <= distance + offset && p0p1 >= distance - offset;
  }

  private static final Vector2f result = new Vector2f();
  private static final Vector3f max = new Vector3f();
  private static final Vector3f min = new Vector3f();
  public static Entity raycast(Vector3f direction, Vector3f from, HashSet<Entity> entities)
  {
    MathUtil.result.zero();
    MathUtil.max.zero();
    MathUtil.min.zero();

    Entity selected = null;

    float closest = Float.POSITIVE_INFINITY;

    for (Entity entity : entities)
    {
      Matrix4f transform = SceneGraph.transform(entity);
      Vector3f translation = transform.getTranslation(new Vector3f()).negate();
      Vector3f scale = transform.getScale(new Vector3f());

      MathUtil.max.set(translation);
      MathUtil.min.set(translation);
      MathUtil.max.add(scale.x, scale.y, scale.z);
      MathUtil.min.add(-scale.x, -scale.y, -scale.z);

      boolean ray = Intersectionf.intersectRayAab(
        from,
        new Vector3f(direction.x, direction.y, direction.z),
        min, max,
        MathUtil.result
      );

      if (ray && MathUtil.result.x < closest)
      {
        closest = MathUtil.result.x;
        selected = entity;
      }
    }

    return selected;
  }

  public static Vector2f worldToWindow(Scene scene, Entity camera, Vector3f pos3f)
  {
    Matrix4f projection = camera.getComponent(Camera3D.class).construct(scene.viewport());
    Matrix4f view = SceneGraph.transform(camera);
    Vector4f position = new Vector4f(
      pos3f.x,
      pos3f.y,
      pos3f.z,
      1.0f
    );

    position.mul(projection.mul(view));

    Vector3f ndc = new Vector3f(position.x, position.y, position.z).div(position.w);
    Vector2f window = new Vector2f(ndc.x, ndc.y);
    window.x += 1.0f;
    window.x /= 2.0f;
    window.y = 1 - window.y;
    window.y /= 2.0f;

    window.x *= Engine.Display.size().x;
    window.y *= Engine.Display.size().y;

    return window;
  }

}
