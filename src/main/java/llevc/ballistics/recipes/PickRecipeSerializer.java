package llevc.ballistics.recipes;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;

public class PickRecipeSerializer implements RecipeSerializer<PickRecipe> {
    public static final PickRecipeSerializer INSTANCE = new PickRecipeSerializer(PickRecipe::new);
    public static final Identifier ID = Identifier.fromNamespaceAndPath("ballistics","crafting_special_pick");



    private final MapCodec<PickRecipe> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, PickRecipe> streamCodec;

    public PickRecipeSerializer(CustomRecipe.Serializer.Factory<PickRecipe> factory) {
        this.codec = RecordCodecBuilder.mapCodec(
                instance -> instance.group(CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(CraftingRecipe::category))
                        .apply(instance, factory::create)
        );
        this.streamCodec = StreamCodec.composite(CraftingBookCategory.STREAM_CODEC, CraftingRecipe::category, factory::create);
    }

    @Override
    public @NotNull MapCodec<PickRecipe> codec() {
        return codec;
    }

    @Override
    public @NotNull StreamCodec<RegistryFriendlyByteBuf, PickRecipe> streamCodec() {
        return streamCodec;
    }
}
