/*
此文件是 小乐冒险岛 核心服务器文件之一 -<MapleStory Server>
Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
                       Matthias Butz <matze@odinms.de>
                       Jan Christian Meyer <vimes@odinms.de>

          此文件的制作者为 OdinMs 团队  以上是开发者的信息  以上是开发者的信息
         请不要随意修改否则后果自负
         此2次开发:小乐冒险岛 第1版
         基于Aexr端进行修改
         修改制作人员:小乐
         小乐联系QQ:390824898
*/
package net.sf.odinms.server.maps.pvp;

import java.util.Collections;
import net.sf.odinms.client.*;
import net.sf.odinms.client.ISkill;
import net.sf.odinms.client.SkillFactory;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.net.channel.handler.AbstractDealDamageHandler;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.server.life.MapleMonster;
import net.sf.odinms.server.life.MapleLifeFactory;
import net.sf.odinms.client.MapleBuffStat;
import net.sf.odinms.server.TimerManager;
import net.sf.odinms.tools.MaplePacketCreator;

public class PvPLibrary {

    private static int pvpDamage;
    private static int min;
    public static int maxDis;
    public static int maxHeight;
    private static int matk;
    private static int watk;
    private static int luk;
    private static boolean isAoe;
    public static boolean isLeft = false;
    public static boolean isRight = false;
    private static boolean magic = false;
    private static boolean magicrecovery = false;
    private static boolean magicguard = false;
    private static boolean mesguard = false;
    private static double multi = 0;
    private static double mastery = 0;
    private static int skill = 0;
    private static ISkill skil;
    private static boolean ignore = false;
    private static int attackedDamage = 0;
    private static MapleMonster pvpMob;
    private static Integer combo;
    private static int MAX_PVP_DAMAGE = 199999;
    private static int MIN_PVP_DAMAGE = 0;
    private static int DAMAGE_DIVIDER = 2;

    public static void pvpDamageBalance(AbstractDealDamageHandler.AttackInfo attack, MapleCharacter player) {
        matk = player.getTotalMagic();
        luk = player.getTotalLuk();
        watk = player.getTotalWatk();
        switch (attack.skill) {
            case 0: //normal attack
                multi = 1;
                maxHeight = 35;
                isAoe = false;
                break;
            case 1001004:    //Power Strike
                skil = SkillFactory.getSkill(1001004);
                multi = skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0;
                maxHeight = 35;
                isAoe = false;
                break;
            case 1001005:    //Slash Blast
                skil = SkillFactory.getSkill(1001005);
                multi = skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0;
                maxHeight = 35;
                isAoe = false;
                break;
            case 2001004:    //Energy Bolt
                skil = SkillFactory.getSkill(2001004);
                multi = skil.getEffect(player.getSkillLevel(skil)).getMatk();
                mastery = skil.getEffect(player.getSkillLevel(skil)).getMastery() * 5 + 10 / 100;
                pvpDamage = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8);
                min = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8 * mastery);
                pvpDamage = MapleCharacter.rand(min, pvpDamage);
                maxDis = 200;
                maxHeight = 35;
                isAoe = false;
                magic = true;
                break;
            case 2001005:    //Magic Claw
                skil = SkillFactory.getSkill(2001005);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getMatk());
                mastery = skil.getEffect(player.getSkillLevel(skil)).getMastery() * 5 + 10 / 100;
                pvpDamage = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8);
                min = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8 * mastery);
                pvpDamage = MapleCharacter.rand(min, pvpDamage);
                maxHeight = 35;
                isAoe = false;
                magic = true;
                break;
            case 3001004:    //Arrow Blow
                skil = SkillFactory.getSkill(3001004);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);
                maxHeight = 35;
                isAoe = false;
                break;
            case 3001005:    //Double Shot
                skil = SkillFactory.getSkill(3001005);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);
                maxHeight = 35;
                isAoe = false;
                break;
            case 4001334:    //Double Stab
                skil = SkillFactory.getSkill(4001334);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);
                maxHeight = 35;
                isAoe = false;
                break;
            case 4001344:    //Lucky Seven
                skil = SkillFactory.getSkill(4001344);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);
                pvpDamage = (int) (5 * luk / 100 * watk * multi);
                min = (int) (2.5 * luk / 100 * watk * multi);
                pvpDamage = MapleCharacter.rand(min, pvpDamage);
                maxHeight = 35;
                isAoe = false;
                ignore = true;
                break;
            case 2101004:    //Fire Arrow
                skil = SkillFactory.getSkill(4101004);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getMatk());
                mastery = skil.getEffect(player.getSkillLevel(skil)).getMastery() * 5 + 10 / 100;
                pvpDamage = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8);
                min = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8 * mastery);
                pvpDamage = MapleCharacter.rand(min, pvpDamage);
                maxDis = 400;
                maxHeight = 35;
                isAoe = false;
                magic = true;
                break;
            case 2101005:    //Poison Brace
                skil = SkillFactory.getSkill(2101005);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getMatk());
                mastery = skil.getEffect(player.getSkillLevel(skil)).getMastery() * 5 + 10 / 100;
                pvpDamage = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8);
                min = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8 * mastery);
                pvpDamage = MapleCharacter.rand(min, pvpDamage);
                maxDis = 400;
                maxHeight = 35;
                isAoe = false;
                magic = true;
                break;
            case 2201004:    //Cold Beam
                skil = SkillFactory.getSkill(2201004);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getMatk());
                mastery = skil.getEffect(player.getSkillLevel(skil)).getMastery() * 5 + 10 / 100;
                pvpDamage = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8);
                min = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8 * mastery);
                pvpDamage = MapleCharacter.rand(min, pvpDamage);
                maxDis = 300;
                maxHeight = 35;
                isAoe = false;
                magic = true;
                break;
            case 2301005:    //Holy Arrow
                skil = SkillFactory.getSkill(2301005);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getMatk());
                mastery = skil.getEffect(player.getSkillLevel(skil)).getMastery() * 5 + 10 / 100;
                pvpDamage = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8);
                min = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8 * mastery);
                pvpDamage = MapleCharacter.rand(min, pvpDamage);
                maxDis = 300;
                maxHeight = 35;
                isAoe = false;
                magic = true;
                break;
            case 4101005:    //Drain
                skil = SkillFactory.getSkill(4101005);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);
                maxHeight = 35;
                isAoe = false;
                break;
            case 4201005:    //Savage Blow
                skil = SkillFactory.getSkill(4201005);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);
                maxHeight = 35;
                isAoe = false;
                break;
            case 1111004:    //Panic: Axe
                skil = SkillFactory.getSkill(1111004);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);
                maxHeight = 35;
                isAoe = false;
                break;
            case 1111003:    //Panic: Sword
                skil = SkillFactory.getSkill(1111003);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);
                maxHeight = 35;
                isAoe = false;
                break;
            case 1311004:    //Dragon Fury: Pole Arm
                skil = SkillFactory.getSkill(1311004);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);
                maxHeight = 35;
                isAoe = false;
                break;
            case 1311003:    //Dragon Fury: Spear
                skil = SkillFactory.getSkill(1311003);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);
                maxHeight = 35;
                isAoe = false;
                break;
            case 1311002:    //Pole Arm Crusher
                skil = SkillFactory.getSkill(1311002);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);
                maxHeight = 35;
                isAoe = false;
                break;
            case 1311005:    //Sacrifice
                skil = SkillFactory.getSkill(1311005);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);
                maxHeight = 35;
                isAoe = false;
                break;
            case 1311001:    //Spear Crusher
                skil = SkillFactory.getSkill(1311001);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);
                maxHeight = 35;
                isAoe = false;
                break;
            case 2211002:    //Ice Strike
                skil = SkillFactory.getSkill(2211002);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getMatk());
                mastery = skil.getEffect(player.getSkillLevel(skil)).getMastery() * 5 + 10 / 100;
                pvpDamage = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8);
                min = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8 * mastery);
                pvpDamage = MapleCharacter.rand(min, pvpDamage);
                maxDis = 250;
                maxHeight = 35;
                isAoe = false;
                magic = true;
                break;
            case 2211003:    //Thunder Spear
                skil = SkillFactory.getSkill(2211003);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getMatk());
                mastery = skil.getEffect(player.getSkillLevel(skil)).getMastery() * 5 + 10 / 100;
                pvpDamage = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8);
                min = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8 * mastery);
                pvpDamage = MapleCharacter.rand(min, pvpDamage);
                maxDis = 300;
                maxHeight = 35;
                isAoe = false;
                magic = true;
                break;
            case 3111006:    //Strafe
                skil = SkillFactory.getSkill(3111006);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 1000.0);
                maxHeight = 35;
                isAoe = false;
                break;
            case 3211006:    //Strafe
                skil = SkillFactory.getSkill(3211006);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 1000.0);
                maxHeight = 35;
                isAoe = false;
                break;
            case 4111005:    //Avenger
                skil = SkillFactory.getSkill(4111005);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);
                maxHeight = 35;
                isAoe = false;
                break;
            case 4211002:    //Assaulter
                skil = SkillFactory.getSkill(4211002);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);
                maxDis = 200;
                maxHeight = 35;
                isAoe = false;
                break;
            case 1121008:    //Brandish
                skil = SkillFactory.getSkill(1121008);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);
                maxHeight = 35;
                isAoe = false;
                break;
            case 1121006:    //Rush
                skil = SkillFactory.getSkill(1121006);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);
                maxHeight = 35;
                isAoe = false;
                break;
            case 1221009:    //Blast
                skil = SkillFactory.getSkill(1221009);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);
                maxHeight = 35;
                isAoe = false;
                break;
            case 1221007:    //Rush
                skil = SkillFactory.getSkill(1221007);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);
                maxHeight = 35;
                isAoe = false;
                break;
            case 1321003:    //Rush
                skil = SkillFactory.getSkill(1321003);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);
                maxHeight = 35;
                isAoe = false;
                break;
            case 2121003:    //Fire Demon
                skil = SkillFactory.getSkill(2121003);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getMatk());
                mastery = skil.getEffect(player.getSkillLevel(skil)).getMastery() * 5 + 10 / 100;
                pvpDamage = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8);
                min = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8 * mastery);
                pvpDamage = MapleCharacter.rand(min, pvpDamage);
                maxDis = 400;
                maxHeight = 35;
                isAoe = false;
                magic = true;
                break;
            case 2221006:    //Chain Lightning
                skil = SkillFactory.getSkill(2221006);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getMatk());
                mastery = skil.getEffect(player.getSkillLevel(skil)).getMastery() * 5 + 10 / 100;
                pvpDamage = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8);
                min = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8 * mastery);
                pvpDamage = MapleCharacter.rand(min, pvpDamage);
                maxDis = 400;
                maxHeight = 35;
                isAoe = false;
                magic = true;
                break;
            case 2221003:    //Ice Demon
                skil = SkillFactory.getSkill(2221003);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getMatk());
                mastery = skil.getEffect(player.getSkillLevel(skil)).getMastery() * 5 + 10 / 100;
                pvpDamage = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8);
                min = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8 * mastery);
                pvpDamage = MapleCharacter.rand(min, pvpDamage);
                maxDis = 400;
                maxHeight = 35;
                isAoe = false;
                magic = true;
                break;
            case 2321007:    //Angel's Ray
                skil = SkillFactory.getSkill(2321007);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getMatk());
                mastery = skil.getEffect(player.getSkillLevel(skil)).getMastery() * 5 + 10 / 100;
                pvpDamage = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8);
                min = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8 * mastery);
                pvpDamage = MapleCharacter.rand(min, pvpDamage);
                maxDis = 400;
                maxHeight = 35;
                isAoe = false;
                magic = true;
                break;
            case 3121003:    //Dragon Pulse
                skil = SkillFactory.getSkill(3121003);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);
                maxHeight = 35;
                isAoe = false;
                break;
            case 3121004:    //Hurricane
                skil = SkillFactory.getSkill(3121004);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);
                maxHeight = 35;
                isAoe = false;
                break;
            case 3221003:    //Dragon Pulse
                skil = SkillFactory.getSkill(3221003);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);
                maxHeight = 35;
                isAoe = false;
                break;
            case 3221001:    //Piercing
                skil = SkillFactory.getSkill(3221003);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);
                maxHeight = 35;
                isAoe = false;
                break;
            case 3221007:    //Sniping
                pvpDamage = (int) (player.calculateMaxBaseDamage(watk) * 3);
                min = (int) (player.calculateMinBaseDamage(player) * 3);
                pvpDamage = MapleCharacter.rand(min, pvpDamage);
                maxHeight = 35;
                isAoe = false;
                ignore = true;
                break;
            case 4121003:    //Showdown taunt
                skil = SkillFactory.getSkill(4121003);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);
                maxHeight = 35;
                isAoe = false;
                break;
            case 4121007:    //Triple Throw
                skil = SkillFactory.getSkill(4121007);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);
                maxHeight = 35;
                isAoe = false;
                break;
            case 4221007:    //Boomerang Step
                skil = SkillFactory.getSkill(4221007);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);
                maxHeight = 35;
                isAoe = false;
                break;
            case 4221003:    //Showdown taunt
                skil = SkillFactory.getSkill(4221003);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);
                maxHeight = 35;
                isAoe = false;
                break;
            //aoe
            case 2201005:    //Thunderbolt
                skil = SkillFactory.getSkill(2201005);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getMatk());
                mastery = skil.getEffect(player.getSkillLevel(skil)).getMastery() * 5 + 10 / 100;
                pvpDamage = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8);
                min = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8 * mastery);
                pvpDamage = MapleCharacter.rand(min, pvpDamage);
                maxDis = 250;
                maxHeight = 250;
                isAoe = true;
                magic = true;
                break;
            case 3101005:    //Arrow Bomb : Bow
                skil = SkillFactory.getSkill(3101005);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);
                maxHeight = 250;
                isAoe = true;
                break;
            case 3201005:    //Iron Arrow : Crossbow
                skil = SkillFactory.getSkill(3201005);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);
                maxHeight = 35;
                isAoe = true;
                break;
            case 1111006:    //Coma: Axe
                skil = SkillFactory.getSkill(1111006);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);
                maxHeight = 250;
                isAoe = true;
                break;
            case 1111005:    //Coma: Sword
                skil = SkillFactory.getSkill(1111005);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);
                maxHeight = 250;
                isAoe = true;
                break;
            case 1211002:    //Charged Blow - skill doesn't work
                skil = SkillFactory.getSkill(1211002);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);
                maxHeight = 250;
                isAoe = true;
                break;
            case 1311006:    //Dragon Roar
                skil = SkillFactory.getSkill(1311006);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);
                maxDis = 600;
                maxHeight = 450;
                isAoe = true;
                break;
            case 2111002:    //Explosion
                skil = SkillFactory.getSkill(2111002);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getMatk());
                mastery = skil.getEffect(player.getSkillLevel(skil)).getMastery() * 5 + 10 / 100;
                pvpDamage = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8);
                min = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8 * mastery);
                pvpDamage = MapleCharacter.rand(min, pvpDamage);
                maxDis = 350;
                maxHeight = 350;
                isAoe = true;
                magic = true;
                break;
            case 2111003:    //Poison Mist
                skil = SkillFactory.getSkill(2111003);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getMatk());
                mastery = skil.getEffect(player.getSkillLevel(skil)).getMastery() * 5 + 10 / 100;
                pvpDamage = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8);
                min = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8 * mastery);
                pvpDamage = MapleCharacter.rand(min, pvpDamage);
                maxDis = 350;
                maxHeight = 350;
                isAoe = true;
                magic = true;
                break;
            case 2311004:    //Shining Ray
                skil = SkillFactory.getSkill(2311004);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getMatk());
                mastery = skil.getEffect(player.getSkillLevel(skil)).getMastery() * 5 + 10 / 100;
                pvpDamage = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8);
                min = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8 * mastery);
                pvpDamage = MapleCharacter.rand(min, pvpDamage);
                maxDis = 350;
                maxHeight = 350;
                isAoe = true;
                magic = true;
                break;
            case 3111004:    //Arrow Rain
                skil = SkillFactory.getSkill(3111004);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);
                maxDis = 350;
                maxHeight = 350;
                isAoe = true;
                break;
            case 3111003:    //Inferno
                skil = SkillFactory.getSkill(3111003);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);
                maxDis = 350;
                maxHeight = 350;
                isAoe = true;
                break;
            case 3211004:    //Arrow Eruption
                skil = SkillFactory.getSkill(3211004);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);
                maxDis = 350;
                maxHeight = 350;
                isAoe = true;
                break;
            case 3211003:    //Blizzard (Sniper)
                skil = SkillFactory.getSkill(3211003);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);
                maxDis = 350;
                maxHeight = 350;
                isAoe = true;
                break;
            case 4211004:    //Band of Thieves Skill doesn't work so i don't know
                skil = SkillFactory.getSkill(4211004);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);
                maxHeight = 350;
                isAoe = true;
                break;
            case 1221011:    //Sanctuary Skill doesn't work so i don't know
                skil = SkillFactory.getSkill(1221011);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);
                maxDis = 350;
                maxHeight = 350;
                isAoe = true;
                break;
            case 2121001:    //Big Bang
                skil = SkillFactory.getSkill(2121001);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getMatk());
                mastery = skil.getEffect(player.getSkillLevel(skil)).getMastery() * 5 + 10 / 100;
                pvpDamage = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8);
                min = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8 * mastery);
                pvpDamage = MapleCharacter.rand(min, pvpDamage);
                maxDis = 175;
                maxHeight = 175;
                isAoe = true;
                magic = true;
                break;
            case 2121007:    //Meteo
                skil = SkillFactory.getSkill(2121007);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getMatk());
                mastery = skil.getEffect(player.getSkillLevel(skil)).getMastery() * 5 + 10 / 100;
                pvpDamage = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8);
                min = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8 * mastery);
                pvpDamage = MapleCharacter.rand(min, pvpDamage);
                maxDis = 600;
                maxHeight = 600;
                isAoe = true;
                magic = true;
                break;
            case 2121006:    //Paralyze
                skil = SkillFactory.getSkill(2121006);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getMatk());
                mastery = skil.getEffect(player.getSkillLevel(skil)).getMastery() * 5 + 10 / 100;
                pvpDamage = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8);
                min = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8 * mastery);
                pvpDamage = MapleCharacter.rand(min, pvpDamage);
                maxDis = 250;
                maxHeight = 250;
                isAoe = true;
                magic = true;
                break;
            case 2221001:    //Big Bang
                skil = SkillFactory.getSkill(2221001);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getMatk());
                mastery = skil.getEffect(player.getSkillLevel(skil)).getMastery() * 5 + 10 / 100;
                pvpDamage = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8);
                min = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8 * mastery);
                pvpDamage = MapleCharacter.rand(min, pvpDamage);
                maxDis = 175;
                maxHeight = 175;
                isAoe = true;
                magic = true;
                break;
            case 2221007:    //Blizzard
                skil = SkillFactory.getSkill(2221007);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getMatk());
                mastery = skil.getEffect(player.getSkillLevel(skil)).getMastery() * 5 + 10 / 100;
                pvpDamage = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8);
                min = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8 * mastery);
                pvpDamage = MapleCharacter.rand(min, pvpDamage);
                maxDis = 600;
                maxHeight = 600;
                isAoe = true;
                magic = true;
                break;
            case 2321008:    //Genesis
                skil = SkillFactory.getSkill(2321008);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getMatk());
                mastery = skil.getEffect(player.getSkillLevel(skil)).getMastery() * 5 + 10 / 100;
                pvpDamage = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8);
                min = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8 * mastery);
                pvpDamage = MapleCharacter.rand(min, pvpDamage);
                maxDis = 600;
                maxHeight = 600;
                isAoe = true;
                magic = true;
                break;
            case 2321001:   //bishop Big Bang
                skil = SkillFactory.getSkill(2321001);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getMatk());
                mastery = skil.getEffect(player.getSkillLevel(skil)).getMastery() * 5 + 10 / 100;
                pvpDamage = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8);
                min = (int) ((matk * 0.8) + (luk / 4) / 18 * multi * 0.8 * mastery);
                pvpDamage = MapleCharacter.rand(min, pvpDamage);
                maxDis = 175;
                maxHeight = 175;
                isAoe = true;
                magic = true;
                break;
            case 4121004:    //Ninja Ambush
                pvpDamage = (int) Math.floor(Math.random() * (180 - 150) + 150);
                maxDis = 150;
                maxHeight = 300;
                isAoe = true;
                ignore = true;
                break;
            case 4121008:    //Ninja Storm knockback
                skil = SkillFactory.getSkill(4121008);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);
                pvpDamage = (int) Math.floor(Math.random() * (player.calculateMaxBaseDamage(watk) * multi));
                maxDis = 150;
                maxHeight = 35;
                isAoe = true;
                ignore = true;
                break;
            case 4221001:    //Assassinate
                skil = SkillFactory.getSkill(4221001);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);
                maxHeight = 35;
                isAoe = true;
                break;
            case 4221004:    //Ninja Ambush
                pvpDamage = (int) Math.floor(Math.random() * (180 - 150) + 150);
                maxDis = 150;
                maxHeight = 150;
                isAoe = true;
                ignore = true;
                break;
            case 9001006: // SUPER dragon ROAR
                pvpDamage = MAX_PVP_DAMAGE;
                maxDis = 150;
                maxHeight = 150;
                isAoe = true;
                ignore = true;
                break;
            /**
             *@author Supiangel
             *
             */
            case 5001001:    //First Strike
                skil = SkillFactory.getSkill(5001001);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);

                maxHeight = 35;
                isAoe = false;
                break;
            case 5001002:    //Back-flip kick
                skil = SkillFactory.getSkill(5001002);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);

                maxHeight = 35;
                isAoe = false;
                break;
            case 5101002:    //Backward Blow
                skil = SkillFactory.getSkill(5101002);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);

                maxHeight = 35;
                isAoe = false;
                break;
            case 5101003:    //Uppercut
                skil = SkillFactory.getSkill(5101003);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);

                maxHeight = 35;
                isAoe = false;
                break;
            case 5101004:    //Spinning Punch
                skil = SkillFactory.getSkill(5101004);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);

                maxHeight = 35;
                isAoe = false;
                break;
            case 5111002:    //Final Punch
                skil = SkillFactory.getSkill(5111002);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);

                maxHeight = 35;
                isAoe = false;
                break;
            case 5111004:    //Absorb
                skil = SkillFactory.getSkill(5111004);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);

                maxHeight = 35;
                isAoe = false;
                break;
            case 5111006:    //Smash
                skil = SkillFactory.getSkill(5111006);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);

                maxHeight = 35;
                isAoe = false;
                break;
            case 5001003:    //Double Shot
                skil = SkillFactory.getSkill(5001003);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);
                pvpDamage = (int) (5 * player.getStr() / 100 * watk * multi);
                min = (int) (2.5 * player.getStr() / 100 * watk * multi);
                pvpDamage = MapleCharacter.rand(min, pvpDamage);
                maxHeight = 35;
                isAoe = false;
                ignore = true;
                break;
            case 5201001:    //Fatal Bullet
                skil = SkillFactory.getSkill(5201001);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);

                maxHeight = 35;
                isAoe = false;
                break;
            case 5201004:    //Decoy
                skil = SkillFactory.getSkill(5201004);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);

                maxHeight = 35;
                isAoe = false;
                break;
            case 5201006:    //Withdraw
                skil = SkillFactory.getSkill(5201006);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);

                maxHeight = 35;
                isAoe = false;
                break;
            case 5210000:    //Triple Shot
                skil = SkillFactory.getSkill(5210000);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);

                maxHeight = 35;
                isAoe = false;
                ignore = true;
                break;
            case 5211004:    //Fire Shot
                skil = SkillFactory.getSkill(5211004);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);

                maxHeight = 35;
                isAoe = false;
                break;
            case 5211005:    //Ice Shot
                skil = SkillFactory.getSkill(5211005);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);

                maxHeight = 35;
                isAoe = false;
                break;
            case 5121001:    //Dragon Strike
                skil = SkillFactory.getSkill(5121001);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);

                maxHeight = 35;
                isAoe = true;
                break;
            case 5121007:    //Fist
                skil = SkillFactory.getSkill(5121007);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);

                maxHeight = 35;
                isAoe = false;
                break;
            case 5121002:    //Energy Orb
                skil = SkillFactory.getSkill(5121002);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);

                maxHeight = 35;
                isAoe = false;
                ignore = true;
                break;
            case 5121004:    //Demolition
                skil = SkillFactory.getSkill(5121004);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);

                maxHeight = 35;
                isAoe = false;
                break;
            case 5121005:    //Snatch
                skil = SkillFactory.getSkill(5121005);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);

                maxHeight = 35;
                isAoe = false;
                ignore = true;
                break;

            case 5221004:    //Rapid Fire
                skil = SkillFactory.getSkill(5221004);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);

                maxHeight = 35;
                isAoe = false;
                break;
            case 5221003:    //Air Strike
                skil = SkillFactory.getSkill(5221003);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);

                maxHeight = 35;
                isAoe = false;
                break;
            case 5221007:    //Battleship Cannon
                skil = SkillFactory.getSkill(5221007);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);

                maxHeight = 35;
                isAoe = false;
                ignore = true;
                break;
            case 5221008:    //Battleship Torpedo
                skil = SkillFactory.getSkill(5221008);
                multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 100.0);

                maxHeight = 35;
                isAoe = false;
                ignore = true;
                break;
            default:
                break;

        }
        if (!magic || !ignore) {
            maxDis = player.getMaxDis(player);
        }
    }

    public static void getDirection(AbstractDealDamageHandler.AttackInfo attack) {
        if (isAoe) {
            isRight = true;
            isLeft = true;
        } else if (attack.direction <= 0 && attack.stance <= 0) {
            isRight = false;
            isLeft = true;
        } else {
            isRight = true;
            isLeft = false;
        }
    }

    public static void monsterBomb(final MapleCharacter player, final MapleCharacter attackedPlayers, final AbstractDealDamageHandler.AttackInfo attack) {
        for (int dmgpacket = 0; dmgpacket < attack.numDamage; dmgpacket++) {
            TimerManager.getInstance().schedule(new Runnable() {

                @Override
                public void run() {
                    if (!magic || !ignore) {
                        pvpDamage = (int) (player.getRandomage(player) * multi);
                    }
                    combo = player.getBuffedValue(MapleBuffStat.COMBO);
                    if (combo != null) {
                        //player.handleOrbgain();//comment out for now
                        skil = SkillFactory.getSkill(1120003);
                        skill = player.getSkillLevel(skil);
                        if (skill > 0) {
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 500.0);
                            if (multi < 0) {
                                multi = 1;
                            }
                            pvpDamage *= multi;
                        } else {
                            skil = SkillFactory.getSkill(1120003);
                            skill = player.getSkillLevel(skil);
                            multi = (skil.getEffect(player.getSkillLevel(skil)).getDamage() / 500.0);
                            if (multi < 0) {
                                multi = 1;
                            }
                            pvpDamage *= multi;
                        }
                    }
                    if (!magic) {
                        pvpDamage -= (attackedPlayers.getTotalWdef() * 1.5);
                    } else {
                        pvpDamage -= (attackedPlayers.getTotalMdef() * 1.5);
                    }
                    pvpDamage /= 5;
                    if (magic) {
                        pvpDamage /= 2;
                    }
		    if (player.getVip() == 3)
			pvpDamage *= 2;
		      else
		    if (player.getVip() == 4)
			pvpDamage *= 4;
		      else
			pvpDamage *= 1;
                    int magicattack = (player.getTotalDex() + player.getTotalInt() + player.getTotalLuk() + player.getTotalStr()) / 300;
                    pvpDamage += magicattack;
                    int reborn = player.getReborns();
                    pvpDamage += reborn;
                    int magicat = (player.getTotalMagic() + player.getTotalWatk()) / 100;
                    pvpDamage += magicat;
                    if (player.getGMLevel() == 5)
                        pvpDamage *= 2000;

                    pvpDamage /= DAMAGE_DIVIDER;
                    if (pvpDamage > MAX_PVP_DAMAGE) {
                        pvpDamage = MAX_PVP_DAMAGE;
                    }
                    if (pvpDamage < 0) {
                        pvpDamage = MIN_PVP_DAMAGE;
                    }
                    Integer mguard = attackedPlayers.getBuffedValue(MapleBuffStat.MAGIC_GUARD);
                    Integer mesoguard = attackedPlayers.getBuffedValue(MapleBuffStat.MESOGUARD);
                    if (mguard != null) {
                        skil = SkillFactory.getSkill(2001002);
                        skill = attackedPlayers.getSkillLevel(skil);
                        if (skill > 0) {
                            multi = (skil.getEffect(attackedPlayers.getSkillLevel(skil)).getX() / 100.0);
                        }
                        int mg = (int) (pvpDamage * multi);
                        if (attackedPlayers.getMp() > mg) {
                            attackedPlayers.setMp(attackedPlayers.getMp() - mg);
                            pvpDamage -= mg;
                        } else {
                            pvpDamage -= attackedPlayers.getMp();
                            attackedPlayers.setMp(0);
                            attackedPlayers.dropMessage(5, "Your MP has been drained.");
                        }
                        magicguard = true;
                    }
                    if (mesoguard != null) {
                        skil = SkillFactory.getSkill(4211005);
                        skill = attackedPlayers.getSkillLevel(skil);
                        if (skill > 0) {
                            multi = (skil.getEffect(attackedPlayers.getSkillLevel(skil)).getX() / 100.0);
                        }
                        int mg = (int) (pvpDamage * multi);
                        if (attackedPlayers.getMeso() > mg) {
                            attackedPlayers.gainMeso(-mg, false);
                            pvpDamage *= 0.5;
                            if (player.haveItem(5221000));
                                pvpDamage *= 4;
                        } else {
                            attackedPlayers.dropMessage(5, "You do not have enough mesos to weaken the blow");
                        }
                        mesguard = true;
                    }
                    int y = 2;
                    int skillid;
                    int aPmp;
                    if (magic) {
                        for (int i = 0; i < y; i++) {
                            skillid = 100000 * i + 2000000;
                            skil = SkillFactory.getSkill(skillid);
                            skill = player.getSkillLevel(skil);
                            if (skill > 0) {
                                multi = (skil.getEffect(player.getSkillLevel(skil)).getX() / 100.0);
                                if (skil.getEffect(player.getSkillLevel(skil)).makeChanceResult()) {
                                    aPmp = (int) (multi * attackedPlayers.getMaxMp());
                                    if (attackedPlayers.getMp() > aPmp) {
                                        attackedPlayers.setMp(attackedPlayers.getMp() - aPmp);
                                        player.setMp(player.getMp() + aPmp);
                                        if (player.getMp() > player.getMaxMp()) {
                                            player.setMp(player.getMaxMp());
                                        }
                                    } else {
                                        player.setMp(player.getMp() + attackedPlayers.getMp());
                                        if (player.getMp() > player.getMaxMp()) {
                                            player.setMp(player.getMaxMp());
                                        }
                                        attackedPlayers.setMp(0);
                                    }
                                }
                            }
                        }
                        magic = false;
                        magicrecovery = true;
                    }
                    pvpMob = MapleLifeFactory.getMonster(9400711);

                    player.getClient().getSession().write(MaplePacketCreator.damagePlayer(attack.numDamage, pvpMob.getId(), attackedPlayers.getId(), pvpDamage));
                    attackedPlayers.addHP(-pvpDamage);
                    attackedDamage += pvpDamage;
                    attackedPlayers.getClient().getSession().write(MaplePacketCreator.serverNotice(5, player.getName() + " 正在攻击你.受到 " + attackedDamage + " 伤害!"));
                    if (attackedDamage > 0) {
                        combo = player.getBuffedValue(MapleBuffStat.COMBO);
                        if (combo != null) {
                            player.handleOrbgain();
                        }
                    }
                    attackedDamage = 0;
                    if (magicguard) {
                        player.getClient().getSession().write(MaplePacketCreator.serverNotice(5, player.getName() + " 使用魔法盾阻挡了部分攻击!"));
                        magicguard = false;
                    }
                    if (mesguard) {
                        player.getClient().getSession().write(MaplePacketCreator.serverNotice(5, player.getName() + " 使用金钱盾阻挡了部分攻击!"));
                        mesguard = false;
                    }
                    if (magicrecovery) {
                        attackedPlayers.getClient().getSession().write(MaplePacketCreator.serverNotice(5, player.getName() + " 使用了魔法吸收!"));
                        magicrecovery = false;
                    }
                    //rewards
            if (attackedPlayers.isDead() && attackedPlayers.getHp() <= 0) {
            int selec = (int)Math.floor(Math.random() * 4) + 1;
	    int randomed = (int)Math.floor(Math.random() * 20) + 1;
            int expReward = attackedPlayers.getLevel() * 100;
            int gpReward = (int)Math.floor(Math.random() * 150D + 50D);
            if ((double)player.getPvpKills() * 0.25D >= (double)player.getPvpDeaths())
            expReward *= 20;
            player.gainExp(expReward, true, false);
            player.gainPvpKill();
            player.getClient().getSession().write(MaplePacketCreator.serverNotice(6, "您已经把 " + attackedPlayers.getName() + "杀死!您将增加一点杀人次数！"));
            attackedPlayers.gainPvpDeath();
            if (selec >= 1)
            {
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            IItem itemedid = attackedPlayers.getInventory(MapleInventoryType.EQUIP).getItem((byte)randomed).copy();
	    if (attackedPlayers.getlockitem(randomed) == 0)
            {
            MapleInventoryManipulator.removeFromSlot(attackedPlayers.getClient(), MapleInventoryType.EQUIP, (byte)randomed, (short)1, true);
	    attackedPlayers.getMap().spawnItemDrop(attackedPlayers, attackedPlayers, itemedid, attackedPlayers.getPosition(), true, false);
            }
            } else
            {
                MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                IItem itemedid = attackedPlayers.getInventory(MapleInventoryType.ETC).getItem((byte)randomed);
                IItem tradeItem = itemedid.copy();
                MapleInventoryManipulator.removeFromSlot(attackedPlayers.getClient(), MapleInventoryType.ETC, (byte)randomed, (short)1, true);
                attackedPlayers.getMap().spawnItemDrop(attackedPlayers, attackedPlayers, ii.getEquipById(tradeItem.getItemId()), attackedPlayers.getPosition(), true, false);
            }
            int random = (int)Math.floor(Math.random() * 300000) + 10000;
            if (attackedPlayers.getMeso() >= random)
            {
                attackedPlayers.getMap().spawnMesoDrop(random, random, attackedPlayers.getPosition(), attackedPlayers, attackedPlayers, false);
                attackedPlayers.gainMeso(-random, true);
            }
            attackedPlayers.getClient().getSession().write(MaplePacketCreator.serverNotice(6, player.getName() + "已经把您杀死!"));
           // attackedPlayers.gainPvpDeath();

                    }
                }
            }, dmgpacket * 40 + 100);
        }
    }
    public synchronized static void doPvP(MapleCharacter player, AbstractDealDamageHandler.AttackInfo attack) {
        pvpDamageBalance(attack, player); //grab height/distance/damage/aoetrue\false
        getDirection(attack);
        for (MapleCharacter attackedPlayers : player.getMap().getNearestPvpChar(player.getPosition(), PvPLibrary.maxDis, PvPLibrary.maxHeight, Collections.unmodifiableCollection(player.getMap().getCharacters()))) {
            if (attackedPlayers.isAlive() && (player.getParty() == null || player.getParty() != attackedPlayers.getParty())) {
                monsterBomb(player, attackedPlayers, attack);
            }
        }
    }
}