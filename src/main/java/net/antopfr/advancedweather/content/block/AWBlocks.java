package net.antopfr.advancedweather.content.block;

import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.antopfr.advancedweather.content.block.archive.WeatherArchiveBlock;
import net.antopfr.advancedweather.content.block.archive.WeatherArchiveBlockEntity;
import net.antopfr.advancedweather.content.block.autosampler.AutoSamplerBlock;
import net.antopfr.advancedweather.content.block.autosampler.AutoSamplerBlockEntity;
import net.antopfr.advancedweather.content.block.calibration.CalibrationBenchBlock;
import net.antopfr.advancedweather.content.block.detector.WeatherDetectorBlock;
import net.antopfr.advancedweather.content.block.sensor.anemometer.AnemometerBlock;
import net.antopfr.advancedweather.content.block.sensor.anemometer.AnemometerBlockEntity;
import net.antopfr.advancedweather.content.block.sensor.barometer.BarometerBlock;
import net.antopfr.advancedweather.content.block.sensor.barometer.BarometerBlockEntity;
import net.antopfr.advancedweather.content.block.sensor.hygrometer.HygrometerBlock;
import net.antopfr.advancedweather.content.block.sensor.hygrometer.HygrometerBlockEntity;
import net.antopfr.advancedweather.content.block.sensor.thermometer.ThermometerBlock;
import net.antopfr.advancedweather.content.block.sensor.thermometer.ThermometerBlockEntity;
import net.antopfr.advancedweather.content.block.station.WeatherStationBlockEntity;
import net.antopfr.advancedweather.content.item.AWItems;
import net.antopfr.advancedweather.util.AWRegistrate;
import net.antopfr.advancedweather.content.block.station.WeatherStationBlock;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;
import net.neoforged.neoforge.common.Tags;

@SuppressWarnings("unused")
public class AWBlocks {

    public static final BlockEntry<WeatherStationBlock> WEATHER_STATION =
            AWRegistrate.get()
                    .block("weather_station", WeatherStationBlock::new)
                    .properties(p -> p
                            .mapColor(MapColor.METAL)
                            .strength(2.0f)
                            .sound(SoundType.COPPER)
                            .requiresCorrectToolForDrops())
                    .blockstate((ctx, prov) -> {
                        var model = prov.models().getBuilder(ctx.getName())
                                .parent(prov.models().getExistingFile(prov.mcLoc("block/block")))
                                .texture("particle", prov.modLoc("block/weather_station_side"))
                                .texture("front", prov.modLoc("block/weather_station_front"))
                                .texture("back", prov.modLoc("block/sensor_casing_fans"))
                                .texture("side", prov.modLoc("block/weather_station_side"))
                                .texture("side_mirrored", prov.modLoc("block/weather_station_side_mirrored"))
                                .element()
                                .from(0, 0, 0).to(16, 16, 16)
                                .face(Direction.NORTH).texture("#front").end()
                                .face(Direction.SOUTH).texture("#back").end()
                                .face(Direction.EAST).texture("#side_mirrored").end()
                                .face(Direction.WEST).texture("#side").end()
                                .face(Direction.UP).texture("#side")
                                .rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end()
                                .face(Direction.DOWN).texture("#side")
                                .rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end()
                                .end();
                        prov.horizontalBlock(ctx.get(), model);
                    })
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .simpleItem()
                    .register();

    public static final BlockEntityEntry<WeatherStationBlockEntity> WEATHER_STATION_BE =
            AWRegistrate.get()
                    .blockEntity("weather_station", WeatherStationBlockEntity::new)
                    .validBlock(WEATHER_STATION)
                    .register();

    public static final BlockEntry<WeatherArchiveBlock> WEATHER_ARCHIVE =
            AWRegistrate.get()
                    .block("weather_archive", WeatherArchiveBlock::new)
                    .properties(p -> p.mapColor(MapColor.METAL)
                            .strength(2.5f)
                            .sound(SoundType.COPPER)
                            .requiresCorrectToolForDrops())
                    .blockstate((ctx, prov) -> {
                        var model = prov.models().getBuilder(ctx.getName())
                                .parent(prov.models().getExistingFile(prov.mcLoc("block/block")))
                                .texture("particle", prov.modLoc("block/weather_archive_side"))
                                .texture("front", prov.modLoc("block/weather_archive_front"))
                                .texture("back", prov.modLoc("block/sensor_casing_fans"))
                                .texture("side", prov.modLoc("block/weather_archive_side"))
                                .texture("side_mirrored", prov.modLoc("block/weather_archive_side_mirrored"))
                                .element()
                                .from(0, 0, 0).to(16, 16, 16)
                                .face(Direction.NORTH).texture("#front").end()
                                .face(Direction.SOUTH).texture("#back").end()
                                .face(Direction.EAST).texture("#side_mirrored").end()
                                .face(Direction.WEST).texture("#side").end()
                                .face(Direction.UP).texture("#side")
                                .rotation(ModelBuilder.FaceRotation.CLOCKWISE_90).end()
                                .face(Direction.DOWN).texture("#side")
                                .rotation(ModelBuilder.FaceRotation.COUNTERCLOCKWISE_90).end()
                                .end();
                        prov.horizontalBlock(ctx.get(), model);
                    })
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .simpleItem()
                    .register();

