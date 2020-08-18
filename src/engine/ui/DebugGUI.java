package engine.ui;

import engine.Engine;
import engine.platform.input.Input;
import engine.render.Viewport;
import engine.scene.Scene;
import engine.util.Memory;
import imgui.*;
import imgui.callbacks.ImStrConsumer;
import imgui.callbacks.ImStrSupplier;
import imgui.enums.*;
import imgui.gl3.ImGuiImplGl3;
import org.joml.Vector2f;
import org.joml.Vector2i;

import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11C.glViewport;

public abstract class DebugGUI
{
  public static final ConcurrentHashMap<Class<?>, DebugGUI> guis = new ConcurrentHashMap<>();

  public static final long[] cursors = new long[ImGuiMouseCursor.COUNT];

  public static final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();

  public static ImGuiImplGl3 ui()
  {
    return imGuiGl3;
  }

  public static void add(DebugGUI gui)
  {
    if (!DebugGUI.guis.containsKey(gui.getClass()))
    {
      DebugGUI.guis.put(gui.getClass(), gui);
    }
  }

  public static void renderGUIS(Scene scene)
  {
    for (DebugGUI gui : DebugGUI.guis.values())
    {
      gui.render(scene);
    }
  }

  public abstract void render(Scene scene);

  public static void preFrame(float dt, Viewport viewport)
  {
    final ImGuiIO io = ImGui.getIO();
    //io.setConfigDockingAlwaysTabBar(true);
    io.setConfigDockingWithShift(true);
    io.setConfigWindowsMoveFromTitleBarOnly(true);

    int[] winWidth = new int[1];
    int[] winHeight = new int[1];
    int[] fbWidth = new int[1];
    int[] fbHeight = new int[1];

    glfwGetWindowSize(Engine.Display.handle(), winWidth, winHeight);
    glfwGetFramebufferSize(Engine.Display.handle(), fbWidth, fbHeight);

    final float scaleX = (float) fbWidth[0] / winWidth[0];
    final float scaleY = (float) fbHeight[0] / winHeight[0];

    io.setDisplaySize(winWidth[0], winHeight[0]);
    io.setDisplayFramebufferScale(scaleX, scaleY);

    Vector2f position = Input.getPosition();

    io.setMousePos(position.x * scaleX, position.y * scaleY);
    io.setDeltaTime(dt);

    // Update the mouse cursor
    final int imguiCursor = ImGui.getMouseCursor();
    glfwSetCursor(Engine.Display.handle(), DebugGUI.cursors[imguiCursor]);
    glfwSetInputMode(Engine.Display.handle(), GLFW_CURSOR, GLFW_CURSOR_NORMAL);

    ImGui.newFrame();
    glViewport(viewport.position().x, viewport.position().y, viewport.size().x, viewport.size().y);
    DebugGUI.base();
  }

  public static int DOCKSPACE = 0;

  public static void base()
  {
    ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0.0f);
    ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0.0f);
    ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0.0f, 0.0f);

    Vector2i size = Engine.Display.size();
    ImGui.setNextWindowSize(size.x, size.y, ImGuiCond.Always);
    ImGui.setNextWindowPos(0, 0, ImGuiCond.Once);
    ImGui.setNextWindowBgAlpha(1.0f);

    ImGui.begin("Engine",
      ImGuiWindowFlags.NoCollapse
        | ImGuiWindowFlags.MenuBar
        | ImGuiWindowFlags.NoResize
        | ImGuiWindowFlags.NoMove
        | ImGuiWindowFlags.NoTitleBar
        | ImGuiWindowFlags.NoDocking
        | ImGuiWindowFlags.NoBringToFrontOnFocus
        | ImGuiWindowFlags.NoBackground
    );
    ImGui.popStyleVar(3);

    DOCKSPACE = ImGui.getID("BASE");
    ImGui.dockSpace(DOCKSPACE, 0.0f, 0.0f, ImGuiDockNodeFlags.PassthruCentralNode);
    ImGui.end();
  }

  public static void postFrame()
  {
    ImGui.render();
    ImGui.endFrame();
    DebugGUI.ui().render(ImGui.getDrawData());
  }

  public static void dispose()
  {
    ImGui.destroyContext();
  }

  public static void initialize(long display)
  {
    // IMPORTANT!!
    // This line is critical for Dear ImGui to work.
    ImGui.createContext();
    //ImGui.styleColorsLight();

    // ------------------------------------------------------------
    // Initialize ImGuiIO config
    final ImGuiIO io = ImGui.getIO();

    io.setIniFilename(null); // We don't want to save .ini file
    io.setConfigFlags(ImGuiConfigFlags.NavEnableKeyboard | ImGuiConfigFlags.DockingEnable); // Navigation with keyboard and enabled docking
    io.setBackendFlags(ImGuiBackendFlags.HasMouseCursors); // Mouse cursors to display while resizing windows etc.
    io.setBackendPlatformName("imgui_java_impl_glfw");

    // ------------------------------------------------------------
    // Keyboard mapping. ImGui will use those indices to peek into the io.KeysDown[] array.
    final int[] keyMap = new int[ImGuiKey.COUNT];
    keyMap[ImGuiKey.Tab] = GLFW_KEY_TAB;
    keyMap[ImGuiKey.LeftArrow] = GLFW_KEY_LEFT;
    keyMap[ImGuiKey.RightArrow] = GLFW_KEY_RIGHT;
    keyMap[ImGuiKey.UpArrow] = GLFW_KEY_UP;
    keyMap[ImGuiKey.DownArrow] = GLFW_KEY_DOWN;
    keyMap[ImGuiKey.PageUp] = GLFW_KEY_PAGE_UP;
    keyMap[ImGuiKey.PageDown] = GLFW_KEY_PAGE_DOWN;
    keyMap[ImGuiKey.Home] = GLFW_KEY_HOME;
    keyMap[ImGuiKey.End] = GLFW_KEY_END;
    keyMap[ImGuiKey.Insert] = GLFW_KEY_INSERT;
    keyMap[ImGuiKey.Delete] = GLFW_KEY_DELETE;
    keyMap[ImGuiKey.Backspace] = GLFW_KEY_BACKSPACE;
    keyMap[ImGuiKey.Space] = GLFW_KEY_SPACE;
    keyMap[ImGuiKey.Enter] = GLFW_KEY_ENTER;
    keyMap[ImGuiKey.Escape] = GLFW_KEY_ESCAPE;
    keyMap[ImGuiKey.KeyPadEnter] = GLFW_KEY_KP_ENTER;
    keyMap[ImGuiKey.A] = GLFW_KEY_A;
    keyMap[ImGuiKey.C] = GLFW_KEY_C;
    keyMap[ImGuiKey.V] = GLFW_KEY_V;
    keyMap[ImGuiKey.X] = GLFW_KEY_X;
    keyMap[ImGuiKey.Y] = GLFW_KEY_Y;
    keyMap[ImGuiKey.Z] = GLFW_KEY_Z;
    io.setKeyMap(keyMap);

    // ------------------------------------------------------------
    // Mouse cursors mapping
    cursors[ImGuiMouseCursor.Arrow] = glfwCreateStandardCursor(GLFW_ARROW_CURSOR);
    cursors[ImGuiMouseCursor.TextInput] = glfwCreateStandardCursor(GLFW_IBEAM_CURSOR);
    cursors[ImGuiMouseCursor.ResizeAll] = glfwCreateStandardCursor(GLFW_ARROW_CURSOR);
    cursors[ImGuiMouseCursor.ResizeNS] = glfwCreateStandardCursor(GLFW_VRESIZE_CURSOR);
    cursors[ImGuiMouseCursor.ResizeEW] = glfwCreateStandardCursor(GLFW_HRESIZE_CURSOR);
    cursors[ImGuiMouseCursor.ResizeNESW] = glfwCreateStandardCursor(GLFW_ARROW_CURSOR);
    cursors[ImGuiMouseCursor.ResizeNWSE] = glfwCreateStandardCursor(GLFW_ARROW_CURSOR);
    cursors[ImGuiMouseCursor.Hand] = glfwCreateStandardCursor(GLFW_HAND_CURSOR);
    cursors[ImGuiMouseCursor.NotAllowed] = glfwCreateStandardCursor(GLFW_ARROW_CURSOR);

    // ------------------------------------------------------------
    // GLFW callbacks to handle user input

    glfwSetKeyCallback(display, (w, key, scancode, action, mods) -> {
      if (action == GLFW_PRESS) {
        io.setKeysDown(key, true);
      } else if (action == GLFW_RELEASE) {
        io.setKeysDown(key, false);
      }

      io.setKeyCtrl(io.getKeysDown(GLFW_KEY_LEFT_CONTROL) || io.getKeysDown(GLFW_KEY_RIGHT_CONTROL));
      io.setKeyShift(io.getKeysDown(GLFW_KEY_LEFT_SHIFT) || io.getKeysDown(GLFW_KEY_RIGHT_SHIFT));
      io.setKeyAlt(io.getKeysDown(GLFW_KEY_LEFT_ALT) || io.getKeysDown(GLFW_KEY_RIGHT_ALT));
      io.setKeySuper(io.getKeysDown(GLFW_KEY_LEFT_SUPER) || io.getKeysDown(GLFW_KEY_RIGHT_SUPER));
    });

    glfwSetCharCallback(display, (w, c) -> {
      if (c != GLFW_KEY_DELETE) {
        io.addInputCharacter(c);
      }
    });

    glfwSetMouseButtonCallback(display, (w, button, action, mods) -> {
      io.setMouseDown(0, button == GLFW_MOUSE_BUTTON_1 && action != GLFW_RELEASE);
      io.setMouseDown(1, button == GLFW_MOUSE_BUTTON_2 && action != GLFW_RELEASE);
      io.setMouseDown(2, button == GLFW_MOUSE_BUTTON_3 && action != GLFW_RELEASE);
      io.setMouseDown(3, button == GLFW_MOUSE_BUTTON_4 && action != GLFW_RELEASE);
      io.setMouseDown(4, button == GLFW_MOUSE_BUTTON_5 && action != GLFW_RELEASE);

      if (!io.getWantCaptureMouse() && io.getMouseDown(1))
      {
        ImGui.setWindowFocus(null);
      }

      if (!ImGui.getIO().getWantCaptureMouse())
      {
        Input.onMouse(w, button, action, mods);
      }
    });

    glfwSetScrollCallback(display, (w, xOffset, yOffset) -> {
      io.setMouseWheelH(io.getMouseWheelH() + (float) xOffset);
      io.setMouseWheel(io.getMouseWheel() + (float) yOffset);
      if (!ImGui.getIO().getWantCaptureMouse())
      {
        Input.onScroll(w, xOffset, yOffset);
      }
    });

    io.setSetClipboardTextFn(new ImStrConsumer() {
      @Override
      public void accept(final String s) {
        glfwSetClipboardString(display, s);
      }
    });

    io.setGetClipboardTextFn(new ImStrSupplier() {
      @Override
      public String get() {
        final String clipboardString = glfwGetClipboardString(display);
        return Objects.requireNonNullElse(clipboardString, "");
      }
    });

    // ------------------------------------------------------------
    // Fonts configuration
    // Read: https://raw.githubusercontent.com/ocornut/imgui/master/docs/FONTS.txt

    final ImFontAtlas fontAtlas = io.getFonts();
    final ImFontConfig fontConfig = new ImFontConfig(); // Natively allocated object, should be explicitly destroyed

    // Glyphs could be added per-font as well as per config used globally like here
    fontConfig.setGlyphRanges(fontAtlas.getGlyphRangesCyrillic());

    // Add a default font, which is 'ProggyClean.ttf, 13px'
    // fontAtlas.addFontDefault();

    // Fonts merge example
    //fontConfig.setMergeMode(true); // When enabled, all fonts added with this config would be merged with the previously added font
    //fontConfig.setPixelSnapH(true);

    final ByteBuffer font = Memory.load("platform/resources/fonts/OpenSans-Regular.ttf");
    assert font != null;
    byte[] fontData = new byte[font.limit()];
    for (int i = 0; i < font.limit(); i++)
    {
      fontData[i] = font.get(i);
    }

    fontAtlas.addFontFromMemoryTTF(fontData, 16, fontConfig);

    fontConfig.setMergeMode(false);
    fontConfig.setPixelSnapH(false);

    fontConfig.destroy(); // After all fonts were added we don't need this config more

    // ------------------------------------------------------------
    // Use freetype instead of stb_truetype to build a fonts texture
    ImGuiFreeType.buildFontAtlas(fontAtlas);

    // https://github.com/ocornut/imgui/issues/707#issuecomment-411226461
    ImGuiStyle style = ImGui.getStyle();
    style.setChildRounding(4.0f);
    style.setFrameBorderSize(1.0f);
    style.setFrameRounding(0.0f);
    style.setGrabMinSize(7.0f);
    style.setPopupRounding(2.0f);
    style.setScrollbarRounding(12.0f);
    style.setScrollbarSize(13.0f);
    style.setTabBorderSize(1.0f);
    style.setTabRounding(0.0f);
    style.setWindowRounding(0.0f);

    style.setColor(ImGuiCol.Text, 1.0f, 1.0f, 1.0f, 1.0f);
    style.setColor(ImGuiCol.TextDisabled, 0.5f, 0.5f, 0.5f, 1.0f);
    style.setColor(ImGuiCol.WindowBg, 0.180f, 0.180f, 0.180f, 1.000f);
    style.setColor(ImGuiCol.ChildBg, 0.280f, 0.280f, 0.280f, 0.000f);
    style.setColor(ImGuiCol.PopupBg, 0.313f, 0.313f, 0.313f, 1.000f);
    style.setColor(ImGuiCol.Border, 0.266f, 0.266f, 0.266f, 1.000f);
    style.setColor(ImGuiCol.BorderShadow, 0.000f, 0.000f, 0.000f, 0.000f);
    style.setColor(ImGuiCol.FrameBg, 0.160f, 0.160f, 0.160f, 1.000f);
    style.setColor(ImGuiCol.FrameBgHovered, 0.200f, 0.200f, 0.200f, 1.000f);
    style.setColor(ImGuiCol.FrameBgActive, 0.280f, 0.280f, 0.280f, 1.000f);
    style.setColor(ImGuiCol.TitleBg, 0.148f, 0.148f, 0.148f, 1.000f);
    style.setColor(ImGuiCol.TitleBgActive, 0.148f, 0.148f, 0.148f, 1.000f);
    style.setColor(ImGuiCol.TitleBgCollapsed, 0.148f, 0.148f, 0.148f, 1.000f);
    style.setColor(ImGuiCol.MenuBarBg, 0.195f, 0.195f, 0.195f, 1.000f);
    style.setColor(ImGuiCol.ScrollbarBg, 0.160f, 0.160f, 0.160f, 1.000f);
    style.setColor(ImGuiCol.ScrollbarGrab, 0.277f, 0.277f, 0.277f, 1.000f);
    style.setColor(ImGuiCol.ScrollbarGrabHovered, 0.300f, 0.300f, 0.300f, 1.000f);
    style.setColor(ImGuiCol.ScrollbarGrabActive, 1.000f, 0.391f, 0.000f, 1.000f);
    style.setColor(ImGuiCol.CheckMark, 1.000f, 1.000f, 1.000f, 1.000f);
    style.setColor(ImGuiCol.SliderGrab, 0.391f, 0.391f, 0.391f, 1.000f);
    style.setColor(ImGuiCol.SliderGrabActive, 1.000f, 0.391f, 0.000f, 1.000f);
    style.setColor(ImGuiCol.Button, 1.000f, 1.000f, 1.000f, 0.000f);
    style.setColor(ImGuiCol.ButtonHovered, 1.000f, 1.000f, 1.000f, 0.156f);
    style.setColor(ImGuiCol.ButtonActive, 1.000f, 1.000f, 1.000f, 0.391f);
    style.setColor(ImGuiCol.Header, 0.313f, 0.313f, 0.313f, 1.000f);
    style.setColor(ImGuiCol.HeaderHovered, 0.469f, 0.469f, 0.469f, 1.000f);
    style.setColor(ImGuiCol.HeaderActive, 0.469f, 0.469f, 0.469f, 1.000f);
    style.setColor(ImGuiCol.Separator, 0.266f, 0.266f, 0.266f, 1.000f);
    style.setColor(ImGuiCol.SeparatorHovered, 0.391f, 0.391f, 0.391f, 1.000f);
    style.setColor(ImGuiCol.SeparatorActive, 1.000f, 0.391f, 0.000f, 1.000f);
    style.setColor(ImGuiCol.ResizeGrip, 1.000f, 1.000f, 1.000f, 0.250f);
    style.setColor(ImGuiCol.ResizeGripHovered, 1.000f, 1.000f, 1.000f, 0.670f);
    style.setColor(ImGuiCol.ResizeGripActive, 1.000f, 0.391f, 0.000f, 1.000f);
    style.setColor(ImGuiCol.Tab, 0.098f, 0.098f, 0.098f, 1.000f);
    style.setColor(ImGuiCol.TabHovered, 0.352f, 0.352f, 0.352f, 1.000f);
    style.setColor(ImGuiCol.TabActive, 0.195f, 0.195f, 0.195f, 1.000f);
    style.setColor(ImGuiCol.TabUnfocused, 0.098f, 0.098f, 0.098f, 1.000f);
    style.setColor(ImGuiCol.TabUnfocusedActive, 0.195f, 0.195f, 0.195f, 1.000f);
    style.setColor(ImGuiCol.DockingPreview, 1.000f, 0.391f, 0.000f, 0.781f);
    style.setColor(ImGuiCol.DockingEmptyBg, 0.180f, 0.180f, 0.180f, 1.000f);
    style.setColor(ImGuiCol.PlotLines, 0.469f, 0.469f, 0.469f, 1.000f);
    style.setColor(ImGuiCol.PlotLinesHovered, 1.000f, 0.391f, 0.000f, 1.000f);
    style.setColor(ImGuiCol.PlotHistogram, 0.586f, 0.586f, 0.586f, 1.000f);
    style.setColor(ImGuiCol.PlotHistogramHovered, 1.000f, 0.391f, 0.000f, 1.000f);
    style.setColor(ImGuiCol.TextSelectedBg, 1.000f, 1.000f, 1.000f, 0.156f);
    style.setColor(ImGuiCol.DragDropTarget, 1.000f, 0.391f, 0.000f, 1.000f);
    style.setColor(ImGuiCol.NavHighlight, 1.000f, 0.391f, 0.000f, 1.000f);
    style.setColor(ImGuiCol.NavWindowingHighlight, 1.000f, 0.391f, 0.000f, 1.000f);
    style.setColor(ImGuiCol.NavWindowingDimBg, 0.000f, 0.000f, 0.000f, 0.586f);
    style.setColor(ImGuiCol.ModalWindowDimBg, 0.000f, 0.000f, 0.000f, 0.586f);

    // Method initializes LWJGL3 renderer.
    // This method SHOULD be called after you've initialized your ImGui configuration (fonts and so on).
    // ImGui context should be created as well.
    imGuiGl3.init("#version 130");
  }
}
