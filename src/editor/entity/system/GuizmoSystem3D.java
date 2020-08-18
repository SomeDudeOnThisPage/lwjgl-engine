package editor.entity.system;

import editor.DevelopmentScene3D;
import engine.entity.*;
import engine.entity.component.Transform;
import engine.platform.input.Input;
import engine.scene.Scene;
import engine.scene.SceneGraph;
import engine.util.MathUtil;
import engine.util.Vector3;
import imgui.ImDrawList;
import imgui.ImGui;
import org.joml.*;

import java.awt.*;

public class GuizmoSystem3D extends EntityCollection implements EntitySystem
{
  /**
   * Data struct containing info for a guizmo angle.
   * Three of these are used to create the translation guizmo.
   */
  private static final class GuizmoAxis3D
  {
    public final Vector3f offset;
    public Vector2f p1;
    public final int color;
    public boolean dragging;

    public GuizmoAxis3D(Vector3f offset, Vector4f color)
    {
      this.offset = offset;
      this.color = new Color(color.z, color.y, color.x, color.w).getRGB() & 0x80FFFFFF;
      this.p1 = new Vector2f();
      this.dragging = false;
    }
  }

  private final Vector3<GuizmoAxis3D> axis;
  private final Vector3<Boolean> dragging;

  private boolean getAxisDrag()
  {
    return this.dragging.x || this.dragging.y || this.dragging.z;
  }

  private boolean getAxisDrag(boolean x, boolean y, boolean z)
  {
    return x == this.dragging.x && y == this.dragging.y && z == this.dragging.z;
  }

  private void setAxisDrag(boolean x, boolean y, boolean z)
  {
    this.dragging.x = x;
    this.dragging.y = y;
    this.dragging.z = z;
  }

  @Override
  public <T extends EntityComponent> Class<T>[] components()
  {
    return new Class[]
    {
      Transform.class
    };
  }

  @Override
  public EntityComponentSystem.SystemPriority priority()
  {
    return EntityComponentSystem.SystemPriority.LEVEL_4;
  }

  private Entity getSelectedEntity(Scene scene)
  {
    if (scene instanceof DevelopmentScene3D)
    {
      return ((DevelopmentScene3D) scene).getSelectedEntity();
    }
    return null;
  }

  private void setSelectedEntity(Scene scene, Entity entity)
  {
    if (scene instanceof DevelopmentScene3D)
    {
      ((DevelopmentScene3D) scene).setSelectedEntity(entity);
    }
  }

  @Override
  public void update(Scene scene)
  {
    Entity camera = scene.getEntity("editor-camera");

    if (Input.pressed() && !Input.isDragging())
    {
      Vector3f dir = MathUtil.windowToWorld(scene, camera, Input.getPosition(), -1.0f);
      dir.normalize();
      this.setSelectedEntity(scene, MathUtil.raycast(dir, camera.getComponent(Transform.class).position, this.entities));
    }

    Entity selected = this.getSelectedEntity(scene);
    if (selected == null || !selected.containsComponent(Transform.class)) { return; }

    if (!selected.flagged(Entity.Flags.NO_GUIZMO))
    {
      Matrix4f transform = SceneGraph.transform(selected);
      Vector3f pos3f = transform.getTranslation(new Vector3f());
      Vector2f window = MathUtil.worldToWindow(scene, camera, pos3f);

      if (MathUtil.bounded(scene, window))
      {
        Vector2f p0 = MathUtil.worldToWindow(scene, camera, pos3f);

        // update axis points
        this.axis.x.p1 = MathUtil.worldToWindow(scene, camera, new Vector3f(pos3f).add(1.25f, 0.0f, 0.0f));
        this.axis.y.p1 = MathUtil.worldToWindow(scene, camera, new Vector3f(pos3f).add(0.0f, 1.25f, 0.0f));
        this.axis.z.p1 = MathUtil.worldToWindow(scene, camera, new Vector3f(pos3f).add(0.0f, 0.0f, 1.25f));

        Vector2f p1x = MathUtil.worldToWindow(scene, camera, new Vector3f(pos3f).add(this.axis.x.offset));
        Vector2f p1y = MathUtil.worldToWindow(scene, camera, new Vector3f(pos3f).add(this.axis.y.offset));
        Vector2f p1z = MathUtil.worldToWindow(scene, camera, new Vector3f(pos3f).add(this.axis.z.offset));

        ImDrawList list = ImGui.getBackgroundDrawList();

        Vector2f mouse = Input.getPosition();
        Vector2f mouseScreen = Input.getPosition();
        Vector2f mouseLast = new Vector2f((float) Input.getMouseLast().x, (float) Input.getMouseLast().y);

        float z = SceneGraph.transform(selected).getTranslation(new Vector3f()).z;

        Vector3f mousePositionWorld = MathUtil.windowToWorld(scene, camera, mouseScreen, z);
        Vector3f mouseLastPositionWorld = MathUtil.windowToWorld(scene, camera, mouseLast, z);

        Vector3f movement = mouseLastPositionWorld.sub(mousePositionWorld);
        float speed = mousePositionWorld.distance(mouseLastPositionWorld) * 10.0f;

        if (this.getAxisDrag(true, false, false) || (!this.getAxisDrag() && MathUtil.onLine(mouse, p0, p1x, 5.0f)))
        {
          this.setAxisDrag(true, false, false);
          list.addLine(p0.x, p0.y, p1x.x, p1x.y, this.axis.x.color | 0xFF000000, 5.0f);
        }
        else
        {
          list.addLine(p0.x, p0.y, p1x.x, p1x.y, this.axis.x.color, 5.0f);
        }

        if (this.getAxisDrag(false, true, false) || (!this.getAxisDrag() && MathUtil.onLine(mouse, p0, p1y, 5.0f)))
        {
          this.setAxisDrag(false, true, false);

          list.addLine(p0.x, p0.y, p1y.x, p1y.y, this.axis.y.color | 0xFF000000, 5.0f);
        }
        else
        {
          list.addLine(p0.x, p0.y, p1y.x, p1y.y, this.axis.y.color, 5.0f);
        }

        if (this.getAxisDrag(false, false, true) || (!this.getAxisDrag() && MathUtil.onLine(mouse, p0, p1z, 5.0f)))
        {
          this.setAxisDrag(false, false, true);
          list.addLine(p0.x, p0.y, p1z.x, p1z.y, this.axis.z.color | 0xFF000000, 5.0f);
        }
        else
        {
          list.addLine(p0.x, p0.y, p1z.x, p1z.y, this.axis.z.color, 5.0f);
        }
        list.addText(ImGui.getFont(), 24, p1x.x - 4.0f, p1x.y - 12.0f, 0xFFFFFFFF, "X");
        list.addText(ImGui.getFont(), 24, p1y.x - 4.0f, p1y.y - 12.0f, 0xFFFFFFFF, "Y");
        list.addText(ImGui.getFont(), 24, p1z.x - 4.0f, p1z.y - 12.0f, 0xFFFFFFFF, "Z");

        if (Input.isDragging())
        {
          Vector3f translation = new Vector3f();
          if (this.dragging.x)
          {
            translation.add(movement.x * speed, 0.0f, 0.0f);
          }

          if (this.dragging.y)
          {
            translation.add(0.0f, movement.y * speed, 0.0f);
          }

          if (this.dragging.z)
          {
            translation.add(0.0f, 0.0f, movement.z * speed);
          }

          selected.getComponent(Transform.class).position.add(translation);
        }

        if (Input.isDragging() && (this.getAxisDrag()))
        {
          Input.consumeMouse();
        }
        else
        {
          this.setAxisDrag(false, false, false);
        }
      }
    }
  }

  public GuizmoSystem3D()
  {
    this.axis = new Vector3<>(
      new GuizmoAxis3D(new Vector3f(1.0f, 0.0f, 0.0f), new Vector4f(1.0f, 0.0f, 0.0f, 1.0f)),
      new GuizmoAxis3D(new Vector3f(0.0f, 1.0f, 0.0f), new Vector4f(0.0f, 1.0f, 0.0f, 1.0f)),
      new GuizmoAxis3D(new Vector3f(0.0f, 0.0f, 1.0f), new Vector4f(0.0f, 0.0f, 1.0f, 1.0f))
    );

    this.dragging = new Vector3<>(false, false, false);
  }
}
