package engine.util;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;

import static org.lwjgl.stb.STBImage.stbi_image_free;

public class Resource
{
  public static class STBILoader
  {
    private IntBuffer width;
    private IntBuffer height;
    private IntBuffer components;
    private ByteBuffer data;

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
      //if (this.data == null) { throw new Error("no texture data available"); }
      return this.data;
    }

    public void load(String path, int channels)
    {
      if (this.data != null)
      {
        stbi_image_free(this.data);
      }

      this.width.clear();
      this.height.clear();
      this.components.clear();
      this.data = null;

      this.data = STBImage.stbi_load("resources/textures/" + path, this.width, this.height, this.components, channels);
    }

    public STBILoader()
    {
      this.width = BufferUtils.createIntBuffer(1);
      this.height = BufferUtils.createIntBuffer(1);
      this.components = BufferUtils.createIntBuffer(1);
    }
  }

  private static final DocumentBuilderFactory factory = DocumentBuilderFactory.newDefaultInstance();

  public static Document loadXML(@NotNull String resource)
  {
    try
    {
      DocumentBuilder builder = Resource.factory.newDocumentBuilder();
      return builder.parse(resource);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }

    return null;
  }

  public static ByteBuffer load(String resource) throws IOException
  {
    ByteBuffer buffer;
    File file = new File(resource);

    if (file.isFile())
    {
      FileInputStream input = new FileInputStream(file);
      FileChannel channel = input.getChannel();

      buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());

      channel.close();
      input.close();

      return buffer;
    }

    throw new IOException("no such file");
  }
}
