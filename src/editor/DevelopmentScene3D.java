package editor;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btStaticPlaneShape;
import editor.entity.PlayerSpawn;
import editor.entity.system.GuizmoSystem3D;
import editor.ui.*;
import engine.Console;
import engine.Engine;
import engine.asset.AssetFamily;
import engine.asset.load.*;
import engine.entity.Entity;
import engine.entity.EntityComponent;
import engine.entity.component.MaterialComponent;
import engine.entity.component.Transform;

import engine.entity.component.light.DirectionalLight;
import engine.entity.component.light.PointLight;
import engine.entity.system.light.DirectionalLightSystem;
import engine.entity.system.light.PointLightSystem;
import engine.entity.system.render.DeferredGeometryRenderer;
import engine.gfx.Texture2D;
import engine.gfx.buffer.FrameBuffer;
import engine.gfx.material.Material;
import engine.gfx.ShaderProgram;
import engine.physics.component.RigidBody;
import engine.physics.system.DebugRenderingSystem;
import engine.render.ForwardRenderer;
import engine.render.MaxViewport;
import engine.render.deferred.DeferredRenderer;
import engine.scene.Scene;
import engine.ui.DebugGUI;
import engine.entity.component.Camera3D;
import editor.entity.component.Camera3DMovement;
import engine.entity.component.Mesh;
import editor.entity.system.SimpleMeshRenderer;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import static engine.Engine.Log;
import static engine.Engine.*;

public class DevelopmentScene3D extends Scene
{
  private Entity selected;

  public void deselectEntity()
  {
    this.selected = null;
  }

  public void setSelectedEntity(Entity entity)
  {
    this.selected = entity;
  }

  public void setSelectedEntity(String entity)
  {
    this.selected = this.getEntity(entity);
  }

  public String getSelectedEntityID()
  {
    if (this.selected == null)
    {
      return null;
    }
    return this.selected.name();
  }

  public Entity getSelectedEntity()
  {
    return this.selected;
  }

  public void registerCommands()
  {
    // register convars
    Console.addConVar("editor_console", new Console.ConVar<>(true));
    Console.addConVar("editor_scenegraph", new Console.ConVar<>(false));
    Console.addConVar("editor_settings", new Console.ConVar<>(false));
    Console.addConVar("editor_collections", new Console.ConVar<>(false));
    Console.addConVar("editor_assets", new Console.ConVar<>(false));
    Console.addConVar("editor_convars", new Console.ConVar<>(true));
    Console.addConVar("component_editor", new Console.ConVar<>(true));
    Console.addConVar("editor_ui_warnings", new Console.ConVar<>(true));

    Console.addConVar("editor_camera_speed", new Console.ConVar<>(1.0f));
    Console.addConVar("editor_camera_sensitivity", new Console.ConVar<>(1.0f));

    // initialize the variables that we just set
    Console.exec("editor");
  }

