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

package net.sf.odinms.net.channel.handler;

import java.sql.SQLException;

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author Penguins (Acrylic)
 */
public class CouponCodeHandler extends AbstractMaplePacketHandler {

    private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CouponCodeHandler.class);

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        slea.skip(2);
        String code = slea.readMapleAsciiString();
        boolean validcode = false;
        int type = -1;
        int item = -1;
        try {
            validcode = c.getPlayer().getNXCodeValid(code.toUpperCase(), validcode);
        } catch (SQLException e) {
            log.error("Code SQL Error", e);
        }

        if (validcode) {
            try {
                type = c.getPlayer().getNXCodeType(code);
            } catch (SQLException e) {
                log.error("Code SQL Error", e);
            }
            try {
                item = c.getPlayer().getNXCodeItem(code);
            } catch (SQLException e) {
                log.error("Code SQL Error", e);
            }
            if (type != 5) {
                try {
                    c.getPlayer().setNXCodeUsed(code);
                } catch (SQLException e) {
                    log.error("Code SQL Error", e);
                }
            }
            /*
             * Explanation of type!
             * Basically, this makes coupon codes do
             * different things!
             * 
             * Type 0: NX, Type 1: Maple Points,
             * Type 2: Gift Tokens, Type 3: NX + Gift Tokens
             * Type 4: Item
             * Type 5: NX Coupon that can be used over and over
             * 
             * When using Types 0-3, the item is the amount
             * of NX or Maple Points you get. When using Type 4
             * the item is the ID of the item you get. Enjoy!
             */
            switch (type) {
                case 0:
                case 1:
                case 2:
                    c.getPlayer().modifyCSPoints(type, item);
                    break;
                case 3:
                    c.getPlayer().modifyCSPoints(0, item);
                    c.getPlayer().modifyCSPoints(1, (item / 5000));
                    break;
                case 4:
                    MapleInventoryManipulator.addById(c, item, (short) 1, "物品使用优惠券获得.", null, -1);
                    c.getSession().write(MaplePacketCreator.showCouponRedeemedItem(item));
                    break;
                case 5:
                    c.getPlayer().modifyCSPoints(0, item);
                    break;
            }
            c.getSession().write(MaplePacketCreator.showNXMapleTokens(c.getPlayer()));
        } else {
            c.getSession().write(MaplePacketCreator.wrongCouponCode());
        }
    }
}
