package engine.core;

import org.joml.Vector2d;

import static org.lwjgl.glfw.GLFW.*;

public class Input
{
  private static long window = -1;
  private static boolean dragging = false;

  private static Vector2d mouseLast = new Vector2d(0.0, 0.0);
  private static Vector2d mouseNow = new Vector2d(0.0, 0.0);

  private static Vector2d scroll = new Vector2d(0.0, 0.0);

  private static boolean pressed = false;

  private static boolean mouse = true;

  public static synchronized void initialize(Window windowObject)
  {
    window = windowObject.getWindow();

    // set callbacks
    glfwSetMouseButtonCallback(window, (window, button, action, mods) ->
    {
      if (Input.mouse)
      {
        if (button == GLFW_MOUSE_BUTTON_1)
        {
          if (action == GLFW_PRESS)
          {
            dragging = true;
          }
          else if (action == GLFW_RELEASE)
          {
            dragging = false;
          }
        }

        if (button == GLFW_MOUSE_BUTTON_2)
        {
          if (action == GLFW_PRESS)
          {
            pressed = true;
          }
          else if (action == GLFW_RELEASE)
          {
            pressed = false;
          }
        }
      }
    });

    glfwSetCursorPosCallback(window, (window, x, y) ->
    {
      mouseNow = new Vector2d(x, y);
    });

    glfwSetScrollCallback(window, (window, x, y) ->
    {
      scroll.add(new Vector2d(x, y));
    });
  }

  /**
   * Called from the render thread (as it holds the window context).
   */
  public static void update()
  {
    glfwPollEvents();
  }

  /**
   * Called from the update thread.
   */
  public static void reset()
  {
    mouseLast = mouseNow;
    scroll.x = 0.0;
    scroll.y = 0.0;

    Input.mouse = true;
  }

  public static void consumeMouse()
  {
    Input.mouse = false;
  }

  public static Vector2d getMovement()
  {
    if (Input.mouse)
    {
      return new Vector2d(mouseNow.x - mouseLast.x, mouseNow.y - mouseLast.y);
    }

    return new Vector2d(0.0, 0.0);
  }

  /**
   * Returns the difference on the x- and y-axis from the last frame.
   * @return 2-Component vector. The first component is the difference on the x-axis, the second component is the position is the difference on the y-axis
   */
  public static Vector2d getDrag()
  {
    if (dragging && Input.mouse)
    {
      return new Vector2d(mouseNow.x - mouseLast.x, mouseNow.y - mouseLast.y);
    }

    return new Vector2d(0.0, 0.0);
  }

  public static Vector2d getScroll()
  {
    if (Input.mouse)
    {
      return scroll;
    }
    return new Vector2d(0.0, 0.0);
  }

  public static boolean keyDown(int key)
  {
    if (window == -1)
    {
      System.err.println("Input module was not initialized.");
      return false;
    }

    return glfwGetKey(window, key) == 1;
  }

  public static boolean pressed()
  {
    return pressed;
  }
}
