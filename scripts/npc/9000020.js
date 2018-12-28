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

/* Spinel
	World Tour Bitch - Located everywhere
*/

importPackage(net.sf.odinms.server.maps);

var status = 0;
var goToShowa = false;

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
		if (cm.getChar().getMapId() == 800000000) {
			if (status == 0) {
				cm.sendNext("Hi, I drive a World Tour Mobile.");
			} else if (status == 1) {
					cm.sendSimple ("I can take you back somewhere for #bFREE#k.\r\n#L0#It's scary here take me back#l\r\n#L1#Bring me to Showa Town!#l");
			} else if (status == 2) {
				if (selection == 0) {
					cm.sendYesNo("So you want to go back to where you came from?");
				} else {
					goToShowa = true;
					cm.sendYesNo("So you want to go to Showa Town?");
				}
			} else if (status == 3) {
				var map;
				if (goToShowa) {
					map = 801000000;
				} else {
					map = cm.getChar().getSavedLocation(SavedLocationType.WORLDTOUR);
					if (map == -1) {
						map = 104000000;
					}
				}
				cm.warp(map, 0);
				cm.dispose();
			}
		} else {
			if (status == 0) {
				cm.sendNext("Hi, I drive a World Tour Mobile.");
			} else if (status == 1) {
				cm.sendNextPrev("I can take you to Zipangu for just a small fee.")
			} else if (status == 2) {
				if (cm.getMeso() < 1500) {
					cm.sendOk("You do not have enough mesos.")
					cm.dispose();
				} else {
					cm.sendYesNo("So you want to pay #b1500 mesos#k and leave for Zipangu? Alright, then, but just be aware that you may be running into some monsters around there, too. Okay, would you like to head over to Zipangu right now?");
				}
			} else if (status == 3) {
				cm.gainMeso(-1500);
				cm.getChar().saveLocation(SavedLocationType.WORLDTOUR);
				cm.warp(800000000, 0);
				cm.dispose();
			}
		}
	}
}