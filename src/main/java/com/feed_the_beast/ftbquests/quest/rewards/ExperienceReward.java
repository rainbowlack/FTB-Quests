package com.feed_the_beast.ftbquests.quest.rewards;

import com.feed_the_beast.ftblib.lib.config.ConfigGroup;
import com.feed_the_beast.ftblib.lib.config.ConfigInt;
import com.feed_the_beast.ftblib.lib.icon.Icon;
import com.feed_the_beast.ftblib.lib.icon.ItemIcon;
import com.feed_the_beast.ftbquests.quest.Quest;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

/**
 * @author LatvianModder
 */
public class ExperienceReward extends QuestReward
{
	protected final ConfigInt value;

	public ExperienceReward(Quest quest, NBTTagCompound nbt)
	{
		super(quest);
		value = new ConfigInt(nbt.getInteger("value"), 1, Integer.MAX_VALUE);
	}

	@Override
	public void writeData(NBTTagCompound nbt)
	{
		nbt.setInteger("value", value.getInt());
	}

	@Override
	public Icon getAltIcon()
	{
		return ItemIcon.getItemIcon(new ItemStack(Items.EXPERIENCE_BOTTLE));
	}

	@Override
	public ITextComponent getAltDisplayName()
	{
		return new TextComponentTranslation("ftbquests.reward.ftbquests.xp.text", TextFormatting.GREEN + "+" + value.getInt());
	}

	@Override
	public void reward(EntityPlayerMP player)
	{
		player.addExperience(value.getInt());
	}

	@Override
	public void getConfig(ConfigGroup group)
	{
		super.getConfig(group);
		group.add("value", value, new ConfigInt(1));
	}
}