/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.print_main.printer.util.records;

public record PlaceDataPack(PlaceData data,boolean placeMode) {
    public static PlaceDataPack plac(PlaceData data){
        return new PlaceDataPack(data,true);
    }
    public static PlaceDataPack inte(PlaceData data){
        return new PlaceDataPack(data,false);
    }

        public static final PlaceDataPack NULL = new PlaceDataPack(PlaceData.NULL,true);

}
