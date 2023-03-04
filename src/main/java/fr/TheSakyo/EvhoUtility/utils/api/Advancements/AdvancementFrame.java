package fr.TheSakyo.EvhoUtility.utils.api.Advancements;

import net.minecraft.advancements.FrameType;

/**********************************/
/* TYPES DE CADRES D'ACHIEVEMENTS */
/**********************************/
public enum AdvancementFrame {

    TASK(FrameType.TASK),
    CHALLENGE(FrameType.CHALLENGE),
    GOAL(FrameType.GOAL);


    private FrameType nms;

    AdvancementFrame(FrameType nms) { this.nms = nms; }

    public FrameType getNMS() { return nms; }

}
/**********************************/
/* TYPES DE CADRES D'ACHIEVEMENTS */
/**********************************/
