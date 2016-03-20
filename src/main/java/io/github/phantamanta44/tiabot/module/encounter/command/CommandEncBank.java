package io.github.phantamanta44.tiabot.module.encounter.command;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.github.phantamanta44.tiabot.core.command.ICommand;
import io.github.phantamanta44.tiabot.core.context.IEventContext;
import io.github.phantamanta44.tiabot.module.encounter.EncounterBank;
import io.github.phantamanta44.tiabot.module.encounter.EncounterBank.BankAccount;
import io.github.phantamanta44.tiabot.module.encounter.EncounterBank.BankStatus;
import io.github.phantamanta44.tiabot.module.encounter.EncounterData;
import io.github.phantamanta44.tiabot.module.encounter.data.EncounterItem;
import io.github.phantamanta44.tiabot.module.encounter.data.EncounterPlayer;
import io.github.phantamanta44.tiabot.module.encounter.event.EncounterHandler;
import io.github.phantamanta44.tiabot.util.MessageUtils;
import sx.blah.discord.handle.obj.IUser;

public class CommandEncBank implements ICommand {

	@Override
	public String getName() {
		return "encbank";
	}

	@Override
	public List<String> getAliases() {
		return Collections.emptyList();
	}

	@Override
	public String getDesc() {
		return "Manipulate the item bank.";
	}

	@Override
	public String getUsage() {
		return "encbank deposit|withdraw <item>";
	}

	@Override
	public void execute(IUser sender, String[] args, IEventContext ctx) {
		EncounterPlayer pl = EncounterHandler.getEncPlayer(sender);
		if (EncounterHandler.isEngaged(pl)) {
			ctx.sendMessage("You cannot use this command whilest in battle!");
			return;
		}
		BankAccount acc = EncounterBank.getAccount(pl);
		if (args.length == 0) {
			String items = acc.getItems().entrySet().stream()
					.map(e -> String.format("- **%s** x %d", e.getKey().getName(), e.getValue()))
					.reduce((a, b) -> a.concat("\n").concat(b)).orElse("(There's nothing here.)");
			ctx.sendMessage("__**Item Bank Account: %s**__\n%s", pl.getName(), items);
		} else {
			switch (args[0]) {
			case "deposit":
				if (args.length < 2) {
					ctx.sendMessage("You must specify an item to deposit!");
					return;
				}
				EncounterItem toD = EncounterData.matchItem(MessageUtils.concat(Arrays.copyOfRange(args, 1, args.length)));
				if (toD == null) {
					ctx.sendMessage("Nonexistent item!");
					return;
				}
				if (!pl.getInv().contains(toD)) {
					ctx.sendMessage("You don't have this item in your inventory!");
					return;
				}	
				pl.getInv().remove(toD);
				acc.addItem(toD);
				EncounterData.save();
				ctx.sendMessage("Deposited: %s x 1", toD.getName());
				break;
			case "withdraw":
				if (args.length < 2) {
					ctx.sendMessage("You must specify an item to withdraw!");
					return;
				}
				EncounterItem toW = EncounterData.matchItem(MessageUtils.concat(Arrays.copyOfRange(args, 1, args.length)));
				if (toW == null) {
					ctx.sendMessage("Nonexistent item!");
					return;
				}
				if (acc.removeItem(toW) == BankStatus.ERROR) {
					ctx.sendMessage("You don't have this item in your bank account!");
					return;
				}
				if (pl.getInv().size() >= EncounterBank.INV_SIZE) {
					ctx.sendMessage("You don't have space for this item! Try depositing something first.");
					return;
				}
				pl.getInv().add(toW);
				EncounterData.save();
				ctx.sendMessage("Withdrawn: %s x 1", toW.getName());
				break;
			default:
				ctx.sendMessage("Invalid syntax!");
				break;
			}
		}
	}

	@Override
	public boolean canUseCommand(IUser sender, IEventContext ctx) {
		return true;
	}

	@Override
	public String getPermissionMessage(IUser sender, IEventContext ctx) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getEnglishInvocation() {
		return null;
	}

}
