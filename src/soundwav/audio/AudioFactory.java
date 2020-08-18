package soundwav.audio;

import engine.Console;
import engine.asset.load.AssetFactory;
import engine.util.Memory;
import engine.util.XMLUtil;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.w3c.dom.Element;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.stb.STBVorbis.*;

public class AudioFactory implements AssetFactory<SoundBuffer>
{
  private ShortBuffer read(String resource, STBVorbisInfo info)
  {
    try (MemoryStack stack = MemoryStack.stackPush())
    {
      ByteBuffer vorbis = Memory.load(resource);
      IntBuffer error = stack.mallocInt(1);
      assert vorbis != null;

      long decoder = stb_vorbis_open_memory(vorbis, error, null);
      if (decoder == MemoryUtil.NULL)
      {
        throw new RuntimeException("failed to open ogg vorbis file - '" + error.get(0) + "'");
      }

      stb_vorbis_get_info(decoder, info);

      int channels = info.channels();
      int lengthSamples = stb_vorbis_stream_length_in_samples(decoder);

      Console.print("creating pcm data with " + channels + " channels and a length of " + lengthSamples + " samples");

      ShortBuffer pcm = MemoryUtil.memAllocShort(lengthSamples);

      int limit = stb_vorbis_get_samples_short_interleaved(decoder, channels, pcm) * channels;
      pcm.limit(limit);

      Console.print("total pcm buffer size of " + limit + " shorts");

      stb_vorbis_close(decoder);

      return pcm;
    }
  }

  @Override
  public String tag()
  {
    return "audio";
  }

  @Override
  public SoundBuffer load(Element xml) throws AssetLoadingException
  {
    try(STBVorbisInfo info = STBVorbisInfo.malloc())
    {
      String source = XMLUtil.element(xml, "source").getTextContent();

      ShortBuffer raw = read(source, info);

      AudioPCM data = new AudioPCM(raw, info.sample_rate(), info.channels());
      return new SoundBuffer(data);
    }
    catch(Exception e)
    {
      e.printStackTrace();
      throw new AssetLoadingException("failed to load sound asset - '" + e + "'");
    }
  }
}
