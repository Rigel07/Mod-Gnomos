package com.gnomos.entity;

import com.gnomos.client.GnomeDialogueScreen;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import java.util.EnumSet;
import java.util.List;

public class GnomeEntity extends PathfinderMob {
    public static final String[] COLORS = {"red", "blue", "green", "yellow", "purple"};

    private static final String[] NAMES = {"Pipo", "Tuk", "Barbolín", "Nico", "Fungo", "Rufo", "Gumi", "Zurri",
            "Bruno", "Olmo", "Tilda", "Mirta", "Pelusa", "Chispa", "Musgo", "Ñoño", "Bellota", "Tomillo"};

    private static final EntityDataAccessor<Integer> DATA_VARIANT =
            SynchedEntityData.defineId(GnomeEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_PROFESSION =
            SynchedEntityData.defineId(GnomeEntity.class, EntityDataSerializers.INT);

    @Nullable
    private BlockPos home;
    private int talkTicks;
    @Nullable
    private Player talkingTo;

    public GnomeEntity(EntityType<? extends GnomeEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 12.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.28D)
                .add(Attributes.FOLLOW_RANGE, 16.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new TalkGoal(this));
        this.goalSelector.addGoal(2, new PanicGoal(this, 1.4D));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Monster.class, 8.0F, 0.9D, 1.15D));
        this.goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 0.8D));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.7D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }

    // ------------------------------------------------------------ datos

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_VARIANT, 0);
        this.entityData.define(DATA_PROFESSION, 0);
    }

    public int getVariant() {
        return this.entityData.get(DATA_VARIANT);
    }

    public void setVariant(int variant) {
        this.entityData.set(DATA_VARIANT, Math.floorMod(variant, COLORS.length));
    }

    public GnomeProfession getProfession() {
        return GnomeProfession.byId(this.entityData.get(DATA_PROFESSION));
    }

    public void setProfession(GnomeProfession profession) {
        this.entityData.set(DATA_PROFESSION, profession.ordinal());
        if (!this.level().isClientSide) {
            updateHeldItem();
        }
    }

    public String getGnomeName() {
        return NAMES[Math.floorMod(this.getUUID().hashCode(), NAMES.length)];
    }

    private void updateHeldItem() {
        ItemStack held = switch (getProfession()) {
            case FARMER -> new ItemStack(Items.IRON_HOE);
            case MINER -> new ItemStack(Items.IRON_PICKAXE);
            case BLACKSMITH -> new ItemStack(Items.IRON_AXE);
            case FORAGER -> new ItemStack(Items.SWEET_BERRIES);
            case ELDER -> new ItemStack(Items.LANTERN);
        };
        this.setItemSlot(EquipmentSlot.MAINHAND, held);
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Variant", getVariant());
        tag.putInt("Profession", getProfession().ordinal());
        if (home != null) {
            tag.putInt("HomeX", home.getX());
            tag.putInt("HomeY", home.getY());
            tag.putInt("HomeZ", home.getZ());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setVariant(tag.getInt("Variant"));
        setProfession(GnomeProfession.byId(tag.getInt("Profession")));
        if (tag.contains("HomeX")) {
            home = new BlockPos(tag.getInt("HomeX"), tag.getInt("HomeY"), tag.getInt("HomeZ"));
            this.restrictTo(home, 22);
        }
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason,
                                        @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
        if (reason != MobSpawnType.STRUCTURE) {
            setVariant(this.random.nextInt(COLORS.length));
            setProfession(GnomeProfession.byId(this.random.nextInt(GnomeProfession.values().length - 1)));
        }
        return super.finalizeSpawn(level, difficulty, reason, data, tag);
    }

    // ------------------------------------------------------ diálogo y trueque

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }
        boolean client = this.level().isClientSide;
        if (client) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> GnomeDialogueScreen.open(this));
        } else {
            this.talkingTo = player;
            this.talkTicks = 600;
            this.getNavigation().stop();
        }
        return InteractionResult.sidedSuccess(client);
    }

    public boolean isTalking() {
        return talkTicks > 0 && talkingTo != null && talkingTo.isAlive() && talkingTo.distanceToSqr(this) < 64.0D;
    }

    @Nullable
    public Player getTalkingTo() {
        return talkingTo;
    }

    public void stopTalking() {
        this.talkTicks = 0;
        this.talkingTo = null;
    }

    public void tryTrade(ServerPlayer player, int index) {
        List<GnomeTrades.Trade> trades = GnomeTrades.forProfession(getProfession());
        if (index < 0 || index >= trades.size()) {
            return;
        }
        GnomeTrades.Trade trade = trades.get(index);
        Item costItem = trade.cost().get();
        int cost = GnomeTrades.getCost(player, trade);
        if (GnomeTrades.count(player, costItem) < cost) {
            player.displayClientMessage(Component.translatable("message.gnomos.not_enough"), true);
            this.playSound(SoundEvents.VILLAGER_NO, 1.0F, 1.3F);
            return;
        }
        GnomeTrades.remove(player, costItem, cost);
        ItemStack reward = new ItemStack(trade.result().get(), trade.resultCount());
        if (!player.getInventory().add(reward)) {
            player.drop(reward, false);
        }
        player.displayClientMessage(Component.translatable("message.gnomos.trade_done"), true);
        this.playSound(SoundEvents.VILLAGER_TRADE, 1.0F, 1.3F);
        this.talkTicks = 600;
    }

    // ----------------------------------------------------------------- vida

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            if (home == null) {
                home = this.blockPosition();
                this.restrictTo(home, 22);
            }
            if (talkTicks > 0) {
                talkTicks--;
            }
        }
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 0.55F;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.VILLAGER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.VILLAGER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.VILLAGER_DEATH;
    }

    /** El gnomo se queda quieto mirando al jugador mientras habla con él. */
    private static class TalkGoal extends Goal {
        private final GnomeEntity gnome;

        TalkGoal(GnomeEntity gnome) {
            this.gnome = gnome;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return gnome.isTalking();
        }

        @Override
        public boolean canContinueToUse() {
            return gnome.isTalking();
        }

        @Override
        public void start() {
            gnome.getNavigation().stop();
        }

        @Override
        public void tick() {
            Player player = gnome.getTalkingTo();
            if (player != null) {
                gnome.getLookControl().setLookAt(player, 30.0F, 30.0F);
            }
        }
    }
}
