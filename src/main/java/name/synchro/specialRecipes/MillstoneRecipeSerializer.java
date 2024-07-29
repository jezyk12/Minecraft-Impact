package name.synchro.specialRecipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import name.synchro.Synchro;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;

public final class MillstoneRecipeSerializer implements RecipeSerializer<MillstoneRecipe> {
    public static final MillstoneRecipeSerializer INSTANCE = new MillstoneRecipeSerializer();
    public static final Identifier ID = Identifier.of(Synchro.MOD_ID, "millstone");
    public static final MapCodec<MillstoneRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.ALLOW_EMPTY_CODEC.fieldOf("input").forGetter(MillstoneRecipe::input),
            ItemStack.CODEC.fieldOf("output").forGetter(MillstoneRecipe::output),
            Codec.INT.fieldOf("degrees").forGetter(MillstoneRecipe::degrees),
            Codec.BOOL.optionalFieldOf("copy_nbt", false).forGetter(MillstoneRecipe::copyNbt))
            .apply(instance, MillstoneRecipe::new));
    public static final PacketCodec<RegistryByteBuf, MillstoneRecipe> PACKET_CODEC =
            PacketCodec.ofStatic(MillstoneRecipeSerializer::encodeBuf, MillstoneRecipeSerializer::decodeBuf);
    @Override
    public MapCodec<MillstoneRecipe> codec() {
        return CODEC;
    }

    @Override
    public PacketCodec<RegistryByteBuf, MillstoneRecipe> packetCodec() {
        return PACKET_CODEC;
    }

    private static MillstoneRecipe decodeBuf(RegistryByteBuf buf) {
        Ingredient input = Ingredient.PACKET_CODEC.decode(buf);
        ItemStack output = ItemStack.PACKET_CODEC.decode(buf);
        int degrees = buf.readInt();
        boolean copyNbt = buf.readBoolean();
        return new MillstoneRecipe(input, output, degrees, copyNbt);
    }

    private static void encodeBuf(RegistryByteBuf buf, MillstoneRecipe recipe) {
        Ingredient.PACKET_CODEC.encode(buf, recipe.input());
        ItemStack.PACKET_CODEC.encode(buf, recipe.output());
        buf.writeInt(recipe.degrees());
        buf.writeBoolean(recipe.copyNbt());
    }
}
