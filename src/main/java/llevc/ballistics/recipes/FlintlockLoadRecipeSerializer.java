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

public class FlintlockLoadRecipeSerializer implements RecipeSerializer<FlintlockLoadRecipe> {
    public static final FlintlockLoadRecipeSerializer INSTANCE = new FlintlockLoadRecipeSerializer(FlintlockLoadRecipe::new);
    public static final Identifier ID = Identifier.fromNamespaceAndPath("ballistics","crafting_special_flintlock_load");



    private final MapCodec<FlintlockLoadRecipe> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, FlintlockLoadRecipe> streamCodec;

    public FlintlockLoadRecipeSerializer(CustomRecipe.Serializer.Factory<FlintlockLoadRecipe> factory) {
        this.codec = RecordCodecBuilder.mapCodec(
                instance -> instance.group(CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(CraftingRecipe::category))
                        .apply(instance, factory::create)
        );
        this.streamCodec = StreamCodec.composite(CraftingBookCategory.STREAM_CODEC, CraftingRecipe::category, factory::create);
    }

    @Override
    public @NotNull MapCodec<FlintlockLoadRecipe> codec() {
        return codec;
    }

    @Override
    public @NotNull StreamCodec<RegistryFriendlyByteBuf, FlintlockLoadRecipe> streamCodec() {
        return streamCodec;
    }
}
