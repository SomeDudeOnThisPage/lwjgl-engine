package engine.gfx.uniform;

import java.util.ArrayList;

public class BufferedUniformArray<T> extends BufferedUniform<T>
{
  private final ArrayList<BufferedUniform<?>> uniforms;

  public ArrayList<BufferedUniform<?>> getUniforms()
  {
    return this.uniforms;
  }

  @Override
  public T get()
  {
    throw new UnsupportedOperationException("cannot get data of BufferedUniformStruct - get the data from the member" +
      "uniforms instead");
  }

  @Override
  public void set(T data)
  {
    throw new UnsupportedOperationException("cannot set data of BufferedUniformStruct - set the data of the member" +
      "uniforms instead");
  }

  public BufferedUniformArray(String uniform, int num, int alignment0, ArrayList<BufferedUniform<?>> uniforms)
  {
    this.uniform = uniform;
    this.uniforms = new ArrayList<>();

    this.alignment = num * alignment0; // total size (max number of elements * alignment per element)

    for (BufferedUniform<?> u : uniforms)
    {
      u.name(""); // clear the name to match the notation
      this.uniforms.add(u);
    }
  }
}
