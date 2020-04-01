package engine.util;

import org.joml.Quaternionf;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class Utils
{
  private static ArrayList<ByteBuffer> buffers = new ArrayList<>();

  /**
   * Converts a JOML {@link org.joml.Vector3f} to a vecmath {@link Vector3f}.
   * @param vec3 The JOML {@link org.joml.Vector3f}.
   * @return A corresponding vecmath {@link Vector3f}.
   */
  public static Vector3f convert(org.joml.Vector3f vec3)
  {
    return new Vector3f(vec3.x, vec3.y, vec3.z);
  }

  /**
   * Converts a vecmath {@link Vector3f} to a joml {@link org.joml.Vector3f}.
   * @param vec3 The vecmath {@link Vector3f}.
   * @return A corresponding JOML {@link org.joml.Vector3f}.
   */
  public static org.joml.Vector3f convert(Vector3f vec3)
  {
    return new org.joml.Vector3f(vec3.x, vec3.y, vec3.z);
  }

  public static Quat4f convert(Quaternionf joml)
  {
    return new Quat4f(joml.x, joml.y, joml.z, joml.w);
  }

  public static Quaternionf convert(Quat4f vecmath)
  {
    return new Quaternionf(vecmath.x, vecmath.y, vecmath.z, vecmath.w);
  }

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

  public static ByteBuffer toBufferB(float[] values)
  {
    ByteBuffer buffer = ByteBuffer.allocateDirect(4 * values.length);

    for (float value : values)
    {
      buffer.putFloat(value);
    }

    return buffer;
  }

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
