package edu.uri.wbl.tex_tronics.smartglove.visualize;

import edu.uri.wbl.tex_tronics.smartglove.io.SmartGloveInterface;

public enum Choice implements SmartGloveInterface{

    //TODO: Changed these to descriptions. Watch out
    FINGER_TAP(InstructionsText.FINGER_TAP_TEXT),
    CLOSED_GRIP(InstructionsText.CLOSED_GRIP_TEXT),
    HAND_FLIP(InstructionsText.HAND_FLIP_TEXT),
    SCREEN_TAP(InstructionsText.SCREEN_TAP_TEXT),
    HEEL_TAP(InstructionsText.HEEL_TAP_TEXT),
    TOE_TAP(InstructionsText.TOE_TAP_TEXT),
    FOOT_STOMP(InstructionsText.FOOT_STOMP_TEXT),
    WALK_STEPS(InstructionsText.WALK_STEPS_TEXT);

    private String displayName;

    Choice(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}