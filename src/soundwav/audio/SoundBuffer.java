package soundwav.audio;

import engine.Engine;
import engine.gfx.BaseAssetBindable;

import static org.lwjgl.openal.AL11.*;

public class SoundBuffer extends BaseAssetBindable
{
  private final AudioPCM data;

  public AudioPCM info()
  {
    return this.data;
  }

  @Override
  public void bind() {}

  @Override
  public void unbind() {}

  @Override
  public void dispose()
  {
    Engine.Log.info("disposed of AL SoundBuffer with ID '" + this.id + "'");
    alDeleteBuffers(this.id);
  }

  public SoundBuffer(AudioPCM data)
  {
    this.data = data;

    this.id = alGenBuffers();
    alBufferData(this.id, data.channels() == 1 ? AL_FORMAT_MONO16 : AL_FORMAT_STEREO16, data.pcm(), data.rate());
  }
}