    public static final BlockEntityEntry<WeatherArchiveBlockEntity> WEATHER_ARCHIVE_BE =
            AWRegistrate.get()
                    .blockEntity("weather_archive", WeatherArchiveBlockEntity::new)
                    .validBlock(WEATHER_ARCHIVE)
                    .register();

    public static final BlockEntry<WeatherDetectorBlock> WEATHER_DETECTOR =
            AWRegistrate.get()
                    .block("weather_detector", WeatherDetectorBlock::new)
                    .properties(p -> p.mapColor(MapColor.METAL)
                            .strength(1.5f)
                            .sound(SoundType.COPPER))
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .simpleItem()
                    .register();

    public static final BlockEntry<Block> SENSOR_CASING =
            AWRegistrate.get()
                    .block("sensor_casing", Block::new)
                    .properties(p -> p
                            .mapColor(MapColor.METAL)
                            .strength(1.5f)
                            .sound(SoundType.COPPER))
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .simpleItem()
                    .register();

    // INSTRUMENTS
    public static final BlockEntry<BarometerBlock> BAROMETER =
            AWRegistrate.get()
                    .block("barometer", BarometerBlock::new)
                    .properties(p -> p.mapColor(MapColor.METAL)
                            .strength(1.5f)
                            .sound(SoundType.COPPER))
                    .blockstate((ctx, prov) -> {})
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .simpleItem()
                    .register();

    public static final BlockEntityEntry<BarometerBlockEntity> BAROMETER_BE =
            AWRegistrate.get()
                    .blockEntity("barometer", BarometerBlockEntity::new)
                    .validBlock(BAROMETER)
                    .register();

    public static final BlockEntry<ThermometerBlock> THERMOMETER =
            AWRegistrate.get()
                    .block("thermometer", ThermometerBlock::new)
                    .properties(p -> p.mapColor(MapColor.METAL)
                            .strength(1.5f)
                            .sound(SoundType.COPPER))
                    .blockstate((ctx, prov) -> {})
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .simpleItem()
                    .register();

    public static final BlockEntityEntry<ThermometerBlockEntity> THERMOMETER_BE =
            AWRegistrate.get()
                    .blockEntity("thermometer", ThermometerBlockEntity::new)
                    .validBlock(THERMOMETER)
                    .register();

    public static final BlockEntry<HygrometerBlock> HYGROMETER =
            AWRegistrate.get()
                    .block("hygrometer", HygrometerBlock::new)
                    .properties(p -> p.mapColor(MapColor.METAL)
                            .strength(1.5f)
                            .sound(SoundType.COPPER))
                    .blockstate((ctx, prov) -> {})
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .simpleItem()
                    .register();

    public static final BlockEntityEntry<HygrometerBlockEntity> HYGROMETER_BE =
            AWRegistrate.get()
                    .blockEntity("hygrometer", HygrometerBlockEntity::new)
                    .validBlock(HYGROMETER)
                    .register();

    public static final BlockEntry<AnemometerBlock> ANEMOMETER =
            AWRegistrate.get()
                    .block("anemometer", AnemometerBlock::new)
                    .properties(p -> p.mapColor(MapColor.METAL)
                            .strength(1.5f)
                            .sound(SoundType.COPPER))
                    .blockstate((ctx, prov) -> {})
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .simpleItem()
                    .register();

    public static final BlockEntityEntry<AnemometerBlockEntity> ANEMOMETER_BE =
            AWRegistrate.get()
                    .blockEntity("anemometer", AnemometerBlockEntity::new)
                    .validBlock(ANEMOMETER)
                    .register();

    public static final BlockEntry<AutoSamplerBlock> AUTO_SAMPLER =
            AWRegistrate.get()
                    .block("auto_sampler", AutoSamplerBlock::new)
                    .properties(p -> p.mapColor(MapColor.METAL)
                            .strength(2.0f)
                            .sound(SoundType.COPPER))
                    .blockstate((ctx, prov) -> {})
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .item()
                    .model((ctx, prov) -> {})
                    .build()
                    .register();

