package engine.physics;

import com.badlogic.gdx.physics.bullet.collision.btBvhTriangleMeshShape;
import engine.asset.load.AssetFactory;
import engine.asset.load.Assimp;
import engine.util.XMLUtil;
import org.w3c.dom.Element;

public class CollisionShapeFactory implements AssetFactory<CollisionShape>
{
  @Override
  public String tag()
  {
    return "cshape";
  }

  @Override
  public CollisionShape load(Element xml) throws AssetLoadingException
  {
    Element source = XMLUtil.element(xml, "source");
    int meshes = XMLUtil.int1(XMLUtil.element(xml, "meshes").getTextContent());
    btBvhTriangleMeshShape shape = Assimp.loadTriMesh(source.getTextContent(), meshes);

    return new CollisionShape(shape);
  }
}
