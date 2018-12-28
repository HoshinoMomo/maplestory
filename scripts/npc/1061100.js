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

/* Hotel Bitch
   SleepyWood Sauna
*/

var wui = 0;
var cost = 1000;

function start() {
	cm.sendYesNo ("Hello and welcome to Odin Sauna! Do you want to visit the sauna for #bFREE#k? Limited #bBETA#k offer only");
}

function action(mode, type, selection) {
	if (mode == 0 || wui == 1) {
		cm.dispose();
		cm.warp(105040401, 0);
		//cm.warp(925100600, 0);
	} else {
		wui = 1;
		cm.sendOk ("Enjoy the fat man inside!(pressing yes or no WILL send you in there anyways)");
	}
}
