package handling;

public enum RecvPacketOpcode implements WritableIntValueHolder {
    //LOGIN
    //登录密码  
    LOGIN_PASSWORD(false, (short) 0x01),//0x01
    //请求服务器列表  
    SERVERLIST_REQUEST(true, (short) 0x02),//0x02
    //许可协议回复  
    LICENSE_REQUEST(true, (short) 0x03),//0x03
    //选择性别  
    SET_GENDER(false, (short) 0x04),//0x04
    //服务器状态  
    SERVERSTATUS_REQUEST(false, (short) 0x05),//0x05
    //返回服务器列表
    SERVERLIST_REREQUEST(false, (short) 0x7FFE),//0xFF
    //请求人物列表  
    CHARLIST_REQUEST(false, (short) 0x09),//0x09
    //开始游戏  
    CHAR_SELECT(true, (short) 0x0A),//0x0A
    //检查人物名字 
    CHECK_CHAR_NAME(true, (short) 0x0C),//0x0C
    //创建人物 
    CREATE_CHAR(false, (short) 0x11),//0x11
    //删除人物？？？
    DELETE_CHAR(true, (short) 0x12),//0x12
    //GENERAL  
    PONG(false, (short) 0x13),//0x13
    //错误日志
    ERROR_LOG(true, (short) 0x14),//0x14
    //日志
    PACKET_ERROR(true, (short) 0x19),//0x19
    //CHANNEL
    PLAYER_LOGGEDIN(false, (short) 0x0B),//0x0B
    //异常数据
    STRANGE_DATA(true, (short) 0x15),//0x15
    //更换地图
    CHANGE_MAP(true, (short) 0x21),//0x21
    //更换频道
    CHANGE_CHANNEL(true, (short) 0x22),//0x22
    //进入商城
    ENTER_CASH_SHOP(true, (short) 0x23),//0x23
    //人物移动
    MOVE_PLAYER(true, (short) 0x24),//0x24
    //取消椅子
    CANCEL_CHAIR(true, (short) 0x25),//0x25
    //使用椅子
    USE_CHAIR(true, (short) 0x26),//0x26
    //近距离攻击
    CLOSE_RANGE_ATTACK(true, (short) 0x28),//0x28
    //远距离攻击
    RANGED_ATTACK(true, (short) 0x29),//0x29
    //魔法攻击
    MAGIC_ATTACK(true, (short) 0x2A),//0x2A
    //能量攻击
    PASSIVE_ENERGY(true, (short) 0x2B),//0x2B
    //获取伤害
    TAKE_DAMAGE(true, (short) 0x2C),//0x2C
    //普通聊天
    GENERAL_CHAT(true, (short) 0x2D),//0x2D
    //关闭黑板
    CLOSE_CHALKBOARD(true, (short) 0x2E),//0x2E
    //人物面部表情
    FACE_EXPRESSION(true, (short) 0x2F),//0x2F
    //使用物品效果
    USE_ITEMEFFECT(true, (short) 0x30),//0x30
    //怪物卡片
    MONSTER_BOOK_COVER(true, (short) 0x35),//0x35
    //NPC交谈
    NPC_TALK(true, (short) 0x36),//0x36
    //NPC详细交谈
    NPC_TALK_MORE(true, (short) 0x38),//0x38
    //NPC商店
    NPC_SHOP(true, (short) 0x3A),//0x3A
    //仓库
    STORAGE(true, (short) 0x3B),//0x3B
    //雇佣商店
    USE_HIRED_MERCHANT(true, (short) 0x3C),//0x3C
    //雇佣仓库领取
    MERCH_ITEM_STORE(true, (short) 0x3D),//0x3D
    //送货员
    DUEY_ACTION(true, (short) 0x3E),//0x3E
    //物品整理
    ITEM_SORT(true, (short) 0x42),//0x42
    //物品排序
    ITEM_GATHER(true, (short) 0x43),//0x43
    //物品移动
    ITEM_MOVE(true, (short) 0x44),//0x44
    //使用物品
    USE_ITEM(true, (short) 0x45),//0x45
    //取消物品结果
    CANCEL_ITEM_EFFECT(true, (short) 0x46),//0x46
    //使用召唤包
    USE_SUMMON_BAG(true, (short) 0x48),//0x48
    //宠物食品
    USE_MOUNT_FOOD(true, (short) 0x4A),//0x4A
    //使用现金物品
    USE_CASH_ITEM(true, (short) 0x4C),//0x4C
    //使用扑捉物品
    USE_CATCH_ITEM(true, (short) 0x4E),//0x4E
    //使用技能书
    USE_SKILL_BOOK(true, (short) 0x4F),//0x4F
    //使用回城卷
    USE_RETURN_SCROLL(true, (short) 0x52),//0x52
    //使用砸卷
    USE_UPGRADE_SCROLL(true, (short) 0x53),//0x53
    //分发能力点
    DISTRIBUTE_AP(true, (short) 0x54),//0x54
    //自动分发能力点
    AUTO_ASSIGN_AP(true, (short) 0x55),//0x55
    //自动回复HP/MP
    HEAL_OVER_TIME(true, (short) 0x56),//0x56
    //分发技能点
    DISTRIBUTE_SP(true, (short) 0x57),//0x57
    //特殊移动
    SPECIAL_MOVE(true, (short) 0x58),//0x58
    //取消增益效果
    CANCEL_BUFF(true, (short) 0x59),//0x59
    //技能效果
    SKILL_EFFECT(true, (short) 0x5A),//0x5A
    //金币掉落
    MESO_DROP(true, (short) 0x5B),//0x5B
    //给人气
    GIVE_FAME(true, (short) 0x5C),//0x5C
    //返回人物信息
    CHAR_INFO_REQUEST(true, (short) 0x5E),//0x5E
    //取消负面效果
    CANCEL_DEBUFF(true, (short) 0x60),//0x60
    //特殊地图移动---
    CHANGE_MAP_SPECIAL(true, (short) 0x61),//0x61
    //使用时空门---
    USE_INNER_PORTAL(true, (short) 0x62),//0x62
    //缩地石---
    TROCK_ADD_MAP(true, (short) 0x63),//0x63
    //任务动作---
    QUEST_ACTION(true, (short) 0x68),//0x68
    //效果开关--- 	服务端无次选择
    EFFECT_ON_OFF(false, (short) 0x69),//0x69
    //快捷交任务
    quest_KJ(true, (short) 0x6A),//0x6A
    //技能宏---
    SKILL_MACRO(true, (short) 0x66),//0x66
    //宝物盒 	
    ITEM_BAOWU(true, (short) 0x70),//0x70
    //孙子兵法 	
    ITEM_SUNZI(true, (short) 0x8E),//0x8E
    //谜之蛋
    USE_TREASUER_CHEST(true, (short) 0x72),//0x72
    //锻造技能
    ITEM_MAKER(true, (short) 0x71),//0x71
    //组队/家族聊天
    PARTYCHAT(true, (short) 0x74),//0x74
    //悄悄话
    WHISPER(true, (short) 0x75),//0x75
    //聊天招待
    MESSENGER(true, (short) 0x76),//0x76
    //玩家互动
    PLAYER_INTERACTION(true, (short) 0x77),//0x77
    //开设组队
    PARTY_OPERATION(true, (short) 0x78),//0x78
    //拒绝组队邀请
    DENY_PARTY_REQUEST(true, (short) 0x79),//0x79
    //开设家族
    GUILD_OPERATION(true, (short) 0x7A),//0x7A
    //拒绝家族邀请
    DENY_GUILD_REQUEST(true, (short) 0x7B),//0x7B
    //好友操作
    BUDDYLIST_MODIFY(true, (short) 0x7E),//0x7E
    //小纸条
    NOTE_ACTION(true, (short) 0x7F),//0x7F
    //使用门
    USE_DOOR(true, (short) 0x81),//0x81
    //改变键盘布局
    CHANGE_KEYMAP(true, (short) 0x83),//0x83
    RPS_GAME(true, (short) 0x84),//0x84
    //戒指
    RING_ACTION(true, (short) 0x85),//0x85
    //家族联盟
    ALLIANCE_OPERATION(true, (short) 0x8A),//0x8A
    DENY_ALLIANCE_REQUEST(true, (short) 0x8B),//0x8B
    //家族BBS
    BBS_OPERATION(true, (short) 0x8C),//0x8C
    //进入拍卖
    ENTER_MTS(true, (short) 0x8D),//0x8D
    //
    //打开学院
    REQUEST_FAMILY(true, (short) 0x95),//0x95
    OPEN_FAMILY(true, (short) 0x96),//0x96
    //添加学院
    FAMILY_OPERATION(true, (short) 0x97),//0x97
    DELETE_JUNIOR(true, (short) 0x98),//0x98
    DELETE_SENIOR(true, (short) 0x99),//0x99
    ACCEPT_FAMILY(true, (short) 0x9A),//0x9A
    USE_FAMILY(true, (short) 0x9B),//0x9B
    FAMILY_PRECEPT(true, (short) 0x9C),//0x9C
    FAMILY_SUMMON(true, (short) 0x9D),//0x9D
    CYGNUS_SUMMON(true, (short) 0x9E),//0x9E
    //战神连击点
    ARAN_COMBO(true, (short) 0x9F),//0x9F
    //召唤兽移动---
    MOVE_SUMMON(true, (short) 0xAD),//0xAD
    //召唤兽动作---
    SUMMON_ATTACK(true, (short) 0xAE),//0xAE
    //召唤兽伤害---
    DAMAGE_SUMMON(true, (short) 0xB0),//0xB0
    //怪物移动
    MOVE_LIFE(true, (short) 0xB7),//0xB7
    //自动攻击
    AUTO_AGGRO(true, (short) 0xB8),//0xB8
    //怪物攻击怪物
    FRIENDLY_DAMAGE(true, (short) 0xBD),//0xBD
    //怪物炸弹
    MONSTER_BOMB(true, (short) 0xBC),//0xBC
    //NPC说话
    NPC_ACTION(true, (short) 0xC0),//0xC0
    //物品拣起
    ITEM_PICKUP(true, (short) 0xC6),//0xC6
    //伤害反映
    DAMAGE_REACTOR(true, (short) 0xC9),//0xC9
    //碰触反映
    TOUCH_REACTOR(true, (short) 0xCA),//0xCA
    SNOWBALL(true, (short) 0xD0),//0xD0
    LEFT_KNOCK_BACK(true, (short) 0xD2),//0xD2
    COCONUT(true, (short) 0xD1),//0xD1
    //未知··
    NEW_SX(true, (short) 0xCB),//0xCB
    //怪物嘉年华
    MONSTER_CARNIVAL(true, (short) 0xD7),//0xD7
    //组队搜索请求
    SHIP_OBJECT(true, (short) 0xD9),//0xD9
    PARTY_SS(true, (short) 0xDF),//0xDF
    //人物数据更新
    PLAYER_UPDATE(true, (short) 0xE0),//0xE0
    //冒险岛TV
    MAPLETV(true, (short) 0xF6),//0xF6
    //拍卖系统
    MTS_OP(true, (short) 0xFB),//0xFB
    //豆豆机操作
    BEANS_GAME1(true, (short) 0xE2),//0xE2
    BEANS_GAME2(true, (short) 0xE3),//0xE3
    //CASHSHOP
    //点卷确认
    TOUCHING_CS(true, (short) 0xE8),//0xE8
    //购买物品
    BUY_CS_ITEM(true, (short) 0xE9),//0xE9
    //使用兑换券
    COUPON_CODE(true, (short) 0xEA),//0xEA
    //PET
    //召唤宠物
    SPAWN_PET(true, (short) 0x5F),//0x5F
    //宠物移动
    MOVE_PET(true, (short) 0xA5),//0xA5
    //宠物自动吃药
    PET_AUTO_POT(true, (short) 0xA9),//0xA9
    //宠物说话
    PET_CHAT(true, (short) 0xA6),//0xA6
    //宠物命令
    PET_COMMAND(true, (short) 0xA7),//0xA7
    //宠物拣取
    PET_LOOT(true, (short) 0xA8),//0xA8
    //宠物食品
    PET_FOOD(true, (short) 0x49),//0x49
    //聊天系统
    ChatRoom_SYSTEM(true, (short) 0x104),
    //
    //
    //
    //
    //
    //
    //以下源码没有处理的地方
    //---源代码没有
    REVIVE_ITEM(false, (short) 0x31),//0x31
    //钓鱼---源代码没有
    USE_FISHING_ITEM(false, (short) 0x47),//0x47
    //------源代码没有
    SCRIPTED_ITEM(false, (short) 0x4B),//0x4B
    //
    //
    //
    //
    //
    //以下未知
    MOONRABBIT_HP(false, (short) 0x7FFE),
    FOLLOW_REPLY(false, (short) 0x7FFE),
    FOLLOW_REQUEST(false, (short) 0x7FFE),
    USE_ITEM_QUEST(false, (short) 0x7FFE),
    UPDATE_QUEST(false, (short) 0x7FFE),
    USE_OWL_MINERVA(false, (short) 0x7FFE),
    OWL_WARP(false, (short) 0x7FFE),
    OWL(false, (short) 0x7FFE),
    GAME_POLL(false, (short) 0x7FFE),
    REPAIR_ALL(false, (short) 0x7FFE),
    REPAIR(false, (short) 0x7FFE),
    PET_EXCEPTIONLIST(false, (short) 0x7FFE),
    MTS_TAB(false, (short) 0x7FFE),
    TOUCHING_MTS(false, (short) 0x7FFE),
    CS_UPDATE(false, (short) 0x7FFE),
    UPDATE_CHAR_INFO(false, (short) 0x7FFE),
    MARRAGE_RECV(false, (short) 0x7FFE),
    DISPLAY_NODE(false, (short) 0x7FFE),
    MOB_NODE(false, (short) 0x7FFE),
    HYPNOTIZE_DMG(false, (short) 0x7FFE),
    USE_EQUIP_SCROLL(false, (short) 0x7FFE),
    USE_POTENTIAL_SCROLL(false, (short) 0x7FFE),
    USE_SCRIPTED_NPC_ITEM(false, (short) 0x7FFE),
    USE_MAGNIFY_GLASS(false, (short) 0x7FFE),
    TRANSFORM_PLAYER(false, (short) 0x7FFE),
    AUTH_SECOND_PASSWORD(false, (short) 0x7FFE),
    HELLO_CHANNEL(false, (short) 0x7FFE),
    HELLO_LOGIN(false, (short) 0x7FFE),
    MOVE_DRAGON(false, (short) 0x7FFE),
    AFTER_LOGIN(false, (short) 0x7FFE),//0xFF
    REGISTER_PIN(false, (short) 0x7FFE),//0xFF
    PLAYER_DC(false, (short) 0x7FFE),//0xFF
    VIEW_ALL_CHAR(false, (short) 0x7FFE),//0xFF
    PICK_ALL_CHAR(false, (short) 0x7FFE),//0xFF
    SPOUSE_CHAT(false, (short) 0x7FFE),//0x7FFE
    //报告玩家
    REPORT_PLAYER(false, (short) 0x7FFE),//0x7FFE
    ;
    private short code = -2;

    @Override
    public void setValue(short code) {
        this.code = code;
    }

    @Override
    public final short getValue() {
        return code;
    }
    private final boolean CheckState;

    private RecvPacketOpcode() {
        this.CheckState = true;
    }

    private RecvPacketOpcode(final boolean CheckState) {
        this.CheckState = CheckState;
    }

    private RecvPacketOpcode(final boolean CheckState, short code) {
        this.CheckState = CheckState;
        this.code = code;
    }

    public final boolean NeedsChecking() {
        return CheckState;
    }

    public static String nameOf(short value) {
        for (RecvPacketOpcode header : RecvPacketOpcode.values()) {
            if (header.getValue() == value) {
                return header.name();
            }
        }
        return "UNKNOWN";
    }
}
