package dev.ftb.mods.ftbquests.quest.theme.property;

import dev.ftb.mods.ftblibrary.icon.Icon;

/**
 * @author LatvianModder
 */
public class IconProperty extends ThemeProperty<Icon> {
	public final Icon builtin;

	public IconProperty(String n, Icon b) {
		super(n, Icon.EMPTY);
		builtin = b;
	}

	public IconProperty(String n) {
		this(n, Icon.EMPTY);
	}

	@Override
	public Icon parse(String string) {
		if (string.equals("builtin")) {
			return builtin;
		}

		return Icon.getIcon(string);
	}
}