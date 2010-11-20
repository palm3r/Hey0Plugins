# Market

A plugin for Minecraft hMod that provides simple market and price fluctuation.

**This plugin is not released yet, and this document might be not correct.**

## Installation

1. Extract zip to MinecraftServer/bin. You will see the following files added

		server/
			bin/
				plugins/
					Market/
						plugin.ini
						items.txt
						money.txt

1. Open server/bin/server.properties, and add Market to the value of plugins:

		example) plugins=Market,...

1. Open server/bin/groups.txt. add "/market" to the group which can change item settings in the market

		example) mods:d:/market,....

## Configuration

### bin/Market/plugin.ini

This file includes plugin configurations.
Each line must be "key = value"

		units = coin,coins // currency unit (singular and plural)

### bin/Market/items.txt

Includes item data handled in the market

Format: `ID,N,E,P,F,S,B`

		ID : Item ID
		N  : Item name
		E  : 1 or 0 (set 0 if you want to make it not buy and sell)
		P  : Base price
		F  : Price fluctuation rate
		S  : Stocks of item
		B  : Balance between supply and demand

### bin/Market/money.txt

Includes player's money

Format: `player:amount`

		player : Player name
		amount : Amount of money

## Commands

### /buy &lt;amount&gt; &lt;item&gt;

Buy items

		amount : Amount of item (1 when omitted)
		item   : Item name or Item ID (item in hand when omitted)

### /sell

Sell items<br />
Instructions:

1. Put items you want to sell to crafting table (2*2 upper right space in inventory)
1. Type /sell
1. Items that is not handled in the market will be left in crafting table

### /money &lt;player&gt; &lt;amount&gt;

* Show money (for normal players)
* Show other player's money, or set money (for admins only, not include groups which can use /market)

### /market [item] &lt;key value&gt; ...

Show and update item configuration in the market.
See "items.txt" section about key/value.

Examples:

	/market sand p 100    // change base price of sand to 100)
	/market 45 s 100 e 1  // change stocks of brickblock(id45) to 100, and enable
	/market tnt s 0 e 0   // change stocks of TNT to 0, and disable)

## Download

[Market.jar](https://github.com/palm3r/Hey0Plugins/raw/master/plugins/Market.jar)
