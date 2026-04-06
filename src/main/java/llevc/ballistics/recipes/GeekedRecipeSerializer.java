package llevc.ballistics.recipes;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;

public class GeekedRecipeSerializer implements RecipeSerializer<GeekedRecipe> {
    public static final GeekedRecipeSerializer INSTANCE = new GeekedRecipeSerializer(GeekedRecipe::new);
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("ballistics","crafting_special_geeked");



    private final MapCodec<GeekedRecipe> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, GeekedRecipe> streamCodec;

    public GeekedRecipeSerializer(CustomRecipe.Serializer.Factory<GeekedRecipe> factory) {
        this.codec = RecordCodecBuilder.mapCodec(
                instance -> instance.group(CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(CraftingRecipe::category))
                        .apply(instance, factory::create)
        );
        this.streamCodec = StreamCodec.composite(CraftingBookCategory.STREAM_CODEC, CraftingRecipe::category, factory::create);
    }

    @Override
    public @NotNull MapCodec<GeekedRecipe> codec() {
        return codec;
    }

    @Override
    public @NotNull StreamCodec<RegistryFriendlyByteBuf, GeekedRecipe> streamCodec() {
        return streamCodec;
    }
}
