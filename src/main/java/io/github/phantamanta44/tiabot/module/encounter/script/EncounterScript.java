package io.github.phantamanta44.tiabot.module.encounter.script;

import java.util.Random;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import io.github.phantamanta44.tiabot.core.context.IEventContext;
import io.github.phantamanta44.tiabot.module.encounter.BattleContext;
import io.github.phantamanta44.tiabot.module.encounter.data.StatsDto;

public class EncounterScript {
	
	private static final String PKG_IMPORT = "importPackage(Packages.io.github.phantamanta44.tiabot.module.encounter.data);";

	public static void execute(String script, IEventContext ctx, BattleContext bc) {
		execute(script, ctx, bc, StatsDto.IDENTITY);
	}
	
	public static void execute(String script, IEventContext ctx, BattleContext bc, StatsDto stats) {
		Context con = Context.enter();
		try {
			Scriptable scope = con.initStandardObjects();
			defineObjects(scope, ctx, bc, stats);
			con.evaluateString(scope, script, "<enct>", 1, null);
		} catch (Exception ex) {
			ctx.sendMessage("Errored while executing script!");
			ex.printStackTrace();
		} finally {
			Context.exit();
		}
	}

	private static void defineObjects(Scriptable scope, IEventContext ctx, BattleContext bc, StatsDto stats) {
		ScriptableObject.putProperty(scope, "ectx", ctx);
		ScriptableObject.putProperty(scope, "bctx", bc);
		ScriptableObject.putProperty(scope, "stats", stats);
		ScriptableObject.putProperty(scope, "rand", new Random());
		Context.getCurrentContext().evaluateString(scope, PKG_IMPORT, "<enct>", 1, null);
	}
	
}

