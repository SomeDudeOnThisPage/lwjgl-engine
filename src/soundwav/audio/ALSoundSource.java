package soundwav.audio;

import engine.gfx.BaseAssetBindable;

import static org.lwjgl.openal.AL11.*;
import static engine.Engine.*;

/**
 * Sound sources are assets, but are NOT managed by the AssetManager.
 * The SoundSource entity component is responsible for the lifecycle of the sound asset.
 */
public class ALSoundSource extends BaseAssetBindable
{
  private SoundBuffer source;
  private boolean playing;

  private int samples;
  private float seconds;

  private float gain;

  public boolean playing()
  {
    return this.playing;
  }

  public void set(String source)
  {
    if (this.source != null)
    {
      AssetManager.release(this.source, SoundBuffer.class);
      this.seconds = -1;
      this.samples = -1;
    }

    this.source = AssetManager.request(source, SoundBuffer.class);
    alSourcei(this.id, AL_BUFFER, this.source.id());
  }

  public void loop(boolean looping)
  {
    alSourcei(this.id, AL_LOOPING, looping ? AL_TRUE : AL_FALSE);
  }

  public float gain()
  {
    return this.gain;
  }

  public void gain(float gain)
  {
    if (this.gain != gain)
    {
      this.gain = gain;
      alSourcef(this.id, AL_GAIN, gain);
    }
  }

  /**
   * Returns the amount of frames this source has been played for.
   * @return Amount of frames this source has been played for.
   */
  public int frames()
  {
    return alGetSourcei(this.id, AL_SAMPLE_OFFSET);
  }

  /**
   * Returns the amount of seconds this source has been played for.
   * @return Amount of seconds this source has been played for.
   */
  public float seconds()
  {
    return alGetSourcef(this.id, AL_SEC_OFFSET);
  }

  public void seconds(float seconds)
  {
    alSourceRewind(this.id);
    alSourcef(this.id, AL_SEC_OFFSET, seconds);
    alSourcePlay(this.id);
  }

  public float lengthSeconds()
  {
    // only calculate when we need to
    if (this.seconds == -1 || this.samples == -1)
    {
      int size = alGetBufferi(this.id, AL_SIZE);
      //int channels = alGetBufferi(this.id, AL_CHANNELS);
      int bits = alGetBufferi(this.id, AL_BITS);

      this.samples = size * 8 / (/*channels * */ bits);
      this.seconds = (float) samples / (float) alGetBufferi(this.id, AL_FREQUENCY);
    }

    return this.seconds;
  }

  public void play()
  {
    if (!this.playing)
    {
      this.bind();
      this.playing = true;
    }
  }

  public void pause()
  {
    if (this.playing)
    {
      this.unbind();
      this.playing = false;
    }
  }

  public void stop()
  {
    if (this.playing)
    {
      alSourceStop(this.id);
      this.playing = false;
    }
  }

  @Override
  public void bind()
  {
    alSourcePlay(this.id);
  }

  @Override
  public void unbind()
  {
    alSourcePause(this.id);
  }

  @Override
  public void dispose()
  {
    alDeleteSources(this.id);
  }

  public ALSoundSource(boolean loop, boolean relative)
  {
    this.id = alGenSources();
    this.playing = false;
    this.samples = -1;
    this.seconds = -1;

    this.gain = 1.0f;

    if (loop)
    {
      alSourcei(this.id, AL_LOOPING, AL_TRUE);
    }
    if (relative)
    {
      alSourcei(this.id, AL_SOURCE_RELATIVE, AL_TRUE);
    }
  }
}