  @Override
  public void onInit()
  {
    this.registerCommands();

    AssetManager.registerFamily(new AssetFamily<>(ShaderProgram.class));
    AssetManager.registerFactory(ShaderProgram.class, new ShaderFactory());

    AssetManager.registerFamily(new AssetFamily<>(Material.class));
    AssetManager.registerFactory(Material.class, new MaterialFactory());

    AssetManager.registerFamily(new AssetFamily<>(Texture2D.class));
    AssetManager.registerFactory(Texture2D.class, new TextureFactory());

    AssetManager.registerFamily(new AssetFamily<>(FrameBuffer.class));
    AssetManager.registerFactory(FrameBuffer.class, new FrameBufferFactory());

    AssetManager.load("phong_color.shd");
    AssetManager.load("colors_phong.mtl");
    //AssetManager.load("wood_textured.mtl");
    AssetManager.load("wood_phong.mtl");
    AssetManager.load("cube.mdl");

    AssetManager.load("gbuffer.ppl");

    Assimp.loadMeshes("cube.obj");
    Assimp.loadMeshes("lantern.obj");
    Assimp.loadMeshes("terrain.obj");
    Assimp.loadMeshes("sphere.obj");

    // init GUIS
    DebugGUI.initialize(Engine.Display.handle());

    // add dock base
    DebugGUI.add(new EditorBase());

    this.renderer = new DeferredRenderer();
    this.viewport = new MaxViewport(0, 0);

    this.addCollection(new SimpleMeshRenderer());
    this.addCollection(new GuizmoSystem3D());
    this.addCollection(new PointLightSystem());
    this.addCollection(new DirectionalLightSystem());

    this.addCollection(new DeferredGeometryRenderer());

    Entity sun = new Entity("sun")
      .addComponent(new DirectionalLight(
        new Vector3f(-0.25f, 0.25f, 0.0f),
        new Vector3f(1.0f)
      ));
    this.addEntity(sun);

    Entity camera = new Entity("editor-camera")
      .addComponent(new Transform(
        new Vector3f(),
        new Quaternionf().identity(),
        new Vector3f(1.0f)
      ).flag(EntityComponent.Flags.NO_REMOVE))
      .addComponent(
        new Camera3D()
        .flag(EntityComponent.Flags.NO_REMOVE)
      )
      .addComponent(new Camera3DMovement())
      .flag(Entity.Flags.NO_REMOVE)
      .flag(Entity.Flags.NO_PARENT_CHANGE);
    this.addEntity(camera);

    Entity lantern = new Entity("lantern")
      .addComponent(new Transform(
        new Vector3f(0.0f, 3.0f, 0.0f),
        new Quaternionf().identity(),
        new Vector3f(1.0f)
      ))
      .addComponent(new Mesh(
        "lantern.Cylinder",
        "lantern.Emissive_Opaque"
      ))
      .addComponent(new MaterialComponent(
        "white.plastic",
        "red.phong"
      ));

    lantern.addChild(new Entity("lantern.light")
      .addComponent(new Transform(
        new Vector3f(0.0f, 2.75f, 0.0f),
        new Quaternionf(),
        new Vector3f(1.0f)
      ))
      .addComponent(new PointLight(
        new Vector3f(1.0f, 1.0f, 1.0f),
        new Vector3f(1.0f, 0.0005f, 0.001f)
      ))
    );
    this.addEntity(lantern);

    Assimp.loadMeshes("terrain.obj");

    Entity world = new Entity("terrain")
      .addComponent(new Mesh(
        "terrain.terrain_1",
        "terrain.terrain_2"
      ))
      .addComponent(new MaterialComponent(
        "green.phong",
        "white.plastic"
      ))
      .addComponent(new Transform(
        new Vector3f(),
        new Quaternionf().identity(),
        new Vector3f(1.0f)
      ));
    world.addComponent(new RigidBody(
      new btStaticPlaneShape(new Vector3(0.0f, 1.0f, 0.0f), 3.0f),
      //Assimp.loadTriMesh("platform/resources/models/terrain.obj", 2),
      0.0f,
      world
    ));

    this.addEntity(world);

    this.addEntity(new PlayerSpawn());

    Entity bulletTest = new Entity()
      .addComponent(new Mesh("cube.Cube"))
      .addComponent(new Transform(
        new Vector3f(10.0f, 10.0f, 0.0f),
        new Quaternionf(),
        new Vector3f(1.0f)
      ))
      .addComponent(new MaterialComponent("red.phong"));
    bulletTest.addComponent(new RigidBody(new btBoxShape(new Vector3(1.0f, 1.0f, 1.0f)), 1.0f, bulletTest));

    this.addEntity(bulletTest);
  }

  @Override
  public void onEnter()
  {
    Log.info("enter");
    this.addCollection(new DebugRenderingSystem(this.physics, this));
  }

  @Override
  public void onExit()
  {
    Log.info("exit");
  }

  @Override
  public void onDispose() {}

  @Override
  public Scene onTick(float dt)
  {
    return this;
  }
}