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

/* Shanks
	Warp NPC to Lith Harbor (104000000)
	located in Southperry (60000)
*/

var status = 0;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1 || status == 4) {
		cm.dispose();
	} else {
		if (status == 2 && mode == 0) {
			cm.sendOk("Alright, I'll be staying here for a while.");
			status = 4;
			return;
		}
		if (mode == 1)
			status++;
		else
			status--;
		if (status == 0) {
			cm.sendNext("#bBy Odin!#k It seems you have become quite strong.");
		} else if (status == 1) {
			cm.sendNextPrev("I can take you to #m104000000# where you'll find stronger enemies!")
		} else if (status == 2) {
			cm.sendYesNo("So, are you finished here?");
		} else if (status == 3) {
			cm.warp(104000000);
			cm.dispose();
		}
	}
}	