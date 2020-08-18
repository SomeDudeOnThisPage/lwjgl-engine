package soundwav.audio;

import engine.asset.BaseAsset;

import java.nio.ShortBuffer;

public class AudioPCM extends BaseAsset
{
  private final ShortBuffer data;
  private final int rate;
  private final int channels;

  public ShortBuffer pcm()
  {
    return this.data;
  }

  public int rate()
  {
    return this.rate;
  }

  public int channels()
  {
    return this.channels;
  }

  @Override
  public void dispose() {}

  public AudioPCM(ShortBuffer data, int rate, int channels)
  {
    this.data = data;
    this.rate = rate;
    this.channels = channels;
  }
}
