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

package uk.co.jcox.votemod;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import uk.co.jcox.votemod.util.Messenger;
import uk.co.jcox.votemod.util.PlayerFetcher;
import uk.co.jcox.votemod.votes.BaseVote;

import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class VoteManager {
    private final Map<String, BaseVote> ongoing;
    private final Main plugin;
    private boolean broadcast;
    private final int requiredPlayers;

    public VoteManager(Main plugin) {
        ongoing = new HashMap<>();
        this.plugin = plugin;
        broadcast = plugin.getConfig().getBoolean("broadcast");
        this.requiredPlayers = plugin.getConfig().getInt("required-players");

    }

    public void newVote(BaseVote vote) {

        if(! voterCheck(vote.getSourcePlayer())) {
            return;
        }

        if(! checkConditions(vote.getSourcePlayer())) {
            return;
        }

        CompletableFuture<UUID> cf = new CompletableFuture<>();
        PlayerFetcher pf = new PlayerFetcher(vote.getTargetPlayer(), cf);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new PlayerFetcher(vote.getTargetPlayer(), cf));
        cf.whenComplete( (res, err) -> {

            if(err != null) {
                err.printStackTrace();
                Messenger.sendMessage(vote.getSourcePlayer(), plugin.getLangValue("no-such-player-message"));
                Messenger.log("Could not get player.");
                return;
            }

            OfflinePlayer op = Bukkit.getOfflinePlayer(res);
            String world = Bukkit.getWorlds().get(0).getName();

            System.out.println(Main.getPermissions().playerHas(world, op, "votemod.bypass"));

            if(! Main.getPermissions().playerHas(world, op, "votemod.bypass")) {
                this.ongoing.put(vote.getTargetPlayer(), vote);
                String msg = plugin.getLangValue("started-vote-message");
                Messenger.broadcast(vote.getSourcePlayer().getName() + " " + msg + " " + vote.getTargetPlayer());
                vote.validate();
            } else {
                Messenger.sendMessage(vote.getSourcePlayer(), plugin.getLangValue("bypass-message"));
            }
        } );
    }

    public void vote(Player voter, String target) {
        if(checkVoteExistence(voter, target)) {

            if(ongoing.get(target).getVotersContains(voter)) {
                //todo add this msg to resource bundle
                Messenger.sendMessage(voter, plugin.getLangValue("already-voted-message"));
                return;
            }
            String personalMsg = plugin.getLangValue("pass-vote-personal-message");
            String votedMsg = plugin.getLangValue("pass-vote-message");
            if(broadcast) Messenger.broadcast(voter.getName() + " " + votedMsg + " " + target);
            else Messenger.sendMessage(voter, personalMsg);
            ongoing.get(target).addVoter(voter);
        }

    }

    public boolean remove(String target, boolean successful) {
        if(checkVoteExistence(target)) {
            ongoing.remove(target);
            if(broadcast) {
                if(successful) {
                    String successMsg = plugin.getLangValue("successful-message").replace("|", target);
                    Messenger.broadcast(successMsg);
                    return true;
                }

                String expiredMsg = plugin.getLangValue("expired-message").replace("|", target);
                Messenger.broadcast(expiredMsg);
                return true;
            }
        }
        return false;
    }

    public boolean remove(Player source, String target) {
        if(checkVoteExistence(source, target)) {
            ongoing.remove(target);
            if(broadcast) Messenger.broadcast(plugin.getLangValue("removed-vote-brc-message") + " " + source);
            else Messenger.sendMessage(source, plugin.getLangValue("removed-vote-message"));
            return true;
        }
        return false;
    }

    public boolean checkVoteExistence(String target) {
        return this.ongoing.containsKey(target);
    }

    public boolean checkVoteExistence(Player source, String target) {
        if(checkVoteExistence(target)) {
            return true;
        }

        Messenger.sendMessage(source, plugin.getLangValue("no-such-vote-message"));
        return false;
    }

    private boolean voterCheck(Player source) {

        if(ongoing.size() >= plugin.getConfig().getInt("allowed-ongoing-votes")) {
            Messenger.sendMessage(source, plugin.getLangValue("ongoing-message"));
            return false;
        }

        return true;
    }

    private boolean checkConditions(Player source) {

        int numberOfPlayers = Bukkit.getServer().getOnlinePlayers().size();

        if(! (numberOfPlayers >= requiredPlayers)) {
            source.sendMessage(plugin.getLangValue("less-required-message"));
            return false;
        }

        //todo implement a time range here.
        return true;
    }

    public void listVotes(Player player) {
        //todo this is terrible and needs re-writing
        String header = plugin.getLangValue("votes-message");
        Messenger.sendMessage(player, "====" + header + "====");

        for(BaseVote vote : ongoing.values()) {
            String NAME = vote.getTargetPlayer();
            int VOTES = vote.getNumberOfVoters();
            String clazz = vote.getClass().getName().replaceFirst("uk.co.jcox.votemod.votes.Vote", "");
            String TYPE = plugin.getLangValue(clazz.toLowerCase());

            Messenger.log("TYPE: " + TYPE);

            String msg = TYPE + " " + NAME + " [" + VOTES + " " + header + "] ";
            Messenger.sendMessage(player, msg);
        }

    }
}
