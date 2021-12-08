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
import uk.co.jcox.votemod.votes.BaseVote;
import uk.co.jcox.votemod.votes.VoteBan;
import uk.co.jcox.votemod.votes.VotePardon;

@CommandAlias("newvote|nv")
public class CMDNewVote extends BaseCommand {

    private Main plugin;

    public CMDNewVote(Main plugin) {
        this.plugin = plugin;
    }

    @Subcommand("ban")
    @CommandAlias("kick|k|ban")
    @Description("Starts a new vote kick")
    @CommandPermission("votemod.vote")
    @Syntax("<player>")
    public void startVoteKick(Player player, String[] args) {
        String playerName = args[0];
        if(playerName != null) {
            BaseVote vb = new VoteBan(player, playerName.toLowerCase(), plugin);
            plugin.getVoteManager().newVote(vb);
        }
    }

    @Subcommand("pardon")
    @CommandAlias("pardon|p|unban|whitelist")
    @Description("Starts a new vote pardon")
    @CommandPermission("votemod.vote")
    @Syntax("<player>")
    public void startVotePardon(Player player, String[] args) {
        String playerName = args[0];
        if(playerName != null) {
            BaseVote vb = new VotePardon(player, playerName.toLowerCase(), plugin);
            plugin.getVoteManager().newVote(vb);
        }
    }
}
