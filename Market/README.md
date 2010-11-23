# Market

A plugin for Minecraft hMod that provides simple market and price fluctuation.

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

		example) plugins=(Other plugins...),Market

1. In server/bin/groups.txt: add "/market" to the group which can use basic commands (/buy, /sell, /money, /top5)

		example) default:f:/market,....

1. In server/bin/groups.txt: add "/market-admin" to the group which can change market configuration.

		example) mods:d:/market,....

## Configuration

### bin/Market/plugin.ini

This file includes plugin configurations.
See below about keys and default values.

		items-file    = items.txt   # name of item data file
		money-file    = money.txt   # name of money data file
		currency-unit = coin,coins  # currency unit (singular and plural)

### bin/Market/items.txt

Includes item data handled in the market.

Format: `id,name,enabled,price,volatility`

		id         : Item ID
		name       : Item name
		enabled    : 1 or 0 (set 0 if you want to make it not buy and sell)
		price      : Item price
		volatility : Volatility of item price (1 is default)

volatility means price how much percentage changes every time one item is bought and sold.  

### bin/Market/money.txt

Includes money data

Format: `player:amount`

		player : Player name
		amount : Amount of money

## Commands

### /buy [item] &lt;amount&gt;

Buy items

		item   : Item name or Item ID
		amount : Amount of item (1 when omitted)

### /sell

Sell items<br />
Instructions:

1. Put items you want to sell to crafting table (2*2 upper right space in inventory)
1. Type /sell
1. Items that is not handled in the market will be left in crafting table

### /money &lt;player&gt; &lt;amount&gt;

* Show money (for normal players)
* Show other player's money, or set money (only admins can set other player's money)

### /market [item] &lt;key value&gt; ...

Show and update item config in the market.

Keys:

		p : Price
		v : Volatility (1 is default)
		e : 1 = enable / 0 = disable

Examples:

	/market tnt e 0        // disable TNT
	/market sand p 100     // change sand price to 100
	/market 52 e 1 p 20000 // enable mobspawner(id 52), and set its price to 20000
	/market wood p 20 v 2  // set wood price to 20, and set volatility to 2
	                       // (it's twice as volatile than normal)

## Download

[Latest version](https://github.com/palm3r/Hey0Plugins/raw/master/Market/build/latest/Market.jar)
