package net.antopfr.advancedweather.client.cloud;

import com.mojang.blaze3d.vertex.*;
import net.antopfr.advancedweather.util.OpenSimplex2;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class CloudMeshBuilder {

    private static final long NOISE_SEED = 0xC10DL;
    private static final double NOISE_SCALE = 0.055;

    private static final int TOP    = 0xFFFFFF;
    private static final int BOTTOM = 0xB4B4B4;
    private static final int SIDE_X = 0xE0E0E0;
    private static final int SIDE_Z = 0xCCCCCC;
    private static final float ALPHA = 0.82f;

    private static final float FADE_BAND = 0.06f;
    private static final float MIN_HEIGHT_RATIO = 0.35f;

    private static final float SIDE_THRESHOLD = 0.8f;

    private static final Vec3 EMBER = new Vec3(1.0, 0.52, 0.18);

    public static float density(int cellX, int cellZ) {
        double a = OpenSimplex2.noise2(NOISE_SEED, cellX * NOISE_SCALE, cellZ * NOISE_SCALE);
        double b = OpenSimplex2.noise2(NOISE_SEED + 31L,
                cellX * NOISE_SCALE * 2.7, cellZ * NOISE_SCALE * 2.7);
        double v = a * 0.68 + b * 0.32;
        return (float) ((v + 1.0) * 0.5);
    }

    private static float heightRatio(float density, float threshold) {
        float depth = Mth.clamp((density - threshold) / 0.25f, 0f, 1f);
        return MIN_HEIGHT_RATIO + depth * (1f - MIN_HEIGHT_RATIO);
    }

    private static float cellScale(int cellX, int cellZ, float threshold) {
        float d = density(cellX, cellZ);
        if (d <= threshold) return 0f;
        return Mth.clamp((d - threshold) / FADE_BAND, 0f, 1f);
    }

    private static float shade(float density, float threshold) {
        float depth = Mth.clamp((density - threshold) / 0.25f, 0f, 1f);
        return 0.82f + depth * 0.18f;
    }

    private static float faceLight(float nx, float ny, float nz, float sunAngle) {
        double a = sunAngle * Math.PI * 2.0;
        double sx = Math.cos(a);
        double sy = Math.sin(a);

        double dot = nx * sx + ny * sy;
        return (float) Mth.clamp(0.72 + dot * 0.28, 0.70, 1.0);
    }

    private static float sunGlow(float dx, float dz, float cellY, Vec3 sunDir) {
        double len = Math.sqrt(dx * dx + cellY * cellY + dz * dz);
        if (len < 0.001) return 0f;

        double dot = (dx * sunDir.x + cellY * sunDir.y + dz * sunDir.z) / len;
        if (dot <= 0) return 0f;

        return (float) Math.pow(dot, 6.0);
    }

    private static boolean occludes(int cellX, int cellZ, float threshold, float myBottom) {
        float d = density(cellX, cellZ);
        if (cellScale(cellX, cellZ, threshold) < 0.99f) return false;
        float neighbourBottom = CloudState.CELL_HEIGHT
                - CloudState.CELL_HEIGHT * heightRatio(d, threshold);
        return neighbourBottom <= myBottom + 0.01f;
    }

    private static boolean sideVisible(int cellX, int cellZ, float threshold, float myBottom) {
        if (cellScale(cellX, cellZ, threshold) < 0.01f) return true;
        float d = density(cellX, cellZ);
        float nb = CloudState.CELL_HEIGHT
                - CloudState.CELL_HEIGHT * heightRatio(d, threshold);
        return nb - myBottom > SIDE_THRESHOLD;
    }

    public static MeshData build(int centerCellX, int centerCellZ, int radiusCells, float threshold) {
        BufferBuilder buf = Tesselator.getInstance()
                .begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        float h = CloudState.CELL_HEIGHT;
        float s = CloudState.CELL_SIZE;

        float sunAngle = CloudState.sunAngle();

        float lTop    = faceLight(0, 1, 0, sunAngle);
        float lBottom = faceLight(0, -1, 0, sunAngle);
        float lNorth  = faceLight(0, 0, -1, sunAngle);
        float lSouth  = faceLight(0, 0, 1, sunAngle);
        float lWest   = faceLight(-1, 0, 0, sunAngle);
        float lEast   = faceLight(1, 0, 0, sunAngle);

        for (int dz = -radiusCells; dz <= radiusCells; dz++) {
            for (int dx = -radiusCells; dx <= radiusCells; dx++) {

                if (dx * dx + dz * dz > radiusCells * radiusCells) continue;

                int cx = centerCellX + dx;
                int cz = centerCellZ + dz;
                float d = density(cx, cz);

                float scale = cellScale(cx, cz, threshold);
                if (scale <= 0.01f) continue;

                float sh = shade(d, threshold);
                float cellH = h * heightRatio(d, threshold);

                float inset = (1f - scale) * 0.5f * s;
                float x0 = dx * s + inset, x1 = dx * s + s - inset;
                float z0 = dz * s + inset, z1 = dz * s + s - inset;
                float y1 = h;
                float y0 = h - cellH * scale;

                quad(buf, x0, y1, z0, x1, y1, z0, x1, y1, z1, x0, y1, z1, sh * lTop);
                quad(buf, x0, y0, z1, x1, y0, z1, x1, y0, z0, x0, y0, z0, sh * lBottom);

                if (!occludes(cx, cz - 1, threshold, y0) && sideVisible(cx, cz - 1, threshold, y0))
                    quad(buf, x0, y0, z0, x1, y0, z0, x1, y1, z0, x0, y1, z0, sh * lNorth);
                if (!occludes(cx, cz + 1, threshold, y0) && sideVisible(cx, cz + 1, threshold, y0))
                    quad(buf, x1, y0, z1, x0, y0, z1, x0, y1, z1, x1, y1, z1, sh * lSouth);
                if (!occludes(cx - 1, cz, threshold, y0) && sideVisible(cx - 1, cz, threshold, y0))
                    quad(buf, x0, y0, z1, x0, y0, z0, x0, y1, z0, x0, y1, z1, sh * lWest);
                if (!occludes(cx + 1, cz, threshold, y0) && sideVisible(cx + 1, cz, threshold, y0))
                    quad(buf, x1, y0, z0, x1, y0, z1, x1, y1, z1, x1, y1, z0, sh * lEast);
            }
        }

        return buf.build();
    }

    private static void quad(BufferBuilder buf,
                             float ax, float ay, float az, float bx, float by, float bz,
                             float cx, float cy, float cz, float dx, float dy, float dz,
                             float light) {
        int v = (int) (Mth.clamp(light, 0f, 1f) * 255);
        int a = (int) (ALPHA * 255);
        buf.addVertex(ax, ay, az).setColor(v, v, v, a);
        buf.addVertex(bx, by, bz).setColor(v, v, v, a);
        buf.addVertex(cx, cy, cz).setColor(v, v, v, a);
        buf.addVertex(dx, dy, dz).setColor(v, v, v, a);
    }
}
