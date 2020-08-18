package engine.ui;

import imgui.*;
import imgui.enums.ImGuiColorEditFlags;
import imgui.enums.ImGuiCond;
import imgui.enums.ImGuiInputTextFlags;

public class ExampleUI
{
  private static final int DODGERBLUE_COLOR = ImColor.get32RGBHex("#1E90FF");
  private static final int CORAL_COLOR = ImColor.get32RGBHex("#FF7F50");
  private static final int LIMEGREEN_COLOR = ImColor.get32RGBHex("#32CD32");

  // Test data for payload
  private static final byte[] testPayload = "Test Payload".getBytes();
  private static String dropTargetText = "Drop Here";

  // To modify background color dynamically
  final static float[] backgroundColor = new float[]{0.5f, 0, 0};

  // Resizable input example
  private static final ImString resizableStr = new ImString(5);
  private static final ImBool showDemoWindow = new ImBool();

  final static ImVec2 windowSize = new ImVec2(); // Vector to store "Custom Window" size
  final static ImVec2 windowPos = new ImVec2(); // Vector to store "Custom Window" position

  public static void render()
  {
    ImGui.setNextWindowSize(600, 600, ImGuiCond.Once);
    ImGui.setNextWindowPos(10, 10, ImGuiCond.Once);

    ImGui.begin("Custom window");  // Start Custom window
    ImGui.text("Hello World");
    ImGui.end();
    //

    /*
    // Draw an image in the bottom-right corner of the window
    ImGui.getWindowSize(windowSize);
    ImGui.getWindowPos(windowPos);
    final float xPoint = windowPos.x + windowSize.x - 100;
    final float yPoint = windowPos.y + windowSize.y;

    // Checkbox to show demo window
    ImGui.checkbox("Show demo window", showDemoWindow);

    ImGui.separator();

    // Drag'n'Drop functionality
    ImGui.button("Drag me");
    if (ImGui.beginDragDropSource()) {
      ImGui.setDragDropPayload("payload_type", testPayload, testPayload.length);
      ImGui.text("Drag started");
      ImGui.endDragDropSource();
    }
    ImGui.sameLine();
    ImGui.text(dropTargetText);
    if (ImGui.beginDragDropTarget()) {
      final byte[] payload = ImGui.acceptDragDropPayload("payload_type");
      if (payload != null) {
        dropTargetText = new String(payload);
      }
      ImGui.endDragDropTarget();
    }

    // Color picker
    ImGui.alignTextToFramePadding();
    ImGui.text("Background color:");
    ImGui.sameLine();
    ImGui.colorEdit3("##click_counter_col", backgroundColor, ImGuiColorEditFlags.NoInputs | ImGuiColorEditFlags.NoDragDrop);

    ImGui.separator();

    // Input field with auto-resize ability
    ImGui.text("You can use text inputs with auto-resizable strings!");
    ImGui.inputText("Resizable input", resizableStr, ImGuiInputTextFlags.CallbackResize);
    ImGui.text("text len:");
    ImGui.sameLine();
    ImGui.textColored(DODGERBLUE_COLOR, Integer.toString(resizableStr.getLength()));
    ImGui.sameLine();
    ImGui.text("| buffer size:");
    ImGui.sameLine();
    ImGui.textColored(CORAL_COLOR, Integer.toString(resizableStr.getBufferSize()));

    ImGui.separator();
    ImGui.newLine();

    // Link to the original demo file
    ImGui.text("Consider to look the original ImGui demo: ");
    ImGui.setNextItemWidth(500);
    ImGui.textColored(LIMEGREEN_COLOR, "Hello World");
    ImGui.sameLine();
    if (ImGui.button("Copy")) {
      ImGui.setClipboardText("Hello World");
    }*/

    //ImGui.end();  // End Custom window

    //if (showDemoWindow.get())
    //{
      //ImGui.showDemoWindow(showDemoWindow);
    //}
  }
}
