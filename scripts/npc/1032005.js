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

/* VIP Cab
	Warp NPC to Ant Tunnel Park (105070001)
	located in Ellinia (101000000)
*/

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
			cm.sendOk("This town also has a lot to offer. Find us if and when you feel the need to go to the ant tunnel park.");
			cm.dispose();
			return;
		}
		if (mode == 1)
			status++;
		else
			status--;
		if (status == 0) {
			cm.sendNext("Hi there! This cab is for VIP customers only.");
		} else if (status == 1) {
			cm.sendNextPrev("Instead of just taking you to different towns like regular cabs, we offer a much better service worthy of VIP class. It's a bit pricey, but... for only 10,000 mesos, we'll take you safely to the #bant tunnel#k.")
		} else if (status == 2) {
			if (cm.getMeso() < 10000) {
				cm.sendOk("You do not have enough mesos.")
				cm.dispose();
			} else {
				cm.sendYesNo("The ant tunnel is located deep inside in the dungeon that's placed at the center of the Victoria Island, where 24 Hr Mobile Store is. Would you like to go there for #b10,000 mesos#k ?.");
			}
		} else if (status == 3) {
			cm.gainMeso(-10000);
			cm.warp(105070001, 0);
			cm.dispose();
		}
	}
}