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

/* Ria
	lolcastle NPC
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
		if (cm.getChar().getMapId() != 101000000) {
			cm.dispose();
			return;
		}
		if (mode == 0) {
			cm.sendOk("Alright, see you next time.");
			cm.dispose();
			return;
		}
		status++;
		if (status == 0) {
			cm.sendNext("I am Ria. For a small fee of #b1000000 meso#k I can send you to the #rField of Judgement#k.");
		} else if (status == 1) {
			cm.sendYesNo("Do you wish to enter #rField of Judgement#k now?");
		} else if (status == 2) {
			var em = cm.getEventManager("lolcastle");
			if (em == null || !em.getProperty("entryPossible").equals("true")) {
				cm.sendOk("Sorry, but #rField of Judgement#k is currently closed.");
			} else if (cm.getMeso() < 1000000) {
				cm.sendOk("You do not have enough meso.");
			} else if (cm.getChar().getLevel() < 21) {
				cm.sendOk("You have to be at least level 21 to enter #rField of Judgement.#k");
			} else if (cm.getChar().getLevel() >= 21 && cm.getChar().getLevel() < 31) {
				cm.gainMeso(-1000000);
				em.getInstance("lolcastle1").registerPlayer(cm.getChar());
			} else if (cm.getChar().getLevel() >= 31 && cm.getChar().getLevel() < 51) {
				cm.gainMeso(-1000000);
				em.getInstance("lolcastle2").registerPlayer(cm.getChar());
			} else if (cm.getChar().getLevel() >= 51 && cm.getChar().getLevel() < 71) {
				cm.gainMeso(-1000000);
				em.getInstance("lolcastle3").registerPlayer(cm.getChar());
			} else if (cm.getChar().getLevel() >= 71 && cm.getChar().getLevel() < 91) {
				cm.gainMeso(-1000000);
				em.getInstance("lolcastle4").registerPlayer(cm.getChar());
			} else {
				cm.gainMeso(-1000000);
				em.getInstance("lolcastle5").registerPlayer(cm.getChar());
			}
			cm.dispose();
		}
	}
}
