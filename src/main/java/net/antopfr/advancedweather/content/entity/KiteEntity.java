package net.antopfr.advancedweather.content.entity;

import net.antopfr.advancedweather.content.item.kite.KiteColors;
import net.antopfr.advancedweather.content.item.kite.KiteItem;
import net.antopfr.advancedweather.weather.effect.global.wind.WindDirection;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class KiteEntity extends Entity {

    private static final EntityDataAccessor<Integer> DATA_OWNER =
            SynchedEntityData.defineId(KiteEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<KiteColors> DATA_COLORS =
            SynchedEntityData.defineId(KiteEntity.class, AWEntitySerializers.KITE_COLORS_SERIALIZER.get());

    public KiteEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public KiteEntity(Level level, Player owner, KiteColors colors) {
        this(AWEntities.KITE.get(), level);
        setPos(owner.getX(), owner.getY() + 3.5, owner.getZ());
        entityData.set(DATA_OWNER, owner.getId());
        entityData.set(DATA_COLORS, colors);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder b) {
        b.define(DATA_OWNER, -1);
        b.define(DATA_COLORS, KiteColors.WHITE);
    }

    public KiteColors getColors() { return entityData.get(DATA_COLORS); }

    private Player owner() {
        int id = entityData.get(DATA_OWNER);
        return id >= 0 && level().getEntity(id) instanceof Player p ? p : null;
    }

    @Override
    public void tick() {
        super.tick();
        Player owner = owner();
        if (owner == null || !holdingKite(owner)) {
            if (!level().isClientSide) discard();
            return;
        }

        Vec3 windDir = WindDirection.getHorizontal(level());

        float intensity = level() instanceof ServerLevel sl
                ? WindDirection.getIntensity(sl)
                : WindDirection.getIntensityClient();

        if (!level().isClientSide) {
            applyPull(owner, intensity);
        }

        double height = 3.5 + intensity * 2.0;
        double drift = 2.0 + intensity * 3.0;

        Vec3 base = owner.position().add(0, height, 0);
        Vec3 target = base.add(windDir.scale(drift));

        Vec3 pos = position();
        Vec3 next = pos.add(target.subtract(pos).scale(0.15));

        float bob = Mth.sin((tickCount + intensity * 20f) * 0.15f) * 0.15f;
        setPos(next.x, next.y + bob, next.z);
    }

    private boolean holdingKite(Player p) {
        return p.getMainHandItem().getItem() instanceof KiteItem
                || p.getOffhandItem().getItem() instanceof KiteItem;
    }

    private void applyPull(Player owner, float intensity) {
        if (intensity < 0.45f) return;

        float pull = (intensity - 0.45f) / 0.55f;
        Vec3 wind = WindDirection.getHorizontal(level());

        Vec3 v = owner.getDeltaMovement();

        double drift = 0.035 * pull;
        owner.setDeltaMovement(
                v.x + wind.x * drift,
                v.y,
                v.z + wind.z * drift);

        if (!owner.onGround() && owner.getDeltaMovement().y > 0) {
            Vec3 cur = owner.getDeltaMovement();
            owner.setDeltaMovement(cur.x, cur.y + 0.045 * pull, cur.z);
        }

        if (owner.fallDistance > 0 && intensity > 0.45f) {
            owner.fallDistance = Math.min(owner.fallDistance, 3.0f);
        }

        owner.hurtMarked = true;
    }

    public Vec3 windDirection() { return WindDirection.getHorizontal(level()); }
    public float windIntensity() { return WindDirection.getIntensity((ServerLevel) level()); }

    public Player getOwnerPlayer() { return owner(); }

    @Override protected void readAdditionalSaveData(@NotNull CompoundTag tag) {}
    @Override protected void addAdditionalSaveData(@NotNull CompoundTag tag) {}
}
