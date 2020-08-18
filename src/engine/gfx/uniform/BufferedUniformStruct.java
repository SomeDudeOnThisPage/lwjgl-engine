package engine.gfx.uniform;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class BufferedUniformStruct<T> extends BufferedUniform<T>
{
  /** String identifiers mapped to BufferedUniform instances. */
  private final HashMap<String, BufferedUniform<?>> uniforms;

  public HashMap<String, BufferedUniform<?>> getUniforms()
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

  public BufferedUniformStruct(String name, BufferedUniform<?>... uniforms)
  {
    this.uniform = name;
    this.uniforms = new LinkedHashMap<>(); // not ideal but for this implementation we need to keep insertion order

    // alignment is calculated from parts, not from N
    long offset = 0;
    for (BufferedUniform<?> uniform : uniforms)
    {
      // System.out.println(uniform.name());
      this.uniforms.put(uniform.name(), uniform);
      offset += uniform.alignment();
    }

    this.alignment = (int) offset;
  }
}
