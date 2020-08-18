package soundwav.ui;

import engine.scene.Scene;
import engine.ui.DebugGUI;
import imgui.ImGui;

public class AudioPlayer extends DebugGUI
{
  public static float volume = 0.5f;
  float[] plot = new float[]
  {
    0.1f, 2.0f, 0.2f, 2.0f, 0.4f, 2.3f
  };

  int offset = 0;
  int samples = 1;

  public void setPlot(float[] plot)
  {
    this.plot = plot;
  }

  public void setOffset(int seconds)
  {
    this.offset = seconds;
  }

  public void setSamples(int samples)
  {
    this.samples = samples;
  }

  private int s = 0;
  @Override
  public void render(Scene scene)
  {
    if (ImGui.begin("Player"))
    {
      float[] value = new float[] { volume };
      if (ImGui.sliderFloat("Volume", value, 0.01f, 0.5f))
      {
        volume = value[0];
      }

      ImGui.text("Current Samples: " + (this.offset));
      ImGui.text("Remaining Samples: " + (this.plot.length - this.offset));

      ImGui.text("Stuff: " + (int) (44000.0f / 1000.0f));

      if (ImGui.sliderFloat("Volume", value, 0.01f, 0.5f))
      {
        volume = value[0];
      }

      int[] start = new int[] { s };
      if (ImGui.sliderInt("Start", start, 0, this.plot.length))
      {
        this.s = start[0];
      }

      ImGui.plotLines("Test Plot Lines",
        this.plot, this.plot.length / 44000, this.offset, "", -1.0f, 1.0f, ImGui.getWindowWidth(), 250.0f, 44000);
      ImGui.plotLines("Full", this.plot, this.plot.length);
      ImGui.end();
    }
  }
}
