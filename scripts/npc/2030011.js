/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc> 
					   Matthias Butz <matze@odinms.de>
					   Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation version 3 as published by
    the Free Software Foundation. You may not use, modify or distribute
    this program under any other version of the GNU Affero General Public
    License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

/* Regular Cab
	Warp NPC
	- Kerning edition (103000000)
*/

var status = 0;
var maps = Array(102000000, 101000000, 100000000, 103000000);
var cost = Array(10000000, 10000000, 10000000, 10000000);
var selectedMap = -1;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	} else {
		if (status >= 2 && mode == 0) {
			cm.sendOk("I'll be waiting, sooner or later you'll come crawling at my feet and pay the money!");
			cm.dispose();
			return;
		}
		if (mode == 1)
			status++;
		else
			status--;
		if (status == 0) {
			cm.sendNext("Hi, I'm the corrupt jail warden.");
		} else if (status == 1) {
			cm.sendNextPrev("I can pardon you for just a small fee.")
		} else if (status == 2) {
			var selStr = "Select your destination.#b";
			for (var i = 0; i < maps.length; i++) {
				selStr += "\r\n#L" + i + "##m" + maps[i] + "# (" + cost[i] + " meso)#l";
			}
			cm.sendSimple(selStr);
		} else if (status == 3) {
			if (cm.getMeso() < cost[selection]) {
				cm.sendOk("You do not have enough mesos.")
				cm.dispose();
			} else {
				cm.sendYesNo("Make sure you've gathered your things. Do you now want to go to #m" + maps[selection] + "#?");
				selectedMap = selection;
			}
		} else if (status == 4) {
			cm.gainMeso(-cost[selectedMap]);
			cm.warp(maps[selectedMap], 0);
			cm.dispose();
		}
	}
}	