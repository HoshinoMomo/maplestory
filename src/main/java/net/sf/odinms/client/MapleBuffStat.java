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

package net.sf.odinms.client;

import java.io.Serializable;
import net.sf.odinms.net.LongValueHolder;

public enum MapleBuffStat implements LongValueHolder, Serializable {

    MORPH(0x2),
    RECOVERY(0x4),
    MAPLE_WARRIOR(0x8),
    STANCE(0x10),
    SHARP_EYES(0x20),
    MANA_REFLECTION(0x40),
    SHADOW_CLAW(0x100),
    INFINITY(0x20000000000000L),
    HOLY_SHIELD(0x400),
    HAMSTRING(0x800),
    BLIND(0x1000),
    CONCENTRATE(0x2000), // another no op buff
    ECHO_OF_HERO(0x8000),
    GHOST_MORPH(0x20000), // ??? Morphs you into a ghost - no idea what for
    DASH(0x60000000),
    BERSERK_FURY(0x8000000),
    ENERGY_CHARGE(0x800000000L),
    MONSTER_RIDING(0x10000000000L),
    WATK(0x100000000L),
    WDEF(0x200000000L),
    MATK(0x400000000L),
    MDEF(0x800000000L),
    ACC(0x1000000000L),
    AVOID(0x2000000000L),
    HANDS(0x4000000000L),
    SPEED(0x8000000000L),
    JUMP(0x10000000000L),
    MAGIC_GUARD(0x20000000000L),
    DARKSIGHT(0x40000000000L), // also used by gm hide
    BOOSTER(0x80000000000L),
    SPEED_INFUSION(0x800000000000L),
    POWERGUARD(0x100000000000L),
    HYPERBODYHP(0x200000000000L),
    HYPERBODYMP(0x400000000000L),
    INVINCIBLE(0x800000000000L),
    SOULARROW(0x1000000000000L),
    STUN(0x2000000000000L),
    POISON(0x4000000000000L),
    SEAL(0x8000000000000L),
    DARKNESS(0x10000000000000L),
    COMBO(0x20000000000000L),
    SUMMON(0x20000000000000L), //hack buffstat for summons ^.- (does/should not increase damage... hopefully <3)
    WK_CHARGE(0x40000000000000L),
    DRAGONBLOOD(0x80000000000000L), // another funny buffstat...
    HOLY_SYMBOL(0x100000000000000L),
    MESOUP(0x200000000000000L),
    SHADOWPARTNER(0x400000000000000L),
    //0x8000000000000
    PICKPOCKET(0x800000000000000L),
    PUPPET(0x800000000000000L), // HACK - shares buffmask with pickpocket - odin special ^.-
    MESOGUARD(0x1000000000000000L),
    WEAKEN(0x4000000000000000L), //SWITCH_CONTROLS(0x8000000000000L)
    ;
    static final long serialVersionUID = 0L;
    private final long i;

    private MapleBuffStat(long i) {
        this.i = i;
    }

    @Override
    public long getValue() {
        return i;
    }
}
