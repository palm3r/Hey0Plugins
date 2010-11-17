# Market

This is a plugin for Hey0 minecraft server mod that provides simple market and price fluctuation.

**This plugin is not released yet, and this document might be not correct.**

## Installation

1. Extract zip to MinecraftServer/bin. You will see the following files added:
    * server/bin/plugins/Market/plugin.ini
    * server/bin/plugins/Market/goods.txt
    * server/bin/plugins/Market/money.txt
1. Open server/bin/server.properties, and add Market to the value of plugins:
    * Example: plugins=Market,...
1. Open server/bin/groups.txt. add "/market-admin" to the group which can use admin commands
    * Example: mods:d:/market-admin,....

## Commands

### Buy

* /buy &lt;amount&gt; &lt;item&gt;
    * amount : integer (1 when omitted)
    * item : item-name or item-id (item in hand when omitted)

### Sell

1. Put items you want to sell to crafting table (2*2 upper right space in inventory)
1. Type /sell
1. Items that is not handled in the market will be left in crafting table

### Money

* /money
    * Display your money
* /money [player]
    * Display money of specified player (admin and only player who can use "/market-admin" command)
* /money [player] [amount]
    * Set money (Admin only)

### Management

* /market [item]
    * Display information of item
    * Example: sand P 10 S 100 B -10 F 1.5 E 1 A 25
    		* P : base price (editable)
    		* S : stocks (editable)
    		* B : balance (editable)
    		* F : factor (editable)
    		* E : enabled (editable)
    		* A : actual price (readonly)
* /market [item] [key:value]...
    * Update item setting
    * Only admin and player who can use "/market-admin" command
    * Keys: p,s,b,f,e
    * Exapmes:
        * /market sand p 100 (change base price of sand to 100)
        * /market 45 s 100 e 1 (change stocks of brickblock to 100, and enable)
        * /market tnt s 0 e 0 (change stocks of TNT to 0, and disable)

## Configuration

### plugins/Market/plugin.ini

This file includes plugin configurations.
Each line must be "key = value"

* Currency unit
    * Format : "units = singular,plural"
    * Default : "coin,coins"
    * Example : "units = dollar,dollars" (This displays your money like "You have 1 dollar" or "You have 2 dollars")

### plugins/Market/goods.txt

The data of items handled in the market is included in this file.

* format: "id,name,enabled,price,factor,stock,balance"
    * id : integer (block/item id)
    * name : string (block/item name)
    * enabled : 1 or 0 (set 0 if you want to make it not buy and sell)
    * price : integer (base price)
    * factor : float (price fluctuation rate)
    * stock : integer (stocks of item)
    * balance : integer (balance between supply and demand)

### plugins/Market/money.txt

Money of each players is included in this file. 

* Format: "player:amount"
    * player : string (player's name)
    * amount : integer (amount of money)

## Downloads

## Future plan

