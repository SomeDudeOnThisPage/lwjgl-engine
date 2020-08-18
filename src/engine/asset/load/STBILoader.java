package engine.asset.load;

import engine.Engine;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.stb.STBImage.stbi_image_free;

public class STBILoader
{
  private final IntBuffer width;
  private final IntBuffer height;
  private final IntBuffer components;
  private ByteBuffer data;

  private final boolean loaded;

  public int width()
  {
    return this.width.get(0);
  }

  public int height()
  {
    return this.height.get(0);
  }

  public int channels()
  {
    return this.components.get(0);
  }

  public ByteBuffer data()
  {
    return this.data;
  }

  public void load(String path, int channels)
  {
    if (!this.loaded)
    {
      this.width.clear();
      this.height.clear();
      this.components.clear();
      this.data = null;

      this.data = STBImage.stbi_load(path, this.width, this.height, this.components, channels);
    }
  }

  public void free()
  {
    if (this.data != null)
    {
      stbi_image_free(this.data);
    }
  }

  public STBILoader()
  {
    this.width = BufferUtils.createIntBuffer(1);
    this.height = BufferUtils.createIntBuffer(1);
    this.components = BufferUtils.createIntBuffer(1);
    this.loaded = false;
  }
}