# VoteMod (A3.6)
Early Development!

How many times have you had it? You have a Minecraft server and ask your newly invited friend to join too. You tell them the IP, they log in, and-- oh, yeah the Whitelist. And to make matter worse the Admin is offline, so you can't ask them. VoteMod fixes this issue by allowing standard players to vote on certain things. Read more below.

## What does VoteMod do?
Standard small grouped survival Multiplayer servers in Minecraft face many problems with regard to server administration (well they have from my own experience) this is because the servers consist of a group of friends, where only a select few of players are given server administrator.

The problem comes when server administrators leave the server, at this point anyone can do what they like because of the lack of administration.

VoteMod is a plugin for Spigot Minecraft Servers that aims to remove these issues. This is done through a voting-like system. This mechanic allows standard players to take administrative actions on a server.

The current actions are ban and unban but many more are being planned!

## Installation
- Make sure you are running a Minecraft server that support Bukkit plugin. (Spigot, Paper, etc)
- Download and place the VoteMod.jar into the plugins directory on the server
- Make sure you download the Vault plugin and place it in the plugins directory on the server
- Download a permission plugin of your choice that works with Vault offline player permission checks, for example LuckPerms

## Issues and Bugs
- Multiple Language support is currently half working. Some of the keys have been put in the wrong place making for a buggy language system when not using English.
- If the user has installed Vault but not a permission plugin that can respond to offline player checking then the asyncronouse thread responsible for obtaining the UUID of the player and sending the request to vault will hang. This will result in the Vote not being added. 

## Disclaimer and info
This is a small project that I hope to work on. I'm going to be honest, although the plugin works I'm not too confident on the quality of my code, in fact not at all!

I genuinely struggled on certain bits of the project, and in fact after completing it I really need to practise some multi threading in Java.

I also feel as though I would like to add some sort of dependancy injection at some point.

BUT!!!! if you notice, anything at all that could be improved, please let me know. I want this project along with my experience to improve too! Thanks.

