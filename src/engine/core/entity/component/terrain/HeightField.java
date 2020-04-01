package engine.core.entity.component.terrain;

import org.joml.Vector2i;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class HeightField
{
  private static final float COLOR_MAX = 255 * 255 * 255;

  /**
   * Data of the heightfield, stored as floats.
   * Access a certain value by row * size.x + y
   */
  private float[][] heights;

  /**
   * Size of the heightfield in rows and columns.
   */
  private Vector2i size;

  /**
   * Returns the height a vertex of a terrain should have.
   * @param x position of the vertex on the x-axis
   * @param y position of the vertex on the y-(z-)axis
   * @return height value
   */
  public float getHeight(int x, int y)
  {
    if (x < 0 || x >= this.size.x || y < 0 || y >= this.size.y)
    {
      return 0;
    }

    return this.heights[x][y];
  }

  public HeightField(String heightmap, int x, int y)
  {
    this.size = new Vector2i(x, y);
    this.heights = new float[x][y];

    try
    {
      BufferedImage image = ImageIO.read(new File("resources/heightmaps/" + heightmap + ".png"));

      // construct heightfield based on image data
      for (int i = 0; i < this.size.x; i++)
      {
        for (int j = 0; j < this.size.y; j++)
        {
          float height = (image.getRGB(i, j)) / COLOR_MAX ;
          height += 1.0f / 2.0f;
          height /= 1.0f / 2.0f;
          height *= 1.0f / 2.0f;

          this.heights[i][j] = height;
        }
      }
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
}