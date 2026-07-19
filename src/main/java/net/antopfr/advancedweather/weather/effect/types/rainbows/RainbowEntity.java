package net.antopfr.advancedweather.weather.effect.types.rainbows;

import net.antopfr.advancedweather.content.entity.AWEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class RainbowEntity extends Entity {

    private static final EntityDataAccessor<Float> START_X =
            SynchedEntityData.defineId(RainbowEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> START_Y =
            SynchedEntityData.defineId(RainbowEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> START_Z =
            SynchedEntityData.defineId(RainbowEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> END_X =
            SynchedEntityData.defineId(RainbowEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> END_Y =
            SynchedEntityData.defineId(RainbowEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> END_Z =
            SynchedEntityData.defineId(RainbowEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> INTENSITY =
            SynchedEntityData.defineId(RainbowEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> GROW_SCALE =
            SynchedEntityData.defineId(RainbowEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> ANTISUN_X =
            SynchedEntityData.defineId(RainbowEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> ANTISUN_Y =
            SynchedEntityData.defineId(RainbowEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> ANTISUN_Z =
            SynchedEntityData.defineId(RainbowEntity.class, EntityDataSerializers.FLOAT);

    private static final int MIN_LIFETIME    = 3600;
    private static final int MAX_LIFETIME    = 7200;
    private static final int FADE_IN_TICKS   = 100;
    private static final int FADE_OUT_TICKS  = 200;
    private static final int NIGHT_FADE_TICKS = 60;

    private int lifetimeTicks;
    private int age = 0;
    private boolean nightFading = false;
    private int nightFadeAge = 0;

    public RainbowEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.lifetimeTicks = MIN_LIFETIME + level.getRandom().nextInt(MAX_LIFETIME - MIN_LIFETIME);
    }

    public RainbowEntity(Level level) {
        this(AWEntities.RAINBOW.get(), level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(START_X, 0f);
        builder.define(START_Y, 0f);
        builder.define(START_Z, 0f);
        builder.define(END_X, 0f);
        builder.define(END_Y, 0f);
        builder.define(END_Z, 0f);
        builder.define(INTENSITY, 0f);
        builder.define(GROW_SCALE, 0f);
        builder.define(ANTISUN_X, 0f);
        builder.define(ANTISUN_Y, 1f);
        builder.define(ANTISUN_Z, 0f);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) return;

        age++;

        long dayTime = level().getDayTime() % 24000;
        boolean isNight = dayTime > 13000 && dayTime < 23000;

        if (isNight && !nightFading) {
            nightFading = true;
            nightFadeAge = 0;
        }

        if (nightFading) {
            nightFadeAge++;
            float fade = Math.max(0f, 1f - nightFadeAge / (float) NIGHT_FADE_TICKS);
            setIntensity(fade);
            setGrowScale(Math.max(getGrowScale(), fade));
            if (nightFadeAge >= NIGHT_FADE_TICKS) {
                this.discard();
            }
            return;
        }

        int remaining = lifetimeTicks - age;

        if (age <= FADE_IN_TICKS) {
            float progress = Mth.clamp(age / (float) FADE_IN_TICKS, 0f, 1f);
            setIntensity(progress);
            setGrowScale(1f - (1f - progress) * (1f - progress));
        } else if (remaining <= FADE_OUT_TICKS) {
            setIntensity(Math.max(0f, remaining / (float) FADE_OUT_TICKS));
            setGrowScale(1f);
        } else {
            setIntensity(1f);
            setGrowScale(1f);
        }

        if (age >= lifetimeTicks) {
            this.discard();
        }
    }

    public void setEndpoints(Vec3 start, Vec3 end) {
        entityData.set(START_X, (float) start.x);
        entityData.set(START_Y, (float) start.y);
        entityData.set(START_Z, (float) start.z);
        entityData.set(END_X, (float) end.x);
        entityData.set(END_Y, (float) end.y);
        entityData.set(END_Z, (float) end.z);
    }

    public Vec3 getStart() {
        return new Vec3(entityData.get(START_X), entityData.get(START_Y), entityData.get(START_Z));
    }

    public Vec3 getEnd() {
        return new Vec3(entityData.get(END_X), entityData.get(END_Y), entityData.get(END_Z));
    }

    public float getIntensity() { return entityData.get(INTENSITY); }
    public void setIntensity(float intensity) { entityData.set(INTENSITY, intensity); }

    public float getGrowScale() { return entityData.get(GROW_SCALE); }
    public void setGrowScale(float scale) { entityData.set(GROW_SCALE, scale); }

    public void setAntiSunDirection(Vec3 dir) {
        entityData.set(ANTISUN_X, (float) dir.x);
        entityData.set(ANTISUN_Y, (float) dir.y);
        entityData.set(ANTISUN_Z, (float) dir.z);
    }

    public Vec3 getAntiSunDirection() {
        return new Vec3(entityData.get(ANTISUN_X), entityData.get(ANTISUN_Y), entityData.get(ANTISUN_Z));
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        entityData.set(START_X, tag.getFloat("sx"));
        entityData.set(START_Y, tag.getFloat("sy"));
        entityData.set(START_Z, tag.getFloat("sz"));
        entityData.set(END_X, tag.getFloat("ex"));
        entityData.set(END_Y, tag.getFloat("ey"));
        entityData.set(END_Z, tag.getFloat("ez"));
        entityData.set(INTENSITY, tag.getFloat("intensity"));
        entityData.set(GROW_SCALE, tag.getFloat("grow_scale"));
        entityData.set(ANTISUN_X, tag.getFloat("anti_sun_x"));
        entityData.set(ANTISUN_Y, tag.getFloat("anti_sun_y"));
        entityData.set(ANTISUN_Z, tag.getFloat("anti_sun_z"));
        age = tag.getInt("age");
        lifetimeTicks = tag.getInt("lifetime");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putFloat("sx", entityData.get(START_X));
        tag.putFloat("sy", entityData.get(START_Y));
        tag.putFloat("sz", entityData.get(START_Z));
        tag.putFloat("ex", entityData.get(END_X));
        tag.putFloat("ey", entityData.get(END_Y));
        tag.putFloat("ez", entityData.get(END_Z));
        tag.putFloat("intensity", entityData.get(INTENSITY));
        tag.putFloat("grow_scale", entityData.get(GROW_SCALE));
        tag.putFloat("anti_sun_x", entityData.get(ANTISUN_X));
        tag.putFloat("anti_sun_y", entityData.get(ANTISUN_Y));
        tag.putFloat("anti_sun_z", entityData.get(ANTISUN_Z));
        tag.putInt("age", age);
        tag.putInt("lifetime", lifetimeTicks);
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 4096.0 * 4096.0;
    }

    @Override
    public boolean isPickable() { return false; }

    @Override
    public boolean isAttackable() { return false; }

    @Override
    public boolean canBeCollidedWith() { return false; }
}