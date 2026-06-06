package defense.common;

import mekanism.api.EnumColor;

public class Reference
{
    /** Name of the channel and mod ID. */
    public static final String NAME = "DefenseTech";
    
    public static final String VERSION = "1.0.1";

    public static final String CHANNEL = "DEF";
    public static final String ENTITY_PREFIX = CHANNEL;
    public static final String DOMAIN = "defense";
    public static final String PREFIX = DOMAIN + ":";
    public static final String ASSETS_PATH = "/assets/defense/";
    public static final String TEXTURE_PATH = "textures/";
    public static final String GUI_PATH = TEXTURE_PATH + "gui/";
    public static final String MODEL_PREFIX = "models/";
    public static final String MODEL_DIRECTORY = ASSETS_PATH + MODEL_PREFIX;

    public static final String MODEL_TEXTURE_PATH = TEXTURE_PATH + MODEL_PREFIX;
    public static final String BLOCK_PATH = TEXTURE_PATH + "blocks/";
    public static final String ITEM_PATH = TEXTURE_PATH + "items/";

    public static final String CHAT_DESC = EnumColor.ORANGE + "[" + NAME + "]";
}
