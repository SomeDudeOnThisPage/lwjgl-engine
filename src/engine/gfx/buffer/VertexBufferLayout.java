package engine.gfx.buffer;

import engine.gfx.ShaderAttribute;

import java.util.ArrayList;
import java.util.Arrays;

// implementation derived from https://www.youtube.com/watch?v=jIJFM_pi6gQ
public class VertexBufferLayout
{
  public static final class BufferElement
  {
    private final String          name;
    private final VertexDataType  type;
    private final int location;
    private int       offset;

    private int retrieveLocation()
    {
      for (ShaderAttribute attribute : ShaderAttribute.values())
      {
        if (attribute.getName().equals(this.name))
        {
          return attribute.getLocation();
        }
      }
      throw new RuntimeException("failed to query shader attribute location for attribute name '" + this.name + "'");
    }

    public int location()
    {
      return this.location;
    }

    public void offset(int offset)
    {
      this.offset = offset;
    }

    public int offset()
    {
      //
      return this.offset;
    }

    public VertexDataType type()
    {
      //
      return this.type;
    }

    public BufferElement(String name, VertexDataType type)
    {
      this.name = name;
      this.type = type;

      this.location = this.retrieveLocation();
    }
  }

  private final ArrayList<BufferElement> elements;
  private int stride;

  private void offset()
  {
    this.stride = 0;
    int offset = 0;
    for (BufferElement element : this.elements)
    {
      element.offset(offset);
      offset += element.type().size();
      this.stride += element.type().size();
    }
  }

  public int stride()
  {
    return this.stride;
  }

  public ArrayList<BufferElement> elements()
  {
    return this.elements;
  }

  public void addElement(BufferElement element)
  {
    this.elements.add(element);
    this.offset();
  }

  public VertexBufferLayout(BufferElement ... elements)
  {
    this.elements = new ArrayList<>();
    this.elements.addAll(Arrays.asList(elements));

    // calculate offsets / stride
    this.offset();
  }
}
