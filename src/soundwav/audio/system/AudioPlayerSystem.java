package soundwav.audio.system;

import engine.entity.*;
import engine.scene.Scene;
import imgui.ImBool;
import imgui.ImGui;
import soundwav.audio.component.SoundSource;

import java.util.ArrayList;

public class AudioPlayerSystem extends EntityCollection implements EntitySystem
{
  @Override
  public <T extends EntityComponent> Class<T>[] components()
  {
    return new Class[]
    {
      SoundSource.class
    };
  }

  @Override
  public EntityComponentSystem.SystemPriority priority()
  {
    return EntityComponentSystem.SystemPriority.LEVEL_3;
  }

  private final boolean[] sliding = new boolean[100];
  private float lastSlide = 0.0f;

  @Override
  public void update(Scene scene)
  {
    float[] sl = new float[1];

    if (ImGui.begin("Audio Player"))
    {
      ImGui.text("Audio Files");
      int i = 0;
      for (Entity entity : this.entities)
      {
        SoundSource sound = entity.getComponent(SoundSource.class);
        final ImBool playing = new ImBool(sound.source.playing());
        ImGui.checkbox(entity.name() + "###playing" + entity, playing);

        if (playing.get())
        {
          sound.source.play();
        }
        else
        {
          sound.source.pause();
        }

        ImGui.sameLine();

        sl[0] = sound.source.seconds();
        if (ImGui.sliderFloat("###offset" + entity, sl, 0.0f, sound.source.lengthSeconds()) && !ImGui.isItemDeactivated())
        {
          this.sliding[i] = true;
          this.lastSlide = sl[0];
        }

        if (this.sliding[i] && !ImGui.getIO().getMouseDown(0))
        {
          this.sliding[i] = false;
          sound.source.seconds(this.lastSlide);
        }

        sl[0] = sound.source.gain();
        ImGui.sliderFloat("Volume###volume" + entity, sl, 0.0f, 1.0f);
        sound.source.gain(sl[0]);

        i++;
      }
      ImGui.end();
    }
  }
}
