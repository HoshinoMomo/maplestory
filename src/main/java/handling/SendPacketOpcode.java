package handling;

public enum SendPacketOpcode {
    //GENERAL
    PING((short) 0x14),//0x14
    //LOGIN
    LOGIN_STATUS((short) 0x00),//0x00
    //新连接
    SEND_LINK((short) 0x7FFE),//0xFF
    //许可协议
    LICENSE_RESULT((short) 0x02),//0x02
    //服务器状态
    SERVERSTATUS((short) 0x06),//0x06
    //选择性别
    CHOOSE_GENDER((short) 0x04),//0x04
    //性别选择返回
    GENDER_SET((short) 0x05),//0x05
    //屏蔽
    PIN_OPERATION((short) 0x7FFE),//0xFF
    PIN_ASSIGNED((short) 0x7FFE),//0xFF
    ALL_CHARLIST((short) 0x7FFE),//0xFF
    //服务器列表
    SERVERLIST((short) 0x09),//0x09
    //人物列表
    CHARLIST((short) 0x0A),//0x0A
    //服务器IP
    SERVER_IP((short) 0x0B),//0x0B
    //检查人物名反馈
    CHAR_NAME_RESPONSE((short) 0x0C),//0x0C
    //增加新人物
    ADD_NEW_CHAR_ENTRY((short) 0x11),//0x11
    //删除角色
    DELETE_CHAR_RESPONSE((short) 0x7FFE),//0x7FFE
    //
    CHANNEL_SELECTED((short) 0x18),//0x18
    //
    //RELOG_RESPONSE((short) 0x7FFE),//0x16
    //CHANNEL
    //频道更换
    CHANGE_CHANNEL((short) 0x13),//0x13
    //道具栏信息
    MODIFY_INVENTORY_ITEM((short) 0x20),//0x20
    UPDATE_INVENTORY_SLOT((short) 0x21),//0x21
    //更新能力值
    UPDATE_STATS((short) 0x22),//0x22
    //获得增益效果状态
    GIVE_BUFF((short) 0x23),//0x23
    //取消增益效果状态
    CANCEL_BUFF((short) 0x24),//0x24
    //临时能力值开始
    TEMP_STATS((short) 0x25),//0x25
    //临时能力值结束
    TEMP_STATS_RESET((short) 0x26),//0x26
    //更新技能
    UPDATE_SKILLS((short) 0x27),//0x27
    SKILL_USE_RESULT((short) 0x28),//0x28
    //人气反馈
    FAME_RESPONSE((short) 0x29),//0x29
    //人物具体信息
    SHOW_STATUS_INFO((short) 0x2A),//0x2A
    //小纸条
    SHOW_NOTES((short) 0x2B),//0x2B
    //缩地石
    TROCK_LOCATIONS((short) 0x2C),//0x2C
    ANTI_MACRO_RESULT((short) 0x2D),//0x2D
    CLAIM_RESULT((short) 0x2F),//0x2F
    CLAIM_STATUS_CHANGED((short) 0x30),//0x30
    SET_TAMING_MOB_INFO((short) 0x31),//0x31
    //更新骑宠
    UPDATE_MOUNT((short) 0x32),//0x32
    //任务完成效果
    SHOW_QUEST_COMPLETION((short) 0x33),//0x33
    //不明
    SEND_TITLE_BOX((short) 0x34),//0x34
    //使用技能书
    USE_SKILL_BOOK((short) 0x35),//0x35
    //不明
    FINISH_SORT((short) 0x36),//0x36
    FINISH_GATHER((short) 0x37),//0x37
    //FINISH_SORT2((short) 0x7FFE),//0x37
    //REPORT_PLAYER((short) 0x7FFE),//0x39
    //REPORTREPLY((short) 0x7FFE),//0x34
    //BBS_OPERATION((short) 0x7FFE),//0x38
    //角色信息
    CHAR_INFO((short) 0x3A),//0x3A
    //开启组队
    PARTY_OPERATION((short) 0x3B),//0x3B
    //好友列表
    BUDDYLIST((short) 0x3C),//0x3C
    //家族操作
    GUILD_OPERATION((short) 0x3E),//0x3E
    //家族联盟
    ALLIANCE_OPERATION((short) 0x3F),//0x3F
    //召唤门
    SPAWN_PORTAL((short) 0x40),//0x40
    //服务器公告
    SERVERMESSAGE((short) 0x41),//0x41
    //66清单上没有空的栏位
    //搜索器
    OWL_OF_MINERVA((short) 0x45),//0x45
    //英语学校
    ENGAGE_REQUEST((short) 0x45),//0x45
    ENGAGE_RESULT((short) 0x46),//0x46
    //塔罗牌
    SHOW_PREDICT_CARD((short) 0x46),//0x46
    //聊天窗显示黄色字体
    YELLOW_CHAT((short) 0x4E),//0x4E
    //情景喇叭
    AVATAR_MEGA((short) 0x56),//0x56
    //玩家NPC
    PLAYER_NPC((short) 0x5A),//0x5A
    //怪物卡
    MONSTERBOOK_ADD((short) 0x5B),//0x5B
    //怪物卡
    MONSTERBOOK_CHANGE_COVER((short) 0x5C),//0x5C
    FAIRY_PEND_MSG((short) 0x60),//0x60
    //打开学院-79
    SEND_PEDIGREE((short) 0x64),//0x64
    //学院信息
    OPEN_FAMILY((short) 0x65),//0x65
    //学院显示结果
    FAMILY_MESSAGE((short) 0x66),//0x66
    //学院邀请窗口
    FAMILY_INVITE((short) 0x67),//0x67
    //学院接受拒绝
    FAMILY_JUNIOR((short) 0x68),//0x68
    //成为了你的老师
    SENIOR_MESSAGE((short) 0x69),//0x69
    //学院权限列表
    FAMILY((short) 0x6A),//0x6A
    //名声度
    REP_INCREASE((short) 0x6B),//0x6B
    //学院登录提醒
    FAMILY_LOGGEDIN((short) 0x6C),//0x6C
    //学院buff使用
    FAMILY_BUFF((short) 0x6D),//0x6D
    //学院召唤 110
    FAMILY_USE_REQUEST((short) 0x6E),//0x6E
    //学院等级提升信息 111
    LEVEL_UPDATE((short) 0x6F),//0x6F
    //(家族)结婚提醒 112
    MARRIAGE_UPDATE((short) 0x70),//0x70
    //(家族)转职提醒 113
    JOB_UPDATE((short) 0x71),//0x71
    SET_BUY_EQUIP_EXT((short) 0x72),//0x72
    TOP_MSG((short) 0x73),//0x73
    DATA_CRC_CHECK_FAILED((short) 0x72),//0x72
    BBS_OPERATION((short) 0x74),//0x74
    FISHING_BOARD_UPDATE((short) 0x75),//0x75
    //抵用卷
    CHAR_CASH((short) 0x7D),//0x7D
    //技能宏
    SKILL_MACRO((short) 0x80),//0x80
    //传送到地图
    WARP_TO_MAP((short) 0x81),//0x81
    //进入拍卖
    MTS_OPEN((short) 0x82),//0x82
    //进入商城
    CS_OPEN((short) 0x83),//0x83
    //返回错误信息 
    BLOCK_MSG((short) 0x87),//0x87
    //组队家族聊天
    MULTICHAT((short) 0x8A),//0x8A
    //悄悄话
    WHISPER((short) 0x8B),//0x8B
    //配偶聊天
    SPOUSE_CHAT((short) 0x8C),//0x8C
    //BOSS血条
    BOSS_ENV((short) 0x8D),//0x8D
    //地图装备效果
    FORCED_MAP_EQUIP((short) 0x8F),//0x8F
    //地图效果
    MAP_EFFECT((short) 0x91),//0x91
    //音乐盒
    CASH_SONG((short) 0x92),//0x92
    //GM效果
    GM_EFFECT((short) 0x93),//0x93
    //OX答题
    OX_QUIZ((short) 0x94),//0x94
    //GM活动命令
    GMEVENT_INSTRUCTIONS((short) 0x95),//0x95
    //时钟
    CLOCK((short) 0x96),//0x96
    //船效果
    BOAT_EFFECT((short) 0x98),//0x98
    BOAT_HIDE((short) 0x99),//0x99
    //停止时钟
    STOP_CLOCK((short) 0x9C),//0x9C
    //阿里安特系列
    ARIANT_SCOREBOARD((short) 0x9A),//0x9A 
    //金字塔
    //REPAIR_WINDOW((short) 0x7FFE),//0x9A
    //PYRAMID_UPDATE((short) 0x7FFE),//0x9D
    //PYRAMID_RESULT((short) 0x7FFE),//0x9E

    MOVE_PLATFORM((short) 0x9F),//0x9F
    //召唤玩家
    SPAWN_PLAYER((short) 0xA2),//0xA2
    //移除玩家
    REMOVE_PLAYER_FROM_MAP((short) 0xA3),//0xA3
    //聊天信息
    CHATTEXT((short) 0xA4),//0xA4
    //小黑板
    CHALKBOARD((short) 0xA6),//0xA6
    //更新玩家
    UPDATE_CHAR_BOX((short) 0xA7),//0xA7
    //卷轴效果
    SHOW_SCROLL_EFFECT((short) 0xA9),//0xA9
    FISHING_CAUGHT((short) 0xAB),//0xAB
    //召唤宠物
    SPAWN_PET((short) 0xAD),//0xAD
    //宠物移动
    MOVE_PET((short) 0xAF),//0xAF
    PET_CHAT((short) 0xB0),//0xB0
    PET_NAMECHANGE((short) 0xB1),//0xB1
    PET_COMMAND((short) 0xB3),//0xB3
    //增加(取消)宠物技能
    PET_FLAG_CHANGE((short) 0xD8),//0xD8
    //召唤兽到地图
    SPAWN_SUMMON((short) 0xB4),//0xB4
    //召唤兽从地图删除
    REMOVE_SUMMON((short) 0xB5),//0xB5
    //召唤兽移动
    MOVE_SUMMON((short) 0xB6),//0xB6
    //召唤兽动作
    SUMMON_ATTACK((short) 0xB7),//0xB7
    //召唤兽伤害
    DAMAGE_SUMMON((short) 0xB9),//0xB9
    //召唤兽技能
    SUMMON_SKILL((short) 0xBA),//0xBA
    //移动玩家
    MOVE_PLAYER((short) 0xBB),//0xBB
    //近距离攻击
    CLOSE_RANGE_ATTACK((short) 0xBC),//0xBC
    RANGED_ATTACK((short) 0xBD),//0xBD
    MAGIC_ATTACK((short) 0xBE),//0xBE
    //技能效果
    SKILL_EFFECT((short) 0xC0),//0xC0
    //取消技能效果
    CANCEL_SKILL_EFFECT((short) 0xC1),//0xC1
    //人物伤害
    DAMAGE_PLAYER((short) 0xC2),//0xC2
    //人物面部表情
    FACIAL_EXPRESSION((short) 0xC3),//0xC3
    //显示物品效果
    SHOW_ITEM_EFFECT((short) 0xC5),//0xC5
    //椅子效果
    SHOW_CHAIR((short) 0xC6),//0xC6
    //更新玩家外观
    UPDATE_CHAR_LOOK((short) 0xC7),//0xC7
    //玩家外观效果
    SHOW_FOREIGN_EFFECT((short) 0xC8),//0xC8
    //获取异常状态
    GIVE_FOREIGN_BUFF((short) 0xC9),//0xC9
    //取消异常状态
    CANCEL_FOREIGN_BUFF((short) 0xCA),//0xCA
    //更新组队HP显示
    UPDATE_PARTYMEMBER_HP((short) 0xCB),//0xCB
    //动画效果
    Animation_EFFECT((short) 0xCE),//0xCE
    //取消椅子
    CANCEL_CHAIR((short) 0xCF),//0xCF
    //增益物品效果显示
    SHOW_ITEM_GAIN_INCHAT((short) 0xD0),//0xD0
    //武陵 
    DOJO_WARP_UP((short) 0x7FFE),//SHOW_ITEM_GAIN_INCHAT 1
    //LUCKSACK_PASS((short) 0xA4),//0xA4
    //LUCKSACK_FAIL((short) 0xA5),//0xA5
    //更新任务信息
    UPDATE_QUEST_INFO((short) 0xD6),//0xD6
    //玩家提示
    PLAYER_HINT((short) 0xD9),//0xD9
    //服务端无次选择
    TUTORIAL_DISABLE_UI((short) 0xE3),//0xE3
    TUTORIAL_LOCK_UI((short) 0xE4),//0xE4
    TUTORIAL_GUIDE((short) 0xE6),//0xE6
    //连击效果
    ARAN_COMBO((short) 0xE7),//0xE7
    //技能冷却-79
    COOLDOWN((short) 0xEC),//0xEC
    //怪物召唤
    SPAWN_MONSTER((short) 0xEE),//0xEE
    //杀死怪物
    KILL_MONSTER((short) 0xEF),//0xEF
    //怪物召唤控制
    SPAWN_MONSTER_CONTROL((short) 0xF0),//0xF0
    //怪物移动
    MOVE_MONSTER((short) 0xF1),//0xF1
    //移动怪物回应
    MOVE_MONSTER_RESPONSE((short) 0xF2),//0xF2
    //怪物伤害
    //DAMAGE_MONSTER((short) 0x7FFE),//0xF3
    //添加怪物状态
    APPLY_MONSTER_STATUS((short) 0xF4),//0xF4
    //取消怪物状态
    CANCEL_MONSTER_STATUS((short) 0xF5),//0xF5
    MOB_TO_MOB_DAMAGE((short) 0xF7),//0xF7
    //怪物伤害
    DAMAGE_MONSTER((short) 0xF8),//0xF8
    //
    ARIANT_THING((short) 0xFB),//0xFB
    //显示怪物HP
    SHOW_MONSTER_HP((short) 0xFC),//0xFC
    //磁铁效果
    SHOW_MAGNET((short) 0xFD),//0xFD
    //阿里安特系列 100 - 101
    CATCH_ARIANT((short) 0x100),//0x100
    //抓获怪物
    CATCH_MONSTER((short) 0x103),//0x103
    //召唤NPC
    SPAWN_NPC((short) 0x104),//0x104
    //移除NPC
    REMOVE_NPC((short) 0x105),//0x105
    //召唤NPC 请求控制权
    SPAWN_NPC_REQUEST_CONTROLLER((short) 0x106),//0x106
    //NPC说话
    NPC_ACTION((short) 0x107),//0x107
    //召唤雇佣商店
    SPAWN_HIRED_MERCHANT((short) 0x10D),//0x10D
    DESTROY_HIRED_MERCHANT((short) 0x10E),//0x10E
    UPDATE_HIRED_MERCHANT((short) 0x10F),//0x10F
    //掉落物品在地图
    DROP_ITEM_FROM_MAPOBJECT((short) 0x110),//0x110
    //从地图上删除物品
    REMOVE_ITEM_FROM_MAP((short) 0x111),//0x111
    //召唤LOVE
    SPAWN_LOVE((short) 0x113),//0x113
    //取消LOVE
    REMOVE_LOVE((short) 0x114),//0x114
    //召唤烟雾
    SPAWN_MIST((short) 0x115),//0x115
    //取消烟雾
    REMOVE_MIST((short) 0x116),//0x116
    //召唤门 0x11B -79
    SPAWN_DOOR((short) 0x117),//0x117
    //取消门
    REMOVE_DOOR((short) 0x118),//0x118
    //反应堆
    REACTOR_HIT((short) 0x11C),//0x11C
    //反应堆召唤
    REACTOR_SPAWN((short) 0x11E),//0x11E
    //重置反映堆
    REACTOR_DESTROY((short) 0x11F),//0x11F
    //雪球副本
    ROLL_SNOWBALL((short) 0x120),//0x120
    HIT_SNOWBALL((short) 0x121),//0x121
    SNOWBALL_MESSAGE((short) 0x122),//0x122
    LEFT_KNOCK_BACK((short) 0x123),//0x123
    HIT_COCONUT((short) 0x124),//0x124
    COCONUT_SCORE((short) 0x125),//0x125
    //MONSTER_CARNIVAL_START((short) 0x7FFE),//0xE2
    //MONSTER_CARNIVAL_OBTAINED_CP((short) 0x7FFE),//0xE3
    //MONSTER_CARNIVAL_PARTY_CP((short) 0x7FFE),//0xE4
    //MONSTER_CARNIVAL_SUMMON((short) 0x7FFE),//0xE5
    //MONSTER_CARNIVAL_DIED((short) 0x7FFE),//0xE7
    //怪物嘉年华
    MONSTER_CARNIVAL_START((short) 0x129),//0x129
    MONSTER_CARNIVAL_OBTAINED_CP((short) 0x12A),//0x12A
    MONSTER_CARNIVAL_PARTY_CP((short) 0x12B),//0x12B
    MONSTER_CARNIVAL_SUMMON((short) 0x12C),//0x12C
    //MONSTER_CARNIVAL_SUMMON1((short) 0x7FFE),//0x12D
    //服务端暂无此选择
    //MONSTER_CARNIVAL_MESSAGE((short) 0x7FFE),//0x12E
    MONSTER_CARNIVAL_DIED((short) 0x12E),//0x12E
    //服务端暂无此选择
    //MONSTER_CARNIVAL_LEAVE((short) 0x7FFE),//0x131
    ENGLISH_QUIZ((short) 0x130),//0x130
    ARIANT_SCORE_UPDATE((short) 0x132),//0x132
    //---
    //ARIANT_PQ_START((short) 0x7FFE),//0xFF
    //扎昆门
    ZAKUM_SHRINE((short) 0x144),//0x144
    //NPC交谈
    NPC_TALK((short) 0x145),//0x145
    //打开NPC商店
    OPEN_NPC_SHOP((short) 0x146),//0x146
    //NPC商店确认
    CONFIRM_SHOP_TRANSACTION((short) 0x147),//0x147
    //打开仓库
    OPEN_STORAGE((short) 0x14A),//0x14A
    //领取雇佣物品提示
    MERCH_ITEM_MSG((short) 0x14B),//0x14B
    //领取雇佣物品
    MERCH_ITEM_STORE((short) 0x14C),//0x14C
    //聊天招待
    MESSENGER((short) 0x14E),//0x14E
    //玩家互动
    PLAYER_INTERACTION((short) 0x14F),//0x14F
    //豆豆机系统
    BEANS_GAME1((short) 0x15C),//0x15C
    BEANS_GAME2((short) 0x15D),//0x15D
    //更新豆豆
    UPDATE_BEANS((short) 0x15E),//0x15E
    //送货员
    DUEY((short) 0x15F),//0x15F
    //现金商店更新
    CS_UPDATE((short) 0x161),//0x161
    //现在商店操作
    CS_OPERATION((short) 0x162),//0x162
    //键盘排序
    KEYMAP((short) 0x16F),//0x16F
    //自动吃药HP 服务端暂无次选择
    AUTO_HP_POT((short) 0x170),//0x170
    AUTO_MP_POT((short) 0x171),//0x171
    //冒险岛TV 服务端暂无次选择
    SEND_TV((short) 0x175),//0x175
    REMOVE_TV((short) 0x176),//0x176
    ENABLE_TV((short) 0x177),//0x177
    //拍卖操作
    MTS_OPERATION((short) 0x17C),//0x17C
    //服务端暂无次选择
    MTS_OPERATION2((short) 0x17B),//0x17B
    //金锤子
    VICIOUS_HAMMER((short) 0x182),//0x182
    //开始加载
    //地图动画播放
    CYGNUS_INTRO_LOCK((short) 0xE4),//0xE4
    //地图动画结束
    CYGNUS_INTRO_DISABLE_UI((short) 0xE3),//0xE3
    TUTORIAL_SUMMON((short) 0xE5),//0xE5
    SUMMON_HINT((short) 0xE5),//0xE5
    //人物动画创建 服务端暂无次选择
    CYGNUS_CHAR_CREATED((short) 0x7FFE),
    CURRENT_MAP_WARP((short) 0x7FFE),
    RELOG_RESPONSE((short) 0x7FFE),
    SERVER_BLOCKED((short) 0x7FFE),
    ENERGY_ATTACK((short) 0x7FFE),
    SHOW_POTENTIAL_RESET((short) 0x7FFE),
    SHOW_EQUIP_EFFECT((short) 0x7FFE),
    SHOW_POTENTIAL_EFFECT((short) 0x7FFE),
    MOVE_ENV((short) 0x7FFE),
    BOAT_EFF((short) 0x7FFE),
    PIGMI_REWARD((short) 0x7FFE),
    HORNTAIL_SHRINE((short) 0x7FFE),
    DRAGON_REMOVE((short) 0x7FFE),
    REPAIR_WINDOW((short) 0x7FFE),
    RPS_GAME((short) 0x7FFE),
    UPDATE_ENV((short) 0x7FFE),
    FOLLOW_MSG((short) 0x7FFE),
    ENERGY((short) 0x7FFE),
    GHOST_POINT((short) 0x7FFE),
    GHOST_STATUS((short) 0x7FFE),
    DRAGON_MOVE((short) 0x7FFE),
    DRAGON_SPAWN((short) 0x7FFE),
    CHAOS_ZAKUM_SHRINE((short) 0x7FFE),
    PYRAMID_UPDATE((short) 0x7FFE),
    GAME_POLL_QUESTION((short) 0x7FFE),
    FOLLOW_REQUEST((short) 0x7FFE),
    FOLLOW_MOVE((short) 0x7FFE),
    CHAOS_HORNTAIL_SHRINE((short) 0x7FFE),
    PYRAMID_RESULT((short) 0x7FFE),
    GAME_POLL_REPLY((short) 0x7FFE),
    FOLLOW_EFFECT((short) 0x7FFE),
    MONSTER_PROPERTIES((short) 0x7FFE),
    SECONDPW_ERROR((short) 0x7FFE),
    XMAS_SURPRISE((short) 0x7FFE),
    MESOBAG_FAILURE((short) 0x7FFE),
    GET_MTS_TOKENS((short) 0x7FFE),
    MESOBAG_SUCCESS((short) 0x7FFE),
    TALK_MONSTER((short) 0x7FFE),
    REMOVE_TALK_MONSTER((short) 0x7FFE),
    MONSTER_CARNIVAL_SUMMON1((short) 0x7FFE),
    EARN_TITLE_MSG((short) 0x7FFE),
    SUMMON_HINT_MSG((short) 0x7FFE),
    FOLLOW_MESSAGE((short) 0x7FFE);

    private short code = -2;

    private SendPacketOpcode(short code) {
        this.code = code;
    }

    public short getValue() {
        return code;
    }

    public static String getOpcodeName(int value) {
        for (SendPacketOpcode opcode : SendPacketOpcode.values()) {
            if (opcode.getValue() == value) {
                return opcode.name();
            }
        }
        return "UNKNOWN";
    }

}
