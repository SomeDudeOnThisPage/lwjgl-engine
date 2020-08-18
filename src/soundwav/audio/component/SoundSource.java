package soundwav.audio.component;

import engine.entity.EntityComponent;
import soundwav.audio.ALSoundSource;

public class SoundSource extends EntityComponent
{
  public ALSoundSource source;

  /** Private because this cannot simply be set, we need to perform additional operations in a setter! */
  private String asset;

  @Override
  public void onComponentAttached()
  {
    if (this.source == null)
    {
      this.source = new ALSoundSource(false, false);
    }
  }

  @Override
  public void onComponentRemoved()
  {
    if (this.source != null)
    {
      this.source.dispose();
      this.source = null;
    }
  }

  public SoundSource(String source)
  {
    this.source = new ALSoundSource(false, false);
    this.source.set(source);
  }
}
