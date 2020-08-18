package soundwav;

import editor.entity.component.Camera2D;
import editor.ui.EditorBase;
import editor.ui.EditorConsole;
import engine.Console;
import engine.Engine;
import engine.asset.AssetFamily;
import engine.asset.load.FrameBufferFactory;
import engine.asset.load.MaterialFactory;
import engine.asset.load.ShaderFactory;
import engine.asset.load.TextureFactory;
import engine.entity.Camera;
import engine.entity.Entity;
import engine.gfx.ShaderProgram;
import engine.gfx.Texture2D;
import engine.gfx.buffer.FrameBuffer;
import engine.gfx.material.Material;
import engine.render.ForwardRenderer;
import engine.render.MaxViewport;
import engine.scene.Scene;
import engine.ui.DebugGUI;
import soundwav.audio.AudioFactory;
import soundwav.audio.OpenAL;
import soundwav.audio.SoundBuffer;
import soundwav.audio.component.SoundSource;
import soundwav.audio.system.AudioPlayerSystem;
import soundwav.entity.SceneCamera2D;
import soundwav.entity.component.Camera2DMovement;
import soundwav.ui.AudioPlayer;

import static engine.Engine.AssetManager;
import static org.lwjgl.openal.AL11.*;

public class SoundWav extends Scene
{
  int soundSource;
  AudioPlayer player;

  @Override
  public void onInit()
  {
    Console.addConVar("editor_console", new Console.ConVar<>(true));

    AssetManager.registerFamily(new AssetFamily<>(ShaderProgram.class));
    AssetManager.registerFactory(ShaderProgram.class, new ShaderFactory());

    AssetManager.registerFamily(new AssetFamily<>(Material.class));
    AssetManager.registerFactory(Material.class, new MaterialFactory());

    AssetManager.registerFamily(new AssetFamily<>(Texture2D.class));
    AssetManager.registerFactory(Texture2D.class, new TextureFactory());

    AssetManager.registerFamily(new AssetFamily<>(FrameBuffer.class));
    AssetManager.registerFactory(FrameBuffer.class, new FrameBufferFactory());

    AssetManager.registerFamily(new AssetFamily<>(SoundBuffer.class));
    AssetManager.registerFactory(SoundBuffer.class, new AudioFactory());

    DebugGUI.initialize(Engine.Display.handle());
    DebugGUI.add(new EditorBase());

    this.renderer = new ForwardRenderer();
    this.viewport = new MaxViewport(0, 0);

    Camera camera = new SceneCamera2D();
    camera.addComponent(new Camera2DMovement());
    this.addEntity(camera);

    this.renderer.camera(camera);

    //this.player = new AudioPlayer();
    //DebugGUI.add(this.player);
    DebugGUI.add(new EditorConsole());

    OpenAL.initialize();

    // add player system
    this.addCollection(new AudioPlayerSystem());

    // load audio asset
    AssetManager.load("handinhand.audio");

    // create entity with sound source
    Entity handinhand = new Entity("handinhand")
      .addComponent(new SoundSource("handinhand"));
    this.addEntity(handinhand);

    Entity yeet = new Entity("yeet")
      .addComponent(new SoundSource("diehorderennt"));
    this.addEntity(yeet);

    // set sound source info
    /*music.getComponent(SoundSource.class).source.loop(true);
    music.getComponent(SoundSource.class).source.gain(0.1f);

    // play sound
    music.getComponent(SoundSource.class).source.play();*/
  }

  @Override
  public void onEnter() {}

  @Override
  public void onExit() {}

  @Override
  public void onDispose()
  {
    OpenAL.destroy();
  }

  @Override
  public Scene onTick(float dt)
  {
    // alSourcef(soundSource, AL_GAIN, AudioPlayer.volume);
    // player.setOffset((int) Math.rint(alGetSourcef(soundSource, AL_SAMPLE_OFFSET)));
    return this;
  }
}
