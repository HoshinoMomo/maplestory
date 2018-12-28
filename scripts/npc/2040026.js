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

/**
 Third Eos Rock

	4001020 - Eos Rock Scroll
	221022900 - Ludibrium : Eos Tower 71st Floor
	221020000 - Ludibrium : Eos Tower 1st Floor
**/

var status = 0;
var maps = new Array(221022900, 221020000);
var mapNames = new Array("2nd Eos Rock", "4th Eos Rock");
var selectedMap = -1;

function start() {
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (status == 0) {
		var where = "Where do you want to go today?";
		for (var i = 0; i < maps.length; i++) {
			where += "\r\n#L" + i + "# " + mapNames[i] + "#l";
		}
		cm.sendSimple(where);
		status++;
	} else {
		if ((status == 1 && type == 1 && selection == -1 && mode == 0) || mode == -1) {
			cm.dispose();
		} else {
			if (status == 1 && selection == -1) {
				status++;
			}
			if (status == 1) {
				selectedMap = selection;
				cm.sendYesNo("You want to go to " + mapNames[selectedMap] + "?");
			} else
			if (status == 2) {
				if(cm.haveItem(4001020)) {
					cm.gainItem(4001020, -1);

					cm.warp(maps[selectedMap], 0);
					cm.dispose();
				} else {
					cm.sendOk("You need at least one Eos Rock Scroll.");
					cm.dispose();
				}
			}
		}
	}
}
