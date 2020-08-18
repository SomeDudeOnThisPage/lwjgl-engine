package editor.ui;

import editor.DevelopmentScene3D;
import engine.entity.component.Mesh;
import engine.Console;
import engine.Engine;
import engine.entity.*;
import engine.entity.component.Transform;
import engine.scene.Scene;
import engine.ui.DebugGUI;
import imgui.*;
import imgui.enums.*;
import org.joml.Vector2i;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class EntityList extends DebugGUI
{
  private boolean addingEntity = false;
  private boolean addingComponent = false;

  private final ImString addingEntityName = new ImString();

  private final ImInt selectedComponentID = new ImInt(0);

  private void treeNodeSelectable(Entity e, ImBool s, Scene scene)
  {
    if (e.flagged(Entity.Flags.NO_REMOVE))
    {
      ImGui.pushStyleColor(ImGuiCol.Text, 1.0f, 0.0f, 0.0f, 1.0f);
    }

    ImGui.selectable("[" + e.name() + "]###selectable" + e.id(), s);
    if (!e.flagged(Entity.Flags.NO_PARENT_CHANGE) && ImGui.beginDragDropSource())
    {
      ImGui.setDragDropPayload("SCENE_GRAPH_SOURCE", e.name().getBytes(), e.name().getBytes().length);
      ImGui.endDragDropSource();
    }

    if (ImGui.beginDragDropTarget())
    {
      // re-parent entities in scene graph
      byte[] payload = ImGui.acceptDragDropPayload("SCENE_GRAPH_SOURCE");
      if (payload != null)
      {
        String name = new String(payload);
        Entity child = scene.getEntity(name);
        e.addChild(child);
      }
      ImGui.endDragDropTarget();
    }

    if (s.get() && !this.addingComponent)
    {
      ((DevelopmentScene3D) scene).setSelectedEntity(e);
    }

    if (e.flagged(Entity.Flags.NO_REMOVE))
    {
      ImGui.popStyleColor();
    }
  }

  private void tree(Entity parent, Scene scene)
  {
    String selected = ((DevelopmentScene3D) scene).getSelectedEntityID();
    if (selected == null) { selected = ""; }

    List<Entity> children = parent.children();
    final ImBool s = new ImBool();
    if (children.size() > 0)
    {
      if (ImGui.treeNodeEx("###treenode" + parent.id(), ImGuiTreeNodeFlags.DefaultOpen))
      {
        if (selected.equals(parent.name()))
        {
          s.set(true);
        }

        if (ImGui.beginDragDropSource(ImGuiDragDropFlags.SourceNoDisableHover))
        {
          ImGui.endDragDropSource();
        }

        if (ImGui.beginDragDropTarget())
        {
          ImGui.endDragDropTarget();
        }

        ImGui.sameLine();
        this.treeNodeSelectable(parent, s, scene);

        for (Entity child : children)
        {
          tree(child, scene);
        }
        ImGui.treePop();
      }
      else
      {
        ImGui.sameLine();
        this.treeNodeSelectable(parent, s, scene);
        //ImGui.treePop();
      }
    }
    else
    {
      if (selected.equals(parent.name()))
      {
        s.set(true);
      }

      ImGui.bullet();
      this.treeNodeSelectable(parent, s, scene);
    }
  }

  private final ImInt selectedAddComponent = new ImInt(0);
  private void showAddComponentWindow(Entity entity)
  {
    Vector2i size = Engine.Display.size();
    ImGui.setNextWindowSize(350, 500);
    ImGui.setNextWindowPos(size.x / 2.0f - 175, size.y / 2.0f - 250);
    ImGui.begin("Add Component",
          ImGuiWindowFlags.NoDocking
        | ImGuiWindowFlags.NoMove
        | ImGuiWindowFlags.NoResize
    );

    Collection<URL> urls = ClasspathHelper.forPackage("engine");

    Reflections reflections = new Reflections(new ConfigurationBuilder()
      .setUrls(urls)
      .setScanners(
        new SubTypesScanner(),
        new TypeAnnotationsScanner())
     .filterInputsBy(new FilterBuilder().includePackage("engine").includePackage("editor")));

    Set<Class<?>> classes = reflections.getTypesAnnotatedWith(EditorComponent.class);

    HashMap<String, Class<?>> map = new HashMap<>();

    String[] components = new String[classes.size()];
    int i = 0;
    for (Class<?> c : classes)
    {
      components[i++] = c.getSimpleName();
      map.put(c.getSimpleName(), c);
    }

    ImGui.setNextItemWidth(-0.001f);
    ImGui.listBox("", selectedAddComponent, components, components.length);

    Class<?> c = map.get(components[selectedAddComponent.get()]);
    Constructor<?> constructor = null;

    // get editor constructor
    for (final Constructor<?> cc : c.getDeclaredConstructors())
    {
      if (cc.isAnnotationPresent(EditorConstructor.class))
      {
        constructor = cc;
      }
    }

    if (constructor != null)
    {
      // create constructor parameter inputs
      Parameter[] parameters = constructor.getParameters();
      for (Parameter p : parameters)
      {

      }

      if (ImGui.button("Confirm"))
      {
        this.addingComponent = false;
        try
        {
          EntityComponent add = (EntityComponent) constructor.newInstance();
          entity.addComponent(add);
        }
        catch (InstantiationException | IllegalAccessException | InvocationTargetException e)
        {
          Console.error("failed to instantiate component");
          e.printStackTrace();
        }
      }

    }
    ImGui.end();
  }

  public void addEntity(Scene scene, Entity entity)
  {
    if (this.addingEntity)
    {
      ImGui.setNextWindowSize(250, 100);
      Vector2i size = Engine.Display.size();
      ImGui.setNextWindowPos(size.x / 2.0f - 125, size.y / 2.0f - 50);
      if (ImGui.begin("Add Entity", ImGuiWindowFlags.NoDocking | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoResize))
      {
        ImGui.checkbox("Transform", initTransform);
        ImGui.checkbox("Mesh", initMesh);

        if (ImGui.inputText("name", this.addingEntityName, ImGuiInputTextFlags.EnterReturnsTrue))
        {
          this.addingEntity = false;
          String name = this.addingEntityName.get();
          if (scene.getEntity(name) == null)
          {
            Entity n = new Entity(name);
            if (initTransform.get()) { n.addComponent(new Transform()); }
            if (initMesh.get())
            {
              Mesh mesh = new Mesh("cube.Cube");
              n.addComponent(mesh);
            }

            if (entity != null)
            {
              scene.addEntity(n, entity);
            }
            else
            {
              scene.addEntity(n);
            }
          }
          this.addingEntityName.set("");
        }
        ImGui.end();
      }
    }
  }

  private final ImBool initTransform = new ImBool();
  private final ImBool initMesh = new ImBool();

  public void render(Scene scene)
  {
    ImGui.setNextWindowSize(350, scene.viewport().size().y, ImGuiCond.Once);
    ImGui.setNextWindowPos(0, 20, ImGuiCond.Once);

    if ((Boolean) Console.getConVar("editor_scenegraph").get())
    {
      if (ImGui.begin("Scene Graph"))
      {
        Entity entity = ((DevelopmentScene3D) scene).getSelectedEntity();

        // for some reason, enclosing beginChildFrame in an if-block, creates a failed assertion when the window
        // is minimized - don't fucking ask why but don't put beginChildFrame in an if-block!
        /* if (*/ ImGui.beginChildFrame(ImGui.getID("Scene Graph###entity_tree"), -0.0001f, -40f); //) {
          Entity root = scene.root();
          tree(root, scene);
          ImGui.newLine();
        ImGui.endChildFrame(); // }

        if (ImGui.button("Add##Entity") && !this.addingEntity && !this.addingComponent)
        {
          this.addingEntity = true;
        }

        this.addEntity(scene, entity);

        ImGui.sameLine();
        if (ImGui.button("Remove##Entity") && entity != null && !this.addingComponent)
        {
          if (entity.flagged(Entity.Flags.NO_REMOVE))
          {
            Console.error("cannot remove entity flagged with 'NO_REMOVE'");
          }
          else
          {
            scene.ecs().removeEntity(((DevelopmentScene3D) scene).getSelectedEntity());
            ((DevelopmentScene3D) scene).deselectEntity();
            this.selectedComponentID.set(-1);
          }
        }
        ImGui.end();
      }
    }
  }
}
