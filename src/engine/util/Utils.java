package engine.util;

import com.badlogic.gdx.math.Vector3;
import org.joml.Quaternionf;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class Utils
{
  /**
   * Converts a libGDX {@link Vector3} to a joml {@link org.joml.Vector3f}.
   * @param vec3 The libGDX {@link Vector3}.
   * @return A corresponding JOML {@link org.joml.Vector3f}.
   */
  public static org.joml.Vector3f convert(Vector3 vec3)
  {
    return new org.joml.Vector3f(vec3.x, vec3.y, vec3.z);
  }

  /**
   * Converts a JOML {@link org.joml.Vector3f} to a libGDX {@link Vector3}.
   * @param vec3 The JOML {@link org.joml.Vector3f}.
   * @return A corresponding libGDX {@link Vector3}.
   */
  public static Vector3 convert(org.joml.Vector3f vec3)
  {
    return new Vector3(vec3.x, vec3.y, vec3.z);
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
