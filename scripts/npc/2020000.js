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

/* Vogen
	El Nath: El Nath Market (211000100)
	
	Dark crystal maker o.o
*/

importPackage(net.sf.odinms.client);

var status = 0;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	} else {
		if (mode == 0) {
			cm.sendOk("See you next time.");
			cm.dispose();
			return;
		}
		if (mode == 1)
			status++;
		else
			status--;
		if (status == 0) {
			cm.sendNext("My name is #rVogen#k. If you bring me 10 #bDark Crystal Ores#k I may be able to craft you a #bDark Crystal#k for the small fee of #b1,000,000 meso#k.");
		} else if (status == 1) {
			var ores = 0;
			var iter = cm.getChar().getInventory(MapleInventoryType.ETC).listById(4004004).iterator();
			while (iter.hasNext()) {
				ores += iter.next().getQuantity();
			}
			if (ores < 10) {
				cm.sendOk("You do not have enough #bDark Crystal Ores#k.");
			} else if (cm.getMeso() < 1000000) {
				cm.sendOk("You do not have enough mesos.");
			} else {
				cm.gainItem(4004004, -10);
				cm.gainMeso(-1000000);
				cm.gainItem(4005004, 1);
				cm.sendOk("Here you go, enjoy this...piece of art!");
				
			}
			cm.dispose();
		}
	}
}	