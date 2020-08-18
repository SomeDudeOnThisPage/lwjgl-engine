package engine.util;

import engine.gfx.opengl.shader.GLShaderProgram;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.BufferUtils;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;

public final class Memory
{
  /**
   * This internal static class provides static final nio {@link java.nio.Buffer}s with fixed sizes for
   * short term use from the main {@link Thread} interacting with the rendering API, meaning that the memory
   * is to be assumed undefined before any data has been set short term.
   * <p>
   *   This can be useful, e.g. for binding uniforms to a {@link GLShaderProgram} program, without allocating
   *   memory each frame.
   * </p>
   */
  public static final class Buffer
  {
    @SuppressWarnings("unused") public static final FloatBuffer buffer1f = BufferUtils.createFloatBuffer(1);
    @SuppressWarnings("unused") public static final FloatBuffer buffer2f = BufferUtils.createFloatBuffer(2);
    @SuppressWarnings("unused") public static final FloatBuffer buffer3f = BufferUtils.createFloatBuffer(3);
    @SuppressWarnings("unused") public static final FloatBuffer buffer4f = BufferUtils.createFloatBuffer(4);
    @SuppressWarnings("unused") public static final FloatBuffer buffer9f = BufferUtils.createFloatBuffer(9);
    @SuppressWarnings("unused") public static final FloatBuffer buffer16f = BufferUtils.createFloatBuffer(16);

    @SuppressWarnings("unused") public static final IntBuffer buffer1i = BufferUtils.createIntBuffer(1);
    @SuppressWarnings("unused") public static final IntBuffer buffer2i = BufferUtils.createIntBuffer(2);
    @SuppressWarnings("unused") public static final IntBuffer buffer3i = BufferUtils.createIntBuffer(3);
    @SuppressWarnings("unused") public static final IntBuffer buffer4i = BufferUtils.createIntBuffer(4);
    @SuppressWarnings("unused") public static final IntBuffer buffer9i = BufferUtils.createIntBuffer(9);
    @SuppressWarnings("unused") public static final IntBuffer buffer16i = BufferUtils.createIntBuffer(16);
  }

  @Nullable
  public static ByteBuffer load(String path)
  {
    ByteBuffer buffer = null;

    try
    {
      File file = new File(path);

      if (file.isFile())
      {
        FileInputStream input = new FileInputStream(file);
        FileChannel channel = input.getChannel();

        buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());

        channel.close();
        input.close();
      }
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }

    return buffer;
  }
}
