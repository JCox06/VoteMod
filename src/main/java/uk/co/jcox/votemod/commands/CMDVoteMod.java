//MIT License
//
//        Copyright (c) 2021 Jacob Cox
//
//        Permission is hereby granted, free of charge, to any person obtaining a copy
//        of this software and associated documentation files (the "Software"), to deal
//        in the Software without restriction, including without limitation the rights
//        to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//        copies of the Software, and to permit persons to whom the Software is
//        furnished to do so, subject to the following conditions:
//
//        The above copyright notice and this permission notice shall be included in all
//        copies or substantial portions of the Software.
//
//        THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//        IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//        FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//        AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//        LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//        OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
//        SOFTWARE.

package uk.co.jcox.votemod.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import org.bukkit.entity.Player;
import uk.co.jcox.votemod.Main;


@CommandAlias("votemod")
public class CMDVoteMod extends BaseCommand {

    private Main plugin;

    public CMDVoteMod(Main plugin) {
        this.plugin = plugin;
    }

    @Subcommand("clearvotes")
    @CommandPermission("votemod.admin")
    public void clearVotes(Player player) {
        plugin.textSystem().sendMessage(player, "reset-vote-manager-message");
        plugin.setNewVoteManager();
    }

    @Subcommand("removevote")
    @CommandPermission("votemod.admin")
    @Syntax("<targetName>")
    public void clearVote(Player player, String args[]) {
        String targetName = args[0];
        plugin.getVoteManager().remove(player, targetName);
    }

    @Subcommand("bug")
    @Description("Tells players how to submit a bug report")
    public void bugReport(Player player) {

        //todo need to setup bug-report with additionals
        plugin.textSystem().sendMessage(player, "bug-report-message", Main.HOME_PAGE + "issues");
    }

    @Subcommand("list")
    @Description("Lists the current ongoing votes")
    public void listVotes(Player player) {
        plugin.getVoteManager().listVotes(player);
    }

}
