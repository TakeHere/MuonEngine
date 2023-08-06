package core.gui;

import core.Window;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;
import imgui.internal.ImGui;

public class GameViewWindow {

    private static boolean isHovered = false;

    public static void imGui(){
        ImGui.begin("Game viewport", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse);

        isHovered = ImGui.isWindowHovered();

        ImVec2 winSize = getLargestSizeForViewport();
        ImVec2 winPos = getCenteredPositionForViewport(winSize);

        ImGui.setCursorPos(winPos.x, winPos.y);
        int texId = Window.get().getFrameBuffer().getTextureId();
        ImGui.image(texId, winSize.x, winSize.y, 0, 1, 1,0);

        ImGui.end();
    }

    private static ImVec2 getCenteredPositionForViewport(ImVec2 aspectSize) {
        ImVec2 winSize = new ImVec2();
        ImGui.getContentRegionAvail(winSize);
        winSize.x -= ImGui.getScrollX();
        winSize.y -= ImGui.getScrollY();

        float viewportX = (winSize.x/2.0f) - (aspectSize.x / 2.0f);
        float viewportY = (winSize.y/2.0f) - (aspectSize.y/2.0f);

        return new ImVec2(viewportX + ImGui.getCursorPosX(), viewportY + ImGui.getCursorPosY());
    }

    private static ImVec2 getLargestSizeForViewport() {
        ImVec2 winSize = new ImVec2();
        ImGui.getContentRegionAvail(winSize);
        winSize.x -= ImGui.getScrollX();
        winSize.y -= ImGui.getScrollY();

        float aspectWidth = winSize.x;
        float aspectHeight = aspectWidth / Window.getTargetAspectRatio();

        if (aspectHeight > winSize.y){
            //Pillarbox mode
            aspectHeight = winSize.y;
            aspectWidth = aspectHeight * Window.getTargetAspectRatio();
        }

        return new ImVec2(aspectWidth, aspectHeight);
    }

    public static boolean isIsHovered() {
        return isHovered;
    }
}