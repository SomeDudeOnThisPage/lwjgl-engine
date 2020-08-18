package engine.gfx;

import engine.asset.Asset;
import org.joml.*;

public interface ShaderProgram extends Asset, Bindable
{
  boolean hasUniform(String... uniform);

  void setUniform(String uniform, boolean data);

  void setUniform(String uniform, int data);
  void setUniform(String uniform, Vector2ic data);
  void setUniform(String uniform, Vector3ic data);
  void setUniform(String uniform, Vector4ic data);

  void setUniform(String uniform, float data);
  void setUniform(String uniform, Vector2fc data);
  void setUniform(String uniform, Vector3fc data);
  void setUniform(String uniform, Vector4fc data);

  void setUniform(String uniform, Matrix2fc data);
  void setUniform(String uniform, Matrix3fc data);
  void setUniform(String uniform, Matrix4fc data);
}
