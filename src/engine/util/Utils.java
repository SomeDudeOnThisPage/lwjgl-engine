package engine.util;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class Utils
{
  /** Internal buffer to reduce memory allocation when converting between libGDX {@link Vector3} and JOML {@link Vector3f}. */
  private static final Vector3f buffer_joml_vec3 = new Vector3f();

  /** Internal buffer to reduce memory allocation when converting between libGDX {@link Vector3} and JOML {@link Vector3f}. */
  private static final Vector3  buffer_lgdx_vec3 = new Vector3();

  /** Internal buffer to reduce memory allocation when converting between libGDX {@link Matrix4} and JOML {@link Matrix4f}. */
  private static final float[] buffer_mat4 = new float[16];

  /** Internal buffer to reduce memory allocation when converting between libGDX {@link Matrix4} and JOML {@link Matrix4f}. */
  private static final Matrix4f buffer_joml_mat4 = new Matrix4f();

  /** Internal buffer to reduce memory allocation when converting between libGDX {@link Matrix4} and JOML {@link Matrix4f}. */
  private static final Matrix4  buffer_lgdx_mat4 = new Matrix4();

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
   * Non-Memory-Allocating implementation of {@link Utils#convert(Vector3)}.
   * This method uses static, temporary memory storage. This method should only be used for short-term usage of the
   * returned vector, and not in a multithreaded context, as the data in the returned vector will be overridden upon
   * another call to this method.
   * @param vec3 The libGDX {@link Vector3}.
   * @return A corresponding JOML {@link Vector3f}.
   */
  @SuppressWarnings("unused")
  public static Vector3f u_convert(@NotNull Vector3 vec3)
  {
    return Utils.buffer_joml_vec3.set(vec3.x, vec3.y, vec3.z);
  }

  /**
   * Non-Memory-Allocating implementation of {@link Utils#convert(Vector3f)}.
   * This method uses static, temporary memory storage. This method should only be used for short-term usage of the
   * returned vector, and not in a multithreaded context, as the data in the returned vector will be overridden upon
   * another call to this method.
   * @param vec3 The JOML {@link Vector3f}.
   * @return A corresponding libGDX {@link Vector3}.
   */
  @SuppressWarnings("unused")
  public static Vector3 u_convert(@NotNull Vector3f vec3)
  {
    return Utils.buffer_lgdx_vec3.set(vec3.x, vec3.y, vec3.z);
  }

  /**
   * Converts a JOML {@link Matrix4f} to a libGDX {@link Matrix4}.
   * @param mat4 The JOML {@link Matrix4f}.
   * @return A corresponding libGDX {@link Matrix4}.
   */
  @SuppressWarnings("unused")
  public static Matrix4 convert(@NotNull Matrix4f mat4)
  {
    return new Matrix4().set(mat4.get(Utils.buffer_mat4));
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
   * Non-Memory-Allocating implementation of {@link Utils#convert(Matrix4)}.
   * This method uses static, temporary memory storage. This method should only be used for short-term usage of the
   * returned matrix, and not in a multithreaded context, as the data in the returned matrix will be overridden upon
   * another call to this method.
   * @param mat4 The libGDX {@link Matrix4}.
   * @return A corresponding JOML {@link Matrix4f}.
   */
  @SuppressWarnings("unused")
  public static Matrix4f u_convert(@NotNull Matrix4 mat4)
  {
    return Utils.buffer_joml_mat4.set(mat4.getValues());
  }

  /**
   * Non-Memory-Allocating implementation of {@link Utils#convert(Matrix4f)}.
   * This method uses static, temporary memory storage. This method should only be used for short-term usage of the
   * returned matrix, and not in a multithreaded context, as the data in the returned matrix will be overridden upon
   * another call to this method.
   * @param mat4 The JOML {@link Matrix4f}.
   * @return A corresponding libGDX {@link Matrix4}.
   */
  @SuppressWarnings("unused")
  public static Matrix4 u_convert(@NotNull Matrix4f mat4)
  {
    return Utils.buffer_lgdx_mat4.set(mat4.get(Utils.buffer_mat4));
  }

  @SuppressWarnings("unused")
  public static float[] toPrimitiveF(ArrayList<Float> list)
  {
    float[] array = new float[list.size()];
    int i = 0;

    for (Float f : list)
    {
      array[i++] = (f != null ? f : Float.NaN);
    }

    return array;
  }

  @SuppressWarnings("unused")
  public static int[] toPrimitiveI(ArrayList<Integer> list)
  {
    int[] array = new int[list.size()];
    int j = 0;

    for (Integer i : list)
    {
      array[j++] = (i != null ? i : 0);
    }

    return array;
  }

  @SuppressWarnings("unused")
  public static byte[] toArrayB(float[] values)
  {
    byte[] out = new byte[values.length * Float.BYTES];

    int i = 0;
    for (float value : values)
    {
      out[i++] = (byte) value;
    }

    return out;
  }

  @SuppressWarnings("unused")
  public static byte[] toArrayB(int[] values)
  {
    byte[] out = new byte[values.length * Short.BYTES];

    int i = 0;
    for (int value : values)
    {
      out[i++] = (byte) ((short) value);
    }

    return out;
  }

  @SuppressWarnings("unused")
  public static ByteBuffer toBufferB(float[] values)
  {
    ByteBuffer buffer = ByteBuffer.allocateDirect(4 * values.length);

    for (float value : values)
    {
      buffer.putFloat(value);
    }

    return buffer;
  }

  @SuppressWarnings("unused")
  public static ByteBuffer toBufferB(int[] values)
  {
    ByteBuffer buffer = ByteBuffer.allocateDirect(4 * values.length);

    for (int value : values)
    {
      buffer.putInt(value);
    }

    return buffer;
  }
}
