//package com.kijinseija.seija_printer.settings;
//
//import meteordevelopment.meteorclient.settings.Setting;
//import net.minecraft.nbt.NbtCompound;
//import net.minecraft.nbt.NbtList;
//import net.minecraft.nbt.NbtString;
//import net.minecraft.state.property.Properties;
//import net.minecraft.state.property.Property;
//
//import java.util.List;
//
//public class EnumProSetting extends Setting<List<Property>> {
//    @Override
//    protected List<Property> parseImpl(String str) {
//        return null;
//    }
//
//    @Override
//    protected boolean isValueValid(List<Property> value) {
//        return false;
//    }
//
//    @Override
//    protected NbtCompound save(NbtCompound tag) {
//        NbtList valueTag = new NbtList();
//        for (Property property : get()) {
//            valueTag.add(NbtString.of(property.getName()));
//        }
//        tag.put("value",valueTag);
//        return tag;
//    }
//
//    @Override
//    protected List<Property> load(NbtCompound tag) {
//        Properties.
//        return null;
//    }
//}
