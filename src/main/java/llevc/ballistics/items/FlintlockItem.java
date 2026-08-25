package llevc.ballistics.items;

import llevc.ballistics.FlintlockProjectile;
import llevc.ballistics.ModItems;
import llevc.ballistics.ModSounds;
import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Unique;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public class FlintlockItem extends ProjectileWeaponItem {
    public FlintlockItem(Properties properties) {
        super(properties);
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return ARROW_ONLY;
    }

    @Override
    public int getDefaultProjectileRange() {
        return 16;
    }

    @Override //bow code as placeholder
    protected void shootProjectile(LivingEntity livingEntity, Projectile projectile, int i, float f, float g, float h, @Nullable LivingEntity livingEntity2) {
        projectile.shootFromRotation(livingEntity, livingEntity.getXRot(), livingEntity.getYRot(), 0.0F, f, g);
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack itemStack) {
        return ItemUseAnimation.CROSSBOW;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        if (isCharged(itemStack)) {
            setCharged(itemStack, false);

            if (isLoaded(itemStack)) {
                setLoaded(itemStack,false);
                if (level instanceof ServerLevel serverLevel) {
                    this.shoot(serverLevel, player, player.getUsedItemHand(), itemStack, List.of(new ItemStack(ModItems.Ball)), 5.0f, 1.0F, true, null);
                }
                level.playSound(null,player.blockPosition(),ModSounds.FlintlockShoot,player.getSoundSource(),0.5f,1.0f);
                return InteractionResult.SUCCESS;
            } else {
                level.playSound(null,player.blockPosition(),ModSounds.FlintlockDryShoot,player.getSoundSource(),0.5f,1.0f);
                return InteractionResult.PASS;
            }
        } else {
            player.startUsingItem(interactionHand);
            level.playSound(null,player.blockPosition(),ModSounds.FlintlockClick,player.getSoundSource(),0.5f,1.0f);
            return InteractionResult.PASS;
        }
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack itemStack, int i) {
        //Ballistics.LOGGER.info(String.valueOf(i));
        int hey = getUseDuration(itemStack,livingEntity) - i;
        int maxhey = 20;
        boolean sonofa = false;

        if (itemStack.has(DataComponents.ENCHANTMENTS)) {
            ItemEnchantments itemEnchantments = itemStack.getOrDefault(DataComponents.ENCHANTMENTS,ItemEnchantments.EMPTY);
            Iterator<Holder<Enchantment>> wow = itemEnchantments.keySet().iterator();
            while (wow.hasNext()) {
                Holder<Enchantment> heyo = wow.next();
                if (heyo.is(Identifier.fromNamespaceAndPath("ballistics","quickload"))) {
                    sonofa = true;
                }
            }
        }

        if (hey > maxhey && !isCharged(itemStack)) {
            setCharged(itemStack,true);

            if (sonofa && livingEntity.isHolding(ModItems.Ball)) {
                EquipmentSlot equipmentSlot = EquipmentSlot.MAINHAND;
                if (!livingEntity.getItemBySlot(equipmentSlot).is(ModItems.Ball)) {
                    equipmentSlot = EquipmentSlot.OFFHAND;
                }
                livingEntity.getItemBySlot(equipmentSlot).consume(1,livingEntity);
                setLoaded(itemStack,true);
            }
        }
    }

    @Override
    public boolean releaseUsing(ItemStack itemStack, Level level, LivingEntity livingEntity, int i) {
        return super.releaseUsing(itemStack, level, livingEntity, i);
    }

    @Override
    public int getUseDuration(ItemStack itemStack, LivingEntity livingEntity) {
        return 72000;
    }

    public boolean isCharged(ItemStack itemStack) {
        CustomData yo = itemStack.get(DataComponents.CUSTOM_DATA);
        if (yo == null) {
            return false;
        }
        if (yo.isEmpty()) {
            return false;
        }
        if (yo.copyTag().getBoolean("charged").isPresent()) {
            return yo.copyTag().getBoolean("charged").get().booleanValue();
        }
        return false;
    }

    public void setCharged(ItemStack itemStack, boolean bool) {
        CustomData yo = itemStack.get(DataComponents.CUSTOM_DATA);
        CompoundTag heyo = new CompoundTag();
        if (yo != null) {
            if (!yo.isEmpty()){
                heyo = yo.copyTag();
            }
        }
        heyo.remove("charged");
        heyo.putBoolean("charged", bool);
        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(heyo));
    }

    public boolean isLoaded(ItemStack itemStack) {
        ChargedProjectiles yo = itemStack.getOrDefault(DataComponents.CHARGED_PROJECTILES,ChargedProjectiles.EMPTY);
        return !yo.isEmpty();
    }

    public void setLoaded(ItemStack itemStack, boolean bool) {
        ChargedProjectiles yo = itemStack.getOrDefault(DataComponents.CHARGED_PROJECTILES,ChargedProjectiles.EMPTY);
        if (bool) {
            itemStack.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.of(new ItemStackTemplate(ModItems.Ball)));
        } else {
            itemStack.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY);
        }
    }

    @Override
    protected @NotNull Projectile createProjectile(Level level, LivingEntity livingEntity, ItemStack itemStack, ItemStack itemStack2, boolean bl) {
        FlintlockProjectile flintlockProjectile = new FlintlockProjectile(level,livingEntity, Vec3.ZERO);
        flintlockProjectile.setPos(livingEntity.getEyePosition());
        return flintlockProjectile;
    }
}
