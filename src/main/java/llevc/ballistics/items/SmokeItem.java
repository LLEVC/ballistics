package llevc.ballistics.items;

import llevc.ballistics.Ballistics;
import llevc.ballistics.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.enchantment.effects.SpawnParticlesEffect;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;

public class SmokeItem extends Item {

    public SmokeItem(Properties properties) {
        super(properties);
    }

    public boolean smokeParticles() {
        if (this.equals(ModItems.pick)) {
            return false;
        }
        return true;
    }

    public float applyRate() {
        return 40;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand interactionHand) {
        if (!level.isClientSide()) {
            player.startUsingItem(interactionHand);
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack itemStack, int i) {
        if (level.isClientSide()) {
            return;
        }
        Ballistics.LOGGER.info(String.valueOf(i));
        if (Math.round(i/applyRate()) == i/applyRate() || i <= 1) {
            for (MobEffectInstance nah : itemStack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).getAllEffects()) {
                MobEffectInstance yah = nah.withScaledDuration(1 / 5f);
                livingEntity.forceAddEffect(yah,livingEntity);
            }
            itemStack.hurtAndBreak(1,livingEntity,livingEntity.getUsedItemHand());
            if (i <= 1) {
                livingEntity.stopUsingItem();
                if (livingEntity instanceof Player player) {
                    player.getCooldowns().addCooldown(itemStack,getUseDuration(itemStack, livingEntity)*2);
                    if (smokeParticles() && !player.level().isClientSide()) {
                        livingEntity.hurtServer((ServerLevel) player.level(), livingEntity.damageSources().source(ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath("ballistics","lungs"))),2.0f);
                    }
                }
            }
        }
    } 

    public void spawnSmokeParticles(Level level, LivingEntity livingEntity) {
        if (smokeParticles()) {
            for (int j = 0; j < 5; j++) {
                ParticleUtils.spawnParticleBelow(level, new BlockPos((int) livingEntity.getEyePosition().x, (int) livingEntity.getEyePosition().y, (int) livingEntity.getEyePosition().z), RandomSource.create(), ParticleTypes.POOF);
            }
        }
    }

    public void sendSmokeParticles(ServerLevel serverLevel, LivingEntity livingEntity) {
        if (smokeParticles()) {
            for (int j = 0; j < 5; j++) {
                RandomSource randomSource = RandomSource.create();
                serverLevel.sendParticles(ParticleTypes.POOF,livingEntity.getEyePosition().x+randomSource.nextDouble()-0.5, livingEntity.getEyePosition().y, livingEntity.getEyePosition().z+randomSource.nextDouble()-0.5,0,0,0,0,0);
                //ParticleUtils.spawnParticleBelow(level, new BlockPos((int) livingEntity.getEyePosition().x, (int) livingEntity.getEyePosition().y, (int) livingEntity.getEyePosition().z), RandomSource.create(), ParticleTypes.POOF);
            }
        }
    }

    @Override
    public boolean releaseUsing(ItemStack itemStack, Level level, LivingEntity livingEntity, int i) {
        if (level.isClientSide()) {
            //spawnSmokeParticles(level,livingEntity);
            return false;
        }
        if (level instanceof ServerLevel serverLevel) {
            sendSmokeParticles(serverLevel,livingEntity);
        }
        if (livingEntity instanceof Player player) {
            player.getCooldowns().addCooldown(itemStack,(getUseDuration(itemStack, livingEntity)-i)*2);
        }
        return false;
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack itemStack) {
        return ItemUseAnimation.TOOT_HORN;
    }

    @Override
    public int getUseDuration(ItemStack itemStack, LivingEntity livingEntity) {
        if (itemStack.is(ModItems.pick)) {
            return 20*2;
        } else if (itemStack.is(ModItems.vape)) {
            return 20*12;
        }
        return 20*6;
    }

    public int ticksUsed = 0;
    @Override
    public void inventoryTick(ItemStack itemStack, ServerLevel serverLevel, Entity entity, @Nullable EquipmentSlot equipmentSlot) {
        if (equipmentSlot == EquipmentSlot.HEAD) {
            if (entity instanceof LivingEntity livingEntity) {
                boolean cooldown = false;
                int woah = getUseDuration(itemStack, livingEntity) - ticksUsed;

                if (entity instanceof Player player) {
                    cooldown = player.getCooldowns().isOnCooldown(itemStack);
                }

                if (livingEntity.isCrouching() && !cooldown) {
                    Ballistics.LOGGER.info(String.valueOf(getUseDuration(itemStack, livingEntity)));
                    if (Math.round(woah / applyRate()) == woah / applyRate() || woah <= 1) {
                        for (MobEffectInstance nah : itemStack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).getAllEffects()) {
                            MobEffectInstance yah = nah.withScaledDuration(1 / 5f);
                            livingEntity.forceAddEffect(yah, livingEntity);
                        }
                        if (!(itemStack.is(ModItems.pick) && itemStack.getOrDefault(DataComponents.POTION_CONTENTS,PotionContents.EMPTY) == PotionContents.EMPTY)) {
                            itemStack.hurtAndBreak(1, livingEntity, equipmentSlot);
                        }
                        if (woah <= 1) {
                            if (livingEntity instanceof Player player) {
                                player.getCooldowns().addCooldown(itemStack, getUseDuration(itemStack, livingEntity) * 2);
                                if (smokeParticles()) {
                                    livingEntity.hurtServer(serverLevel, livingEntity.damageSources().source(ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath("ballistics","lungs"))),2.0f);
                                }
                            }
                        }
                    }
                    ticksUsed++;
                } else if ((!livingEntity.isCrouching() || cooldown) && ticksUsed > 0) {
                    ticksUsed = 0;
                    releaseUsing(itemStack,serverLevel,livingEntity,woah);
                }
            }
        }
    }
}
