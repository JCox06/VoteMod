# VoteMod (A3.6)
Early Development!

How many times have you had it? You have a Minecraft server and ask your newly invited friend to join too. You tell them the IP, they log in, and-- oh, yeah the Whitelist. And to make matter worse the Admin is offline, so you can't ask them. VoteMod fixes this issue by allowing standard players to vote on certain things. Read more below.

## What does VoteMod do?
Standard small grouped survival Multiplayer servers in Minecraft face many problems with regard to server administration (well they have from my own experience) this is because the servers consist of a group of friends, where only a select few of players are given server administrator.

The problem comes when server administrators leave the server, at this point anyone can do what they like because of the lack of administration.

VoteMod is a plugin for Spigot Minecraft Servers that aims to remove these issues. This is done through a voting-like system. This mechanic allows standard players to take administrative actions on a server.

The current actions are ban and unban but many more are being planned!

## Installation
1. Make sure you are running a Minecraft server that supports Bukkit plugins (Spigot, Paper, etc), and that the server is in online mode!
2. Download and place the VoteMod.jar into the plugins directory on the server
3. Make sure you download the Vault plugin and place it in the plugins directory on the server
4. Download a permission plugin of your choice that works with Vault offline player permission checks, for example LuckPerms. This is a critical step this MUST not be skipped.

Please note that at the current stage no pre-compiled jar is provided.
## Usage
- To allow players to start votes and vote on currently ongoing votes, assign them the VoteMod.vote permission
- To allow players to be immune from being voted assign them the VoteMod.bypass permission
- To start a vote type the command: /newvote {pardon/ban} -playername-
- To vote on a vote, type the command: /vote pass -playername-
- To list all the currently ongoing votes, type the command: /votemod list
- Other commands are available to clear all votes and cancel a current vote
- If a vote expires or is cancelled by an administrator, then the player will be added to an immune List where they cannot be voted for a certain amount of time.

## Disclaimer and info
This is a small project that I hope to work on. I'm going to be honest, although the plugin works I'm not too confident on the quality of my code, in fact not at all!

Additionally I'm really annoyed that this requires two other dependencies. It's annoying, even if it's not really an issue, it's annoying, to require Vault and a plugin manager, maybe too much? I don't know. Hopefully this isn't an issue for most people and I myself mostly install Vault and Luckperms the moment I start a new server.

Also, I genuinely struggled on certain bits of the project, and in fact after completing it I really need to practise some multi threading in Java.

I also feel as though I would like to add some sort of dependancy injection at some point.

BUT!!!! if you notice, anything at all that could be improved, please let me know. I want this project along with my experience to improve too! Thanks.

