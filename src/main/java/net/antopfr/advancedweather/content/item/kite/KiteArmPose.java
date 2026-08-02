package net.antopfr.advancedweather.content.item.kite;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.HumanoidArm;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;
import net.neoforged.neoforge.client.IArmPoseTransformer;

public class KiteArmPose {

    public static final EnumProxy<HumanoidModel.ArmPose> POSE_PARAMETERS =
            new EnumProxy<>(HumanoidModel.ArmPose.class,
                    false,
                    (IArmPoseTransformer) KiteArmPose::apply);

    private static void apply(HumanoidModel<?> model, Object entity, HumanoidArm arm) {
        boolean right = arm == HumanoidArm.RIGHT;
        ModelPart part = right ? model.rightArm : model.leftArm;

        part.xRot = -2.35f;
        part.yRot = right ? -0.22f : 0.22f;
        part.zRot = right ? 0.14f : -0.14f;
    }

    public static HumanoidModel.ArmPose get() {
        return POSE_PARAMETERS.getValue();
    }
}
