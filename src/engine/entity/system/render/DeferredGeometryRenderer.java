package engine.entity.system.render;

import engine.asset.AssetManager;
import engine.entity.Entity;
import engine.entity.EntityCollection;
import engine.entity.EntityComponent;
import engine.entity.RenderSystem;
import engine.entity.component.MaterialComponent;
import engine.entity.component.Transform;
import engine.gfx.ShaderProgram;
import engine.gfx.material.Material;
import engine.gfx.opengl.shader.GLShaderProgram;
import engine.render.RenderStage;
import engine.render.deferred.DeferredRenderer;

public class DeferredGeometryRenderer extends EntityCollection implements RenderSystem<DeferredRenderer>
{
  private ShaderProgram geometry;

  @Override
  public <T extends EntityComponent> Class<T>[] components()
  {
    return new Class[]
    {
      Transform.class,
      MaterialComponent.class
    };
  }

  @Override
  public RenderStage stage()
  {
    return RenderStage.DEFERRED_GEOMETRY_PASS;
  }

  @Override
  public void render(DeferredRenderer pipeline)
  {
    // bind geometry shader
    this.geometry.bind();

    for (Entity entity : this.entities)
    {
      for (String index : entity.getComponent(MaterialComponent.class).material)
      {
        Material material = AssetManager.getInstance().request(index, Material.class);

        material.bind();

        //AssetManager.getInstance().release(material);
      }
    }

    this.geometry.unbind();
  }

  public DeferredGeometryRenderer()
  {
    // load shader manually in the asset manager
    // todo: the source of this shader should probably be on the classpath, as it shouldn't be altered ever
    ShaderProgram shader = new GLShaderProgram("deferred.phong_color_geometry.vs", "deferred.phong_color_geometry.fs");
    AssetManager.getInstance().load(shader, "deferred.geometry.phong.color", ShaderProgram.class);
    this.geometry = AssetManager.getInstance().request("deferred.geometry.phong.color", ShaderProgram.class);
  }
}
