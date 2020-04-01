package engine.core.gfx;

import engine.core.gfx.texture.ITextureFilter;
import engine.core.gfx.texture.Texture;
import engine.core.gfx.texture.TextureFormat;
import engine.core.gfx.texture.TextureWrap;
import engine.util.Resource;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL33C.*;
import static org.lwjgl.stb.STBImage.stbi_image_free;

public class Cubemap extends Texture
{
  private static final String[] faces = new String[]
  {
    "right", "left", "top", "bottom", "front", "back"
  };

  @Override
  public void bind()
  {
    glBindTexture(GL_TEXTURE_CUBE_MAP, this.id);
  }

  @Override
  public void unbind()
  {
    glBindTexture(GL_TEXTURE_CUBE_MAP, 0);
  }

  public Cubemap(String path, TextureFormat format, TextureWrap wrap, ITextureFilter filter)
  {

  }

  /**
   * Loads a cubemap from a given texture name.
   * @param path path of the texture beginning from the
   */
  public Cubemap(String path)
  {
    this.id = glGenTextures();
    glBindTexture(GL_TEXTURE_CUBE_MAP, this.id);

    for (int i = 0; i < 6; i++)
    {
      IntBuffer width = BufferUtils.createIntBuffer(1);
      IntBuffer height = BufferUtils.createIntBuffer(1);
      IntBuffer components = BufferUtils.createIntBuffer(1);

      ByteBuffer data = null;
      try
      {
        // STB is a blessing
        data = STBImage.stbi_load_from_memory(Resource.load("resources/textures/" + path + "/" + Cubemap.faces[i] + ".png"), width, height, components, 3);

        if (data == null) { throw new IOException("file not found"); }

        glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_SRGB, width.get(0), height.get(0),0, GL_RGB, GL_UNSIGNED_BYTE, data);
      }
      catch (IOException e)
      {
        System.err.println("could not load texture " + path + ": " + e.getMessage());
      }

      if (data != null)
      {
        stbi_image_free(data);
      }
    }

    // texture parameters
    glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
  }
}