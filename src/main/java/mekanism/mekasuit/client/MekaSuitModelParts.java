package mekanism.mekasuit.client;

/** Pure name routing shared by the legacy OBJ armor and item renderers. */
final class MekaSuitModelParts
{
    enum Layer
    {
        PLAYER,
        BODY,
        HELMET,
        EXOSKELETON,
        LED,
        TOOL
    }

    enum Part
    {
        HEAD(0F, 0F, 0F),
        BODY(0F, 0F, 0F),
        LEFT_ARM(-0.3125F, -0.125F, 0F),
        RIGHT_ARM(0.3125F, -0.125F, 0F),
        LEFT_LEG(-0.125F, -0.75F, 0F),
        RIGHT_LEG(0.125F, -0.75F, 0F);

        final float x;
        final float y;
        final float z;

        Part(float x, float y, float z)
        {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    private MekaSuitModelParts()
    {
    }

    static Layer suitLayer(String group)
    {
        if ("chest_body".equals(group)
              || "chest_left_arm".equals(group)
              || "chest_right_arm".equals(group)
              || "leggings_left_leg".equals(group)
              || "leggings_right_leg".equals(group))
        {
            return Layer.PLAYER;
        }
        if (group.startsWith("helmet_")
              || "chest_body_neck".equals(group))
        {
            return Layer.HELMET;
        }
        if (group.contains("_exo_") || group.startsWith("shared_")
              || group.matches("boots_(left|right)_leg_base[12]"))
        {
            return Layer.EXOSKELETON;
        }
        return Layer.BODY;
    }

    static Layer toolLayer(String group)
    {
        if (group.contains("_finger"))
        {
            return Layer.BODY;
        }
        if (group.contains("_exo_"))
        {
            return Layer.EXOSKELETON;
        }
        if (group.endsWith("_rod") || group.endsWith("_sight_led"))
        {
            return Layer.LED;
        }
        return Layer.TOOL;
    }

    static boolean belongsToSlot(String group, int armorType,
          boolean adjacentWorn)
    {
        boolean matches;
        switch (armorType)
        {
            case 0:
                matches = group.contains("helmet");
                break;
            case 1:
                matches = group.contains("chest");
                break;
            case 2:
                matches = group.contains("leggings");
                break;
            case 3:
                matches = group.contains("boots");
                break;
            default:
                return false;
        }
        if (!matches || !adjacentWorn)
        {
            return matches;
        }
        if (group.startsWith("excl_"))
        {
            return false;
        }
        return armorType != 2 || !group.startsWith("shared_");
    }

    static boolean matchesPart(String group, Part part)
    {
        switch (part)
        {
            case HEAD:
                return group.contains("_head");
            case BODY:
                return group.contains("_body")
                      && !group.contains("_arm");
            case LEFT_ARM:
                return group.contains("left_arm");
            case RIGHT_ARM:
                return group.contains("right_arm");
            case LEFT_LEG:
                return group.contains("left_leg");
            case RIGHT_LEG:
                return group.contains("right_leg");
            default:
                return false;
        }
    }
}
