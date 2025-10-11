package dev.tazer.post_mortem.gen;

import dev.tazer.post_mortem.PostMortem;
import dev.tazer.post_mortem.registry.PMBlocks;
import dev.tazer.post_mortem.registry.PMItems;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PostMortemGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();

        pack.addProvider(LangGenerator::new);
    }

    private static class LangGenerator extends FabricLanguageProvider {
        protected LangGenerator(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
            super(dataOutput, registryLookup);
        }

        @Override
        public void generateTranslations(HolderLookup.Provider provider, TranslationBuilder translationBuilder) {
            for (Block definition : PMBlocks.BLOCKS) {
                translationBuilder.add(langKey(definition), langName(definition.builtInRegistryHolder()));
            }

            for (Item definition : PMItems.ITEMS) {
                if (!(definition instanceof BlockItem)) {
                    translationBuilder.add(langKey(definition), langName(definition.builtInRegistryHolder()));
                }
            }

            translationBuilder.add(PostMortem.MOD_ID + ".itemGroup.tab", "Post Mortem");
            translationBuilder.add("death.attack." + PostMortem.MOD_ID + ".bleed", "%1$s bled out");
            translationBuilder.add("death.attack." + PostMortem.MOD_ID + ".bleed.player", "%1$s bled out");

            translationBuilder.add(PostMortem.MOD_ID + ".anchor_type.spawn_point", "Haunt Spawn Point");
            translationBuilder.add(PostMortem.MOD_ID + ".anchor_type.death_point", "Haunt Death Point");
            translationBuilder.add(PostMortem.MOD_ID + ".anchor_type.gravestone", "Haunt Gravestone");
        }

        private static String langKey(Block block) {
            return "block." + langKey(block.builtInRegistryHolder());
        }

        private static String langKey(Item item) {
            return "item." + langKey(item.builtInRegistryHolder());
        }

        private static String langKey(Holder<?> holder) {
            return holder.getRegisteredName().replaceAll(":", ".");
        }

        private static String langName(Holder<?> holder) {
            String processed = holder.getRegisteredName().split(":")[1].replace("_", " ");

            List<String> nonCapital = List.of("of", "and", "with");

            String[] words = processed.split(" ");
            StringBuilder result = new StringBuilder();

            for (String word : words) {
                if (!word.isEmpty()) {
                    if (!nonCapital.contains(word)) result.append(Character.toUpperCase(word.charAt(0)));
                    else result.append(word.charAt(0));
                    result.append(word.substring(1)).append(" ");
                }
            }

            return result.toString().trim();
        }
    }
}
