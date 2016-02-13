package io.github.phantamanta44.tiabot.util;

import java.util.EnumSet;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IChannel.PermissionOverride;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

public class PermissionHelper {

	public static boolean hasPermission(IUser user, IChannel chan, Permissions perm) {
		EnumSet<Permissions> perms = user.getRolesForGuild(chan.getGuild().getID()).stream()
				.map(IRole::getPermissions)
				.flatMap(EnumSet::stream)
				.collect(() -> EnumSet.noneOf(Permissions.class), (s, p) -> s.add(p), (a, b) -> a.addAll(b));
		PermissionOverride po = chan.getUserOverrides().get(user.getID());
		if (po != null) {
			perms.removeAll(po.deny());
			perms.addAll(po.allow());
		}
		return perms.contains(perm);
	}
	
}