    public static final BlockEntityEntry<AutoSamplerBlockEntity> AUTO_SAMPLER_BE =
            AWRegistrate.get()
                    .blockEntity("auto_sampler", AutoSamplerBlockEntity::new)
                    .validBlock(AUTO_SAMPLER)
                    .register();

    public static final BlockEntry<CalibrationBenchBlock> CALIBRATION_BENCH =
            AWRegistrate.get()
                    .block("calibration_bench", CalibrationBenchBlock::new)
                    .properties(p -> p.mapColor(MapColor.WOOD)
                            .strength(1.5f)
                            .sound(SoundType.WOOD)
                            .noOcclusion())
                    .blockstate((ctx, prov) -> {})
                    .addLayer(() -> RenderType::cutout)
                    .tag(BlockTags.MINEABLE_WITH_AXE)
                    .item()
                    .model((ctx, prov) -> {})
                    .build()
                    .register();

    public static final BlockEntry<Block> CINNABAR_ORE =
            AWRegistrate.get()
                    .block("cinnabar_ore", Block::new)
                    .properties(p -> p.mapColor(MapColor.TERRACOTTA_RED)
                            .strength(3.0f, 3.0f).requiresCorrectToolForDrops()
                            .sound(SoundType.STONE))
                    .loot((lt, b) -> lt.add(b, lt.createOreDrop(b, AWItems.RAW_CINNABAR.get())))
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL)
                    .simpleItem()
                    .register();

    public static final BlockEntry<Block> DEEPSLATE_CINNABAR_ORE =
            AWRegistrate.get()
                    .block("deepslate_cinnabar_ore", Block::new)
                    .properties(p -> p.mapColor(MapColor.DEEPSLATE)
                            .strength(4.5f, 3.0f).requiresCorrectToolForDrops()
                            .sound(SoundType.DEEPSLATE))
                    .loot((lt, b) -> lt.add(b, lt.createOreDrop(b, AWItems.RAW_CINNABAR.get())))
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL)
                    .simpleItem()
                    .register();

    public static final BlockEntry<Block> RAW_CINNABAR_BLOCK =
            AWRegistrate.get()
                    .block("raw_cinnabar_block", Block::new)
                    .properties(p -> p.mapColor(MapColor.COLOR_RED)
                            .strength(5.0f, 6.0f).requiresCorrectToolForDrops())
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE, Tags.Blocks.STORAGE_BLOCKS, BlockTags.NEEDS_IRON_TOOL)
                    .simpleItem()
                    .register();

    public static final BlockEntry<Block> CINNABAR_BLOCK =
            AWRegistrate.get()
                    .block("cinnabar_block", Block::new)
                    .properties(p -> p.mapColor(MapColor.COLOR_RED)
                            .strength(5.0f, 6.0f).requiresCorrectToolForDrops())
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE, Tags.Blocks.STORAGE_BLOCKS, BlockTags.NEEDS_IRON_TOOL)
                    .simpleItem()
                    .register();

    public static final BlockEntry<Block> BAUXITE_SOIL = AWRegistrate.get()
            .block("bauxite_soil", Block::new)
            .properties(p -> p.mapColor(MapColor.TERRACOTTA_RED)
                    .strength(0.6f)
                    .sound(SoundType.GRAVEL))
            .tag(BlockTags.MINEABLE_WITH_SHOVEL)
            .simpleItem()
            .register();

    public static final BlockEntry<Block> BAUXITE_ROCK = AWRegistrate.get()
            .block("bauxite_rock", Block::new)
            .properties(p -> p.mapColor(MapColor.TERRACOTTA_RED)
                    .strength(2.5f, 2.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE))
            .tag(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL)
            .simpleItem()
            .register();

    public static final BlockEntry<Block> ALUMINUM_BLOCK = AWRegistrate.get()
            .block("aluminum_block", Block::new)
            .properties(p -> p.mapColor(MapColor.METAL)
                    .strength(4.0f, 6.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL))
            .tag(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_STONE_TOOL)
            .simpleItem()
            .register();

    public static final BlockEntry<MercuryCauldronBlock> MERCURY_CAULDRON =
            AWRegistrate.get()
                    .block("mercury_cauldron", MercuryCauldronBlock::new)
                    .initialProperties(() -> Blocks.WATER_CAULDRON)
                    .blockstate((ctx, prov) -> {})
                    .loot((lt, block) -> lt.dropOther(block, Items.CAULDRON))
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .register();

    public static void register() {}
}
