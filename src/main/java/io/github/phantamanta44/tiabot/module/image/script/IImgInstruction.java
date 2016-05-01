package io.github.phantamanta44.tiabot.module.image.script;

public interface IImgInstruction {

	String getInvocation();

	int execute(ImageScriptContext ctx, String[] args);

}
