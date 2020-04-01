package engine.core.gfx.texture;

import engine.util.Resource;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;
import org.lwjgl.stb.STBImage;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.EXTTextureFilterAnisotropic.*;
import static org.lwjgl.opengl.GL33C.*;
import static org.lwjgl.stb.STBImage.*;

public class Texture
{
  private static final Resource.STBILoader stb_loader = new Resource.STBILoader();

  protected int id;
  protected int width;
  protected int height;

  protected ITextureFilter filter;
  protected TextureWrap wrap;
  protected TextureFormat format;

  public int getID() { return this.id; }

  public void bind()
  {
    this.bind(0);
  }

  public void bind(int slot)
  {
    glActiveTexture(GL_TEXTURE0 + slot);
    glBindTexture(GL_TEXTURE_2D, this.id);
  }

  public void unbind()
  {
    this.unbind(0);
  }

  public void unbind(int slot)
  {
    glActiveTexture(GL_TEXTURE0 + slot);
    glBindTexture(GL_TEXTURE_2D, 0);
  }

  public Texture(String path, TextureFormat format, TextureWrap wrap, ITextureFilter filter)
  {
    this.format = format;
    this.filter = filter;
    this.wrap = wrap;

    this.id = glGenTextures();
    glBindTexture(GL_TEXTURE_2D, this.id);

    Texture.stb_loader.load(path, format.components());
    glTexImage2D(GL_TEXTURE_2D,
                 0,
                 format.internal(),
                 Texture.stb_loader.width(),
                 Texture.stb_loader.height(),
                 0, format.type(),
                 format.data(),
                 Texture.stb_loader.data()
    );

    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, wrap.s());
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, wrap.t());

    this.filter.apply();
    this.unbind();
  }

  /**
   * Creates and binds an empty texture with the defined parameters.
   * @param width texture width
   * @param height texture height
   * @param type0 color type of the texture
   * @param type1 color type of the texture
   * @param type data type of the texture
   */
  public Texture(int width, int height, int type0, int type1, int type)
  {
    this.id = glGenTextures();
    this.width = width;
    this.height = height;

    glBindTexture(GL_TEXTURE_2D, this.id);

    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

    glTexImage2D(GL_TEXTURE_2D, 0, type0, width, height, 0, type1, type, (ByteBuffer) null);
  }

  public Texture(int width, int height, int type0, int type1, boolean depth)
  {
    int mode = GL_UNSIGNED_BYTE;

    this.id = glGenTextures();
    this.width = width;
    this.height = height;

    if (depth)
    {
      mode = GL_FLOAT;
    }

    glBindTexture(GL_TEXTURE_2D, this.id);

    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

    glTexImage2D(GL_TEXTURE_2D, 0, type0, width, height, 0, type1, mode, (ByteBuffer) null);
  }

  public Texture(String name)
  {
    this(name, GL_RGBA, GL_RGBA);
  }

  public Texture(String name, boolean gamma)
  {
    this(name, GL_SRGB, GL_RGBA);
  }

  public Texture() {}

  public Texture(String name, int type0, int type1)
  {
    IntBuffer width = BufferUtils.createIntBuffer(1);
    IntBuffer height = BufferUtils.createIntBuffer(1);
    IntBuffer components = BufferUtils.createIntBuffer(1);

    ByteBuffer data = null;
    try
    {
      data = STBImage.stbi_load_from_memory(Resource.load("resources/textures/" + name + ".png"), width, height, components, 4);
    }
    catch (IOException e)
    {
      System.err.println("could not load texture " + name + ": " + e.getMessage());
    }

    this.id = glGenTextures();
    this.width = width.get(0);
    this.height = height.get(0);

    glBindTexture(GL_TEXTURE_2D, this.id);

    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

    glTexImage2D(GL_TEXTURE_2D, 0, type0, width.get(), height.get(), 0, type1, GL_UNSIGNED_BYTE, data);

    glGenerateMipmap(GL_TEXTURE_2D);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR_MIPMAP_LINEAR);
    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_LOD_BIAS, -0.1f);

    if (GL.getCapabilities().GL_EXT_texture_filter_anisotropic)
    {
      float amount = Math.min(4.0f, glGetFloat(GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
      glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAX_ANISOTROPY_EXT, amount);
    }

    glBindTexture(GL_TEXTURE_2D, 0);
    if (data != null)
      stbi_image_free(data);
  }
}