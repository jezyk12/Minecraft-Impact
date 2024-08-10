package name.synchro.modUtilData.dataEntries;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import name.synchro.Synchro;
import name.synchro.modUtilData.ModDataContainer;
import name.synchro.modUtilData.reactions.LocationAction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.*;

public class FluidReactionData implements ModDataContainer<Map<Long, FluidReactionData.Entry>> {
    public static final String FOLDER = "fluid_reaction";
    public static final Identifier ID = Identifier.of(Synchro.MOD_ID, FOLDER);
    private Map<Long, Entry> entries = new HashMap<>();

    public static long longKey(Fluid fluid, Block block){
        int f = Registries.FLUID.getRawId(fluid);
        int b = Registries.BLOCK.getRawId(block);
        return ((long) f << 32) | b;
    }

    @Override
    public Map<Long, Entry> data() {
        return entries;
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public void deserialize(JsonElement json, Identifier fileName) {
        try {
            DataResult<Entry> result = Entry.CODEC.codec().parse(JsonOps.INSTANCE, json);
            Entry entry = result.getOrThrow();
            this.data().put(longKey(entry.fluid(), entry.block()), entry);
        } catch (Exception e) {
            Synchro.LOGGER.error("Unable to parse {}/{}: ", FOLDER, fileName, e);
        }
    }

    @Override
    public void readBuf(RegistryByteBuf buf) {
        Map<Long, Entry> entries = new HashMap<>();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            Fluid fluid = PacketCodecs.registryValue(RegistryKeys.FLUID).decode(buf);
            Optional<StatePredicate> fluidStatePredicate = PacketCodecs.optional(StatePredicate.PACKET_CODEC).decode(buf);
            Block block = PacketCodecs.registryValue(RegistryKeys.BLOCK).decode(buf);
            Optional<StatePredicate> blockStatePredicate = PacketCodecs.optional(StatePredicate.PACKET_CODEC).decode(buf);
            List<LocationAction> actions = PacketCodecs.codec(Codec.list(LocationAction.CODEC)).decode(buf);
            entries.put(longKey(fluid, block), new Entry(fluid, fluidStatePredicate, block, blockStatePredicate, actions));
        }
        this.entries = entries;
    }

    @Override
    public void writeBuf(RegistryByteBuf buf) {
        Collection<Entry> entrySet = this.data().values();
        buf.writeInt(entrySet.size());
        for (Entry entry : entrySet){
            PacketCodecs.registryValue(RegistryKeys.FLUID).encode(buf, entry.fluid());
            PacketCodecs.optional(StatePredicate.PACKET_CODEC).encode(buf, entry.fluidStatePredicate());
            PacketCodecs.registryValue(RegistryKeys.BLOCK).encode(buf, entry.block());
            PacketCodecs.optional(StatePredicate.PACKET_CODEC).encode(buf, entry.fluidStatePredicate());
            PacketCodecs.codec(Codec.list(LocationAction.CODEC)).encode(buf, entry.actions());
        }
    }

    public record Entry(Fluid fluid, Optional<StatePredicate> fluidStatePredicate, Block block, Optional<StatePredicate> blockStatePredicate, List<LocationAction> actions){
        public static final MapCodec<Entry> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Registries.FLUID.getCodec().fieldOf("fluid").forGetter(Entry::fluid),
                StatePredicate.CODEC.optionalFieldOf("fluid_state_conditions").forGetter(Entry::fluidStatePredicate),
                Registries.BLOCK.getCodec().fieldOf("block").forGetter(Entry::block),
                StatePredicate.CODEC.optionalFieldOf("block_state_conditions").forGetter(Entry::blockStatePredicate),
                Codec.list(LocationAction.CODEC).fieldOf("actions").forGetter(Entry::actions)
        ).apply(instance, Entry::new));

        public boolean test(FluidState fluidState, BlockState blockState){
            if (fluidStatePredicate().isPresent()){
                if (!fluidStatePredicate().get().test(fluidState)) return false;
            }
            if (blockStatePredicate().isPresent()){
                return blockStatePredicate().get().test(blockState);
            }
            return true;
        }
    }

//    public record LocationActionEntry(Identifier type, LocationAction action){
//        public static final MapCodec<>
//    }
//
//    public record Reactions(Optional<SetBlock> setBlock, Optional<SetFluid> setFluid, Optional<DropItems> dropItems){
//        public static final MapCodec<Reactions> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
//                SetBlock.CODEC.optionalFieldOf("set_block").forGetter(Reactions::setBlock),
//                SetFluid.CODEC.optionalFieldOf("set_fluid").forGetter(Reactions::setFluid),
//                DropItems.CODEC.optionalFieldOf("drop_items").forGetter(Reactions::dropItems))
//                .apply(instance, Reactions::new));
//
//        public static final PacketCodec<RegistryByteBuf, Reactions> PACKET_CODEC = PacketCodec.ofStatic(Reactions::encode, Reactions::decode);
//        private static void encode(RegistryByteBuf buf, Reactions reactions){
//            boolean sb = reactions.setBlock().isPresent();
//            boolean sf = reactions.setFluid().isPresent();
//            boolean di = reactions.dropItems().isPresent();
//            buf.writeBoolean(sb);
//            buf.writeBoolean(sf);
//            buf.writeBoolean(di);
//            if (sb) {
//                PacketCodecs.entryOf(Block.STATE_IDS).encode(buf, reactions.setBlock().get().blockState());
//                buf.writeBoolean(reactions.setBlock().get().destroyEffect());
//            }
//            if (sf) PacketCodecs.entryOf(Fluid.STATE_IDS).encode(buf, reactions.setFluid().get().fluidState());
//            if (di) {
//                List<ItemStack> stackList = reactions.dropItems().get().stacks();
//                buf.writeInt(stackList.size());
//                for (ItemStack itemStack : stackList){
//                    ItemStack.PACKET_CODEC.encode(buf, itemStack);
//                }
//            }
//        }
//
//        private static Reactions decode(RegistryByteBuf buf){
//            boolean sb = buf.readBoolean();
//            boolean sf = buf.readBoolean();
//            boolean di = buf.readBoolean();
//            SetBlock setBlock = null;
//            SetFluid setFluid = null;
//            DropItems dropItems = null;
//            if (sb) {
//                BlockState blockState = PacketCodecs.entryOf(Block.STATE_IDS).decode(buf);
//                boolean d = buf.readBoolean();
//                setBlock = new SetBlock(true, blockState, d);
//            }
//            if (sf) {
//                FluidState fluidState = PacketCodecs.entryOf(Fluid.STATE_IDS).decode(buf);
//                setFluid = new SetFluid(true, fluidState);
//            }
//            if (di) {
//                int size = buf.readInt();
//                List<ItemStack> stackList = new ArrayList<>(size);
//                for (int i = 0; i < size; ++i){
//                    stackList.set(i, ItemStack.PACKET_CODEC.decode(buf));
//                }
//                dropItems = new DropItems(true, stackList);
//            }
//            return new Reactions(Optional.ofNullable(setBlock), Optional.ofNullable(setFluid), Optional.ofNullable(dropItems));
//        }
//    }
//
//    public record ReactionEntry(String type, LocationAction locationAction){
//        public static final Map<String, Codec<? extends LocationAction>> CODEC_MAP = ImmutableMap.of(
//                "set_block", SetBlock.CODEC,
//                "set_fluid", SetFluid.CODEC,
//                "drop_items", DropItems.CODEC);
//           }

}
