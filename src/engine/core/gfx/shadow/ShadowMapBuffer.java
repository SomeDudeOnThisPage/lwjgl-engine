package engine.core.gfx.shadow;

import engine.core.gfx.FrameBuffer;
import org.lwjgl.BufferUtils;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL30C.*;

public class ShadowMapBuffer extends FrameBuffer
{
  private IntBuffer maps;
  private IntBuffer cubes;

  @Override
  public void clear()
  {
    this.bind();
    glDrawBuffers(this.maps);
    glClear(GL_DEPTH_BUFFER_BIT);
  }

  public void bindmap(int map)
  {
    this.bind();
    glDrawBuffers(map);
  }

  public void bindcube(int cube)
  {

  }

  public ShadowMapBuffer(int resolution, int maps, int cubes)
  {
    super(resolution, resolution);

    this.maps = BufferUtils.createIntBuffer(maps);
    for (int i = 0; i < maps; i++)
    {
      this.maps.put(GL_COLOR_ATTACHMENT0 + maps);
    }
    this.maps.flip();

    this.cubes = BufferUtils.createIntBuffer(cubes);
    for (int i = 0; i < cubes; i++)
    {
      this.cubes.put(GL_COLOR_ATTACHMENT0 + (maps - 1) + cubes);
    }
    this.cubes.flip();
  }
}