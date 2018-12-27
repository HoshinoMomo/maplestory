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

/* Item destroyer
*/

importPackage(java.util);
importPackage(net.sf.odinms.client);
importPackage(net.sf.odinms.server);

var status = 0;
var targets = new Array();
var operation = -1;
var sendTarget;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	} else {
		if (mode == 0) {
			cm.sendOk("I'll be waiting...")
			cm.dispose();
			return;
		}
		if (mode == 1)
			status++;
		else
			status--;
		if (status == 0) {
			cm.sendNext("I can destroy equip or send it to someone else on the map.");
		} else if (status == 1) {
			cm.sendSimple("What do you want to do?#b\r\n" +
				"#L0#Destroy item#l\r\n" +
				"#L1#Send it to someone else#l#k");
		} else if (status == 2) {
			operation = selection;
			if (selection == 0) {
				cm.sendYesNo("I will destroy the item in your first equip inventory slot. Are you ready?");
			} else if (selection == 1) {
				var toSend = "Who shall receive the item in your first equip inventory slot?#b";
				var iter = cm.getChar().getMap().getCharacters().iterator();
				var i = 0;
				targets = new Array();
				while (iter.hasNext()) {
					var curChar = iter.next();
					toSend += "\r\n#L" + i + "#" + curChar.getName() + "#l";
					targets[i] = curChar;
					i++;
				}
				toSend += "#k";
				cm.sendSimple(toSend);
			}
		} else if (status == 3) {
			if (operation == 0) {
				MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
				cm.sendOk("It is destroyed. See you next time.")
				cm.dispose();
			} else if (operation == 1) {
				sendTarget = targets[selection];
				cm.sendYesNo("I will send the item in your first equip inventory slot to " + sendTarget.getName() + ". Are you ready?");
			}
		} else if (status == 4) {
			if (operation == 1) {
				var item = cm.getChar().getInventory(MapleInventoryType.EQUIP).getItem(1).copy();
				MapleInventoryManipulator.removeFromSlot(cm.getC(), MapleInventoryType.EQUIP, 1, 1, true);
				MapleInventoryManipulator.addFromDrop(sendTarget.getClient(), item, "Sent to " + sendTarget.getName() + "using Ms. Tan");
				cm.sendOk(sendTarget.getName() + " has received the item. See you next time.");
				cm.dispose();
			}
		}
	}
}	