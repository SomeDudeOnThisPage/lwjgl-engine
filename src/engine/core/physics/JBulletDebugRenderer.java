package engine.core.physics;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import engine.Engine;
import engine.core.entity.component.ProjectionComponent;
import engine.core.rendering.Camera;
import engine.core.scene.SceneGraph;
import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL11.*;

public class JBulletDebugRenderer extends btIDebugDraw
{
  private int mode = 1;

  private Matrix4f m_projection = new Matrix4f();

  public void begin()
  {
    glDisable(GL_DEPTH_TEST);
    glEnable(GL_POINT_SMOOTH);

    glPointSize(2.0f);

    glMatrixMode(GL_PROJECTION);
    this.m_projection.identity();

    Camera camera = Engine.scene_manager.getScene().getPlayer().getCamera();

    camera.get(ProjectionComponent.class).projection.get(this.m_projection);
    this.m_projection.mul(SceneGraph.constructTransform(camera).invert());

    glLoadMatrixf(this.m_projection.get(new float[16]));
  }

  public void end()
  {
    glEnable(GL_DEPTH_TEST);
  }

  @Override
  public void drawLine(Vector3 vector3f, Vector3 vector3f1, Vector3 vector3f2)
  {
    glColor3f(vector3f2.x, vector3f2.y, vector3f2.z);
    glBegin(GL_LINES);
      glVertex3f(vector3f.x, vector3f.y, vector3f.z);
      glVertex3f(vector3f1.x, vector3f1.y, vector3f1.z);
    glEnd();
  }

  @Override
  public void drawContactPoint(Vector3 vector3f, Vector3 vector3f1, float v, int i, Vector3 vector3f2)
  {
    glColor3f(vector3f1.x, vector3f1.y, vector3f1.z);
    glBegin(GL_POINTS);
      glVertex3f(vector3f.x, vector3f1.y, vector3f.z);
      //glVertex3f(vector3f1.x, vector3f1.y, vector3f1.z);
    glEnd();
  }

  @Override
  public void reportErrorWarning(String s)
  {
    System.err.println(s);
  }

  @Override
  public void draw3dText(Vector3 vector3f, String s)
  {

  }

  @Override
  public void setDebugMode(int i)
  {
    this.mode = i;
  }

  @Override
  public int getDebugMode()
  {
    return this.mode;
  }
}
