package dev.ftb.mods.ftbquests.client;

import dev.architectury.utils.Env;
import dev.ftb.mods.ftblibrary.icon.Icons;
import dev.ftb.mods.ftblibrary.ui.BaseScreen;
import dev.ftb.mods.ftblibrary.util.ClientUtils;
import dev.ftb.mods.ftbquests.gui.CustomToast;
import dev.ftb.mods.ftbquests.gui.quests.QuestScreen;
import dev.ftb.mods.ftbquests.integration.FTBQuestsJEIHelper;
import dev.ftb.mods.ftbquests.net.DeleteObjectMessage;
import dev.ftb.mods.ftbquests.quest.*;
import dev.ftb.mods.ftbquests.quest.theme.QuestTheme;
import dev.ftb.mods.ftbquests.util.TextUtils;
import dev.ftb.mods.ftbteams.data.ClientTeamManager;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;

import java.util.Objects;

/**
 * @author LatvianModder
 */
public class ClientQuestFile extends QuestFile {
	public static ClientQuestFile INSTANCE;

	public static boolean exists() {
		return INSTANCE != null && !INSTANCE.invalid;
	}

	public TeamData self;
	public QuestScreen questScreen;
	public BaseScreen questGui;

	@Override
	public void load() {
		if (INSTANCE != null) {
			INSTANCE.deleteChildren();
			INSTANCE.deleteSelf();
		}

		self = new TeamData(Util.NIL_UUID);
		self.file = this;
		self.name = "Loading...";
		self.setLocked(true);
		INSTANCE = this;

		refreshGui();
		FTBQuestsJEIHelper.refresh(this);
	}

	@Override
	public boolean canEdit() {
		return self.getCanEdit();
	}

	@Override
	public void refreshGui() {
		clearCachedData();

		boolean hasPrev = false;
		boolean guiOpen = false;
		int zoom = 0;
		double scrollX = 0, scrollY = 0;
		long selectedChapter = 0L;
		long[] selectedQuests = new long[0];
		boolean chaptersExpanded = false;

		if (questScreen != null) {
			hasPrev = true;
			zoom = questScreen.zoom;
			scrollX = questScreen.questPanel.centerQuestX;
			scrollY = questScreen.questPanel.centerQuestY;
			selectedChapter = questScreen.selectedChapter == null ? 0L : questScreen.selectedChapter.id;
			selectedQuests = new long[questScreen.selectedObjects.size()];
			int i = 0;

			for (Movable m : questScreen.selectedObjects) {
				selectedQuests[i] = m.getMovableID();
				i++;
			}

			if (ClientUtils.getCurrentGuiAs(QuestScreen.class) != null) {
				guiOpen = true;
			}

			chaptersExpanded = questScreen.chapterPanel.expanded;
		}

		Minecraft.getInstance().setScreen(null);  // ensures prevScreen is null, so we can close correctly
		questScreen = new QuestScreen(this);
		questGui = questScreen;

		if (hasPrev) {
			questScreen.zoom = zoom;
			questScreen.selectChapter(getChapter(selectedChapter));

			for (long id : selectedQuests) {
				if (get(id) instanceof Movable m) {
					questScreen.selectedObjects.add(m);
				}
			}

			if (guiOpen) {
				questScreen.openGui();
			}
		}

		questScreen.refreshWidgets();

		if (hasPrev) {
			questScreen.questPanel.scrollTo(scrollX, scrollY);
		}

		questScreen.chapterPanel.setExpanded(chaptersExpanded);
	}

	public void openQuestGui() {
		if (exists()) {
			if (disableGui && !canEdit()) {
				Minecraft.getInstance().getToasts().addToast(new CustomToast(new TranslatableComponent("item.ftbquests.book.disabled"), Icons.BARRIER, TextComponent.EMPTY));
			} else if (self.isLocked()) {
				Minecraft.getInstance().getToasts().addToast(new CustomToast(lockMessage.isEmpty() ? new TextComponent("Quests locked!") : TextUtils.parseRawText(lockMessage), Icons.BARRIER, TextComponent.EMPTY));
			} else {
				questGui.openGui();
			}
		}
	}

	@Override
	public Env getSide() {
		return Env.CLIENT;
	}

	@Override
	public void deleteObject(long id) {
		new DeleteObjectMessage(id).sendToServer();
	}

	@Override
	public void clearCachedData() {
		super.clearCachedData();
		QuestTheme.instance.clearCache();
	}

	@Override
	public TeamData getData(Entity player) {
		return player == Minecraft.getInstance().player ? self : getData(Objects.requireNonNull(ClientTeamManager.INSTANCE.getKnownPlayer(player.getUUID()), "Non-null team required!").teamId);
	}

	public static void openBookToQuestObject(long id) {
		if (exists()) {
			ClientQuestFile file = ClientQuestFile.INSTANCE;
			if (file.questScreen == null) {
				ClientQuestFile.INSTANCE.openQuestGui();
			}
			if (file.questScreen != null) {
				if (id != 0L) {
					QuestObject qo = file.get(id);
					if (qo != null) {
						file.questScreen.open(qo, true);
					}
				} else {
					file.questScreen.openGui();
				}
			}
		}
	}
}