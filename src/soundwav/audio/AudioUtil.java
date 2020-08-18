package soundwav.audio;

import engine.Engine;

import java.nio.ShortBuffer;

public class AudioUtil
{
  public static float[] pcmFloat(ShortBuffer pcm, int sampleRate)
  {
    float[] result = new float[pcm.limit()];
    float rate = (float) sampleRate;

    for (int i = 0; i < pcm.limit(); i++)
    {
      result[i] = pcm.get(i) / rate;
      if (result[i] < -1 || result[i] > 1)
      {
        int squashed = result[i] > 1 ? 1 : -1;
        Engine.Log.warning("wrong sample rate of " + sampleRate + "hz - normalizing " + result[i] + " to " + squashed);
        result[i] = squashed;
      }
    }

    return result;
  }
}
