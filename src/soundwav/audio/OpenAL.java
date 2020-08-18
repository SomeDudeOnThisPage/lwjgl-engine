package soundwav.audio;

import engine.Engine;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.openal.ALC10.*;

public class OpenAL
{
  private static long device;

  public static void initialize()
  {
    device = alcOpenDevice((ByteBuffer) null);
    if(device == MemoryUtil.NULL)
    {
      Engine.Log.error("Unable to open default audio device");
      return;
    }

    ALCCapabilities capabilities = ALC.createCapabilities(device);
    long context = alcCreateContext(device, (IntBuffer) null);
    if(context == MemoryUtil.NULL)
    {
      Engine.Log.error("Unable to create ALC Context");
      return;
    }

    alcMakeContextCurrent(context);
    AL.createCapabilities(capabilities);
  }

  public static void destroy()
  {
    alcCloseDevice(device);
    ALC.destroy();
  }
}
