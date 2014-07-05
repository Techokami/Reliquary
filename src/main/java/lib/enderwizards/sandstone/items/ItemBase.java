package lib.enderwizards.sandstone.items;

import com.google.common.collect.ImmutableMap;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lib.enderwizards.sandstone.mod.ModRegistry;
import lib.enderwizards.sandstone.util.LanguageHelper;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

/**
 * ItemBase, a helper class for items. Handles language names, language
 * tooltips, and icon registering.
 *
 * @author TheMike
 */
public class ItemBase extends Item {

    public ItemBase(String langName) {
        this.setUnlocalizedName(langName);
    }

    /**
     * Just a call to formatTooltip(). If you are overriding this function, call
     * formatTooltip() directly and DO NOT call super.addInformation().
     */
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean whatDoesThisEvenDo) {
        this.formatTooltip(null, stack, list);
    }

    /**
     * Used to format tooltips. Grabs tooltip from language registry with the
     * entry 'item.unlocalizedName.tooltip'. Has support for Handlebars-style
     * templating, and line breaking using '\n'.
     *
     * @param toFormat An ImmutableMap that has all the regex keys and values. Regex
     *                 strings are handled on the tooltip by including '{{regexKey}}'
     *                 with your regex key, of course.
     * @param stack    The ItemStack passed from addInformation.
     * @param list     List of description lines passed from addInformation.
     */
    public void formatTooltip(ImmutableMap<String, String> toFormat, ItemStack stack, List list) {
        String langTooltip = LanguageHelper.getLocalization(this.getUnlocalizedName(stack) + ".tooltip");
        if (langTooltip == null)
            return;
        if (toFormat != null) {
            Iterator<Entry<String, String>> entrySet = toFormat.entrySet().iterator();
            while (entrySet.hasNext()) {
                Entry<String, String> toReplace = entrySet.next();
                langTooltip = langTooltip.replace("{{" + toReplace.getKey() + "}}", toReplace.getValue());
            }
        }

        for (String descriptionLine : langTooltip.split(";")) {
            if (descriptionLine != null && descriptionLine.length() > 0)
                list.add(descriptionLine);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getItemStackDisplayName(ItemStack stack) {
        return LanguageHelper.getLocalization(this.getUnlocalizedNameInefficiently(stack) + ".name");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        itemIcon = iconRegister.registerIcon(ModRegistry.getID(this.getClass().getCanonicalName()) + ":" + this.getUnlocalizedName().substring(5));
    }

}