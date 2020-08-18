package editor.ui;

import engine.entity.Camera;
import engine.entity.component.ProjectionComponent;
import engine.scene.Scene;
import engine.scene.SceneGraph;
import engine.ui.DebugGUI;
import imgui.ImGui;
import imgui.enums.ImGuiCond;
import imgui.enums.ImGuiWindowFlags;
import org.joml.*;

public abstract class Window3D extends DebugGUI
{
  private final String name;
  private final Camera camera;
  private final Vector3f position;
  private final Vector2i size;

  public abstract void content();

  @Override
  public final void render(Scene scene)
  {
    Matrix4f projection = this.camera.getComponent(ProjectionComponent.class).construct(scene.viewport());
    Matrix4f view = SceneGraph.transform(this.camera);
    Vector4f position = new Vector4f(
      this.position.x,
      this.position.y,
      this.position.z,
      1.0f
    );

    position.mul(projection.mul(view));
    Vector3f ndc = new Vector3f(position.x, position.y, position.z).div(position.w);
    Vector2f window = new Vector2f(ndc.x, ndc.y);
    window.x += 1.0f;
    window.x /= 2.0f;
    window.y = 1 - window.y;
    window.y /= 2.0f;

    window.x *= scene.viewport().size().x;
    window.y *= scene.viewport().size().y;

    ImGui.setNextWindowPos(window.x - 50, window.y - 50, ImGuiCond.Always);
    ImGui.setNextWindowSize(this.size.x, this.size.y);
    ImGui.begin(this.name, ImGuiWindowFlags.NoMove
      | ImGuiWindowFlags.NoBringToFrontOnFocus
      | ImGuiWindowFlags.NoNavFocus
      | ImGuiWindowFlags.NoNav
      | ImGuiWindowFlags.NoInputs
      | ImGuiWindowFlags.NoResize
      | ImGuiWindowFlags.NoTitleBar
      | ImGuiWindowFlags.NoFocusOnAppearing
      | ImGuiWindowFlags.NoDecoration
      | ImGuiWindowFlags.NoNavInputs
    );

    this.content();

    ImGui.end();
  }

  public Window3D(String name, Vector2i size, Vector3f root, Camera camera)
  {
    this.name = name;
    this.size = size;
    this.position = root;
    this.camera = camera;
  }
}
