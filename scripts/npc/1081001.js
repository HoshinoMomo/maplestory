/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc> 
                       Matthias Butz <matze@odinms.de>
                       Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation. You may not use, modify
    or distribute this program under any other version of the
    GNU Affero General Public License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

/* Pison
	Warp NPC to Lith Harbor (104000000)
	located in Florina Beach (110000000)
*/

importPackage(net.sf.odinms.server.maps);

var status = 0;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	} else {
		if (status >= 2 && mode == 0) {
			cm.sendOk("Alright, see you next time.");
			cm.dispose();
			return;
		}
		if (mode == 1)
			status++;
		else
			status--;
		if (status == 0) {
			cm.sendNext("Hi, I drive a Boat.");
		} else if (status == 1) {
			cm.sendNextPrev("I can take you back to Lith Harbor for #bFREE#k.")
		} else if (status == 2) {
			if (cm.getMeso() < 0) {
				cm.sendOk("You do not have enough mesos.")
				cm.dispose();
			} else {
				cm.sendYesNo("Are you sure you want to return to #bLith Harbor#k? Alright, we'll have to get going fast. Do you want to head back to Lith Harbor now?");
			}
		} else if (status == 3) {
			var map = cm.getChar().getSavedLocation(SavedLocationType.FLORINA);
			if (map == -1) {
				map = 104000000;
			}
			cm.warp(map, 0);
			cm.dispose();
		}
	}
}