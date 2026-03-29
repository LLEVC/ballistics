package llevc.ballistics.recipes;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.*;
import org.jetbrains.annotations.NotNull;

public class FlaskRecipeSerializer implements RecipeSerializer<FlaskRecipe> {
    public static final FlaskRecipeSerializer INSTANCE = new FlaskRecipeSerializer(FlaskRecipe::new);
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("ballistics","crafting_special_flask");



    private final MapCodec<FlaskRecipe> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, FlaskRecipe> streamCodec;

    public FlaskRecipeSerializer(CustomRecipe.Serializer.Factory<FlaskRecipe> factory) {
        this.codec = RecordCodecBuilder.mapCodec(
                instance -> instance.group(CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(CraftingRecipe::category))
                        .apply(instance, factory::create)
        );
        this.streamCodec = StreamCodec.composite(CraftingBookCategory.STREAM_CODEC, CraftingRecipe::category, factory::create);
    }

    @Override
    public @NotNull MapCodec<FlaskRecipe> codec() {
        return codec;
    }

    @Override
    public @NotNull StreamCodec<RegistryFriendlyByteBuf, FlaskRecipe> streamCodec() {
        return streamCodec;
    }
}
