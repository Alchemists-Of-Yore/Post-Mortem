package dev.tazer.post_mortem;

import dev.tazer.post_mortem.entity.SpiritAnchor;
import foundry.veil.api.client.render.VeilRenderer;
import foundry.veil.api.client.render.post.PostPipeline;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import foundry.veil.api.client.render.shader.uniform.ShaderUniform;
import foundry.veil.platform.VeilEventPlatform;
import net.minecraft.client.Minecraft;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class PostMortemClient {
    public static void init() {
        VeilEventPlatform.INSTANCE.onVeilRendererAvailable(PostMortemClient::onVeilRendererAvailable);
        VeilEventPlatform.INSTANCE.preVeilPostProcessing(PostMortemClient::preVeilPostProcessing);
    }

    public static void onVeilRendererAvailable(VeilRenderer renderer) {
        renderer.getPostProcessingManager().add(PostMortem.location("dead_fog"));
    }

    private static void preVeilPostProcessing(ResourceLocation location, PostPipeline postPipeline, PostPipeline.Context context) {
        if (location.equals(PostMortem.location("dead_fog"))) {
            Player player = Minecraft.getInstance().player;
            if (player != null) {
                ShaderProgram shader = context.getShader(PostMortem.location("dead_fog"));
                if (shader != null) {
                    ShaderUniform shouldRender = shader.getUniform("ShouldRender");
                    ShaderUniform anchorPosition = shader.getUniform("AnchorPosition");
                    ShaderUniform playerPosition = shader.getUniform("PlayerPosition");

                    if (shouldRender != null && anchorPosition != null && playerPosition != null) {
                        playerPosition.setVector((float) player.getX(), (float) player.getZ());

                        SpiritAnchor anchor = player.getAnchor();
                        if (anchor == null) {
                            shouldRender.setInt(0);
                        } else {
                            GlobalPos position = anchor.getPos(player.level());
                            if (position != null) {
                                Vec3 vec3 = Vec3.atCenterOf(position.pos());
                                shouldRender.setInt(1);
                                anchorPosition.setVector((float) vec3.x, (float) vec3.z);
                            } else shouldRender.setInt(0);
                        }
                    }
                }
            }
        }
    }
}
