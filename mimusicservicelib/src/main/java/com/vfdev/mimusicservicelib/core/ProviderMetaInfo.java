package com.vfdev.mimusicservicelib.core;

/**
 * Class describes instances of TrackInfoProvider
 */
public class ProviderMetaInfo {
    public final String name;
    public final int drawable;
    public final Class<?> providerClass;
    public ProviderMetaInfo(String name, int drawable, Class<?> providerClass) {
        this.name = name;
        this.drawable = drawable;
        this.providerClass = providerClass;
    }
}
