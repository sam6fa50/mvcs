package sam6fa50.mvcs;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import sam6fa50.mvcs.tracking.StageScope;

import static sam6fa50.mvcs.MVCS.tracker;

// Home of the commands in this mod
public class MVCSCommands implements ModInitializer {

    @Override
    public void onInitialize() {
        registerStageCommand();
        registerCommitCommand();
    }

    private void registerStageCommand() {
        CommandRegistrationCallback.EVENT.register((dispatcher, buildContext, selection) -> {
            dispatcher.register(Commands.literal(MVCS.MOD_ID)
                    .then(Commands.literal("add").executes(MVCSCommands::stageError)
                            .then(Commands.literal("-A").executes(MVCSCommands::stageWithScopeAll))));
        });
    }

    private void registerCommitCommand() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(Commands.literal(MVCS.MOD_ID)
                    .then(Commands.literal("commit").executes(MVCSCommands::commitError)
                            .then(Commands.literal("-m").executes(MVCSCommands::commitError)
                                    .then(Commands.argument("message", StringArgumentType.string()).executes(MVCSCommands::commitWithMessage))))
            );
        });
    }

    private static int stageError(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        throw new CommandSyntaxException(null,
                () -> "You must supply an stage scope with '-A'! (for now the only option)");
    }

    private static int stageWithScopeAll(CommandContext<CommandSourceStack> context) {
        boolean status = tracker.stage(StageScope.ALL);

        if (status) {
            context.getSource().sendSuccess(() -> Component.literal(tracker.stageDiff()), false);
            return 1;
        }

        context.getSource().sendFailure(Component.literal("Nothing to stage!"));
        return 0;
    }

    private static int commitError(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        throw new CommandSyntaxException(null, () -> "You must supply a commit message with '-m'!");
    }

    private static int commitWithMessage(CommandContext<CommandSourceStack> context) {
        String commitMessage = StringArgumentType.getString(context, "message");
        boolean status = tracker.commit(commitMessage);

        if (status) {
            context.getSource().sendSuccess(() -> Component.literal("Committed with message: " + commitMessage),
                    false);
            return 1;
        }

        context.getSource().sendFailure(Component.literal(
                "Could not commit! You must stage changes with '/mvcs add [scope]' first"));
        return 0;
    }
}
