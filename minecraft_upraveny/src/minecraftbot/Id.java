package minecraftbot;

/**
 *
 * @author eZ
 */
public enum Id {
            NONE(-1),
            BEGIN(-2),
            END(-3),
            // Block Tiles 
            AIR(0),
            STONE(1, ItemType.ROCK),
            GRASS (2, ItemType.GROUND),
            DIRT (3, ItemType.GROUND),
            COBBLESTONE (4, ItemType.ROCK),
            WOOD (5, ItemType.WOOD),
            SAPLING (6, ItemType.PLANT),
            BEDROCK (7),
            ADMINIUM (7),
            WATER (8, ItemType.LIQUID),
            STILL_WATER (9, ItemType.LIQUID),
            LAVA (10, ItemType.LIQUID),
            STILL_LAVA (11, ItemType.LIQUID),
            SAND (12, ItemType.GROUND),
            GRAVEL (13, ItemType.GROUND),
            GOLD_ORE (14),
            IRON_ORE (15),
            COAL_ORE (16),
            LOG (17, ItemType.WOOD),
            LEAVES (18, ItemType.LEAVES),
            SPONGE(19),
            GLASS (20, ItemType.GLASS),
            SANDSTONE(24),
            WOOL (35, ItemType.WOOL),
            FLOWER (37, ItemType.PLANT),
            ROSE (38, ItemType.PLANT),
            MUSHROOM (39),
            RED_MUSHROOM (40),
            GOLD_BLOCK (41),
            IRON_BLOCK (42),
            DOUBLE_STAIR (43),
            STAIR (44),
            BRICK (45),
            TNT (46),
            BOOKSHELF (47, ItemType.WOOD),
            MOSSY_COBBLESTONE (49, ItemType.ROCK),
            OBSIDIAN (49),
            TORCH (50),
            FIRE (51),
            MOB_SPAWNER (52),
            WOODEN_STAIRS (53),
            CHEST (54),
            REDSTONE_WIRE (55),
            DIAMOND_ORE (56),
            DIAMOND_BLOCK (57),
            WORKBENCH (58),
            CROPS (59),
            SOIL (60),
            FURNACE (61),
            BURNING_FURNACE (62),
            SIGN_POST (63),
            LADDER (65),
            RAILS (66),
            STAIRS (67),
            LEVER (69),
            STONE_PRESSURE_PLATE (70),
            WOODEN_PRESSURE_PLATE ( 72),
            REDSTONE_ORE (73),
            REDSTONE_TORCH (76),
            STONE_BUTTON (77),
            SNOW (78, ItemType.SNOW),
            ICE (79, ItemType.ICE),
            SNOW_BLOCK (80),
            CACTUS ( 81),
            CLAY (82),
            REED (83),
            JUKEBOX(84),
            FENCE (85),

            //Items
            IRON_SHOVEL(256, ItemType.SHOVEL),
            IRON_PICKAXE(257, ItemType.PICKAXE),
            IRON_AXE(258, ItemType.AXE),
            COAL(263),
            IRON_SWORD(267, ItemType.SWORD),
            WOODEN_SWORD(268, ItemType.SWORD),
            WOODEN_SHOVEL(269, ItemType.SHOVEL),
            WOODEN_PICKAXE(270, ItemType.PICKAXE),
            WOODEN_AXE(271, ItemType.AXE),
            STONE_SWORD(272, ItemType.SWORD),
            STONE_SHOVEL(273, ItemType.SHOVEL),
            STONE_PICKAXE(274, ItemType.PICKAXE),
            STONE_AXE(275, ItemType.AXE),
            DIAMOND_SWORD(276, ItemType.SWORD),
            DIAMOND_SHOVEL(277, ItemType.SHOVEL),
            DIAMOND_PICKAXE(278, ItemType.PICKAXE),
            DIAMOND_AXE(279, ItemType.AXE),
            STICK(280),
            GOLDEN_SWORD(283, ItemType.SWORD),
            GOLDEN_SHOVEL(284, ItemType.SHOVEL),
            GOLDEN_PICKAXE(285, ItemType.PICKAXE),
            GOLDEN_AXE(286, ItemType.AXE),
            WOODEN_HOE(290, ItemType.HOE),
            STONE_HOE(291, ItemType.HOE),
            IRON_HOE(292, ItemType.HOE),
            DIAMOND_HOE(293, ItemType.HOE),
            GOLDEN_HOE(294, ItemType.HOE);
            
            
            
            
            
/*
            Flint_And_Steel = 259,
            Lighter = 259,
            Apple = 260,
            Bow = 261,
            Arrow = 262,
            Coal = 263,
            Diamond = 264,
            Iron_Ingot = 265,
            Gold_Ingot = 266,
            IRON_SWORD = 267,

/*
            STICK = 280,
            Bowl = 281,
            Mushroom_Soup = 282,

            Gold_Sword = 283,
            Gold_Spade = 284,
            Gold_Pick = 285,
            Gold_Pickaxe = 285,
            Gold_Axe = 286,

            Bow_String = 287,
            Feather = 288,
            Gunpowder = 289,

            WOODEN_HOE = 290,
            STONE_HOE = 291,
            IRON_HOE = 292,
            DIAMOND_HOE = 293,
            Gold_Hoe = 294,

            Seeds = 295,
            Wheat = 296,
            Bread = 297,

            Leather_Helmet = 298,
            Leather_Chestplate = 299,
            Leather_Pants = 300,
            Leather_Boots = 301,

            Chainmail_Helmet = 302,
            Chainmail_Chestplate = 303,
            Chainmail_Pants = 304,
            Chainmail_Boots = 305,

            Iron_Helmet = 306,
            Iron_Chestplate = 307,
            Iron_Pants = 308,
            Iron_Boots = 309,

            Diamond_Helmet = 310,
            Diamond_Chestplate = 311,
            Diamond_Pants = 312,
            Diamond_Boots = 313,

            Gold_Helmet = 314,
            Gold_Chestplate = 315,
            Gold_Pants = 316,
            Gold_Boots = 317,

            Flint = 318,
            Pork = 319,
            Grilled_Pork = 320,
            Paintings = 321,
            Golden_Apple = 322,
            Sign = 323,
            Wooden_Door = 324,
            Bucket = 325,
            Water_Bucket = 326,
            Lava_Bucket = 327,
            Mine_Cart = 328,
            Saddle = 329,
            Iron_Door = 330,
            Redstone = 331,
            Snowball = 332,
            Boat = 333,
            Leather = 334,
            Milk_Bucket = 335,
            Clay_Brick = 336,
            Clay_Balls = 337,
            Reeds = 338,
            Paper = 339,
            Book = 340,
            Slime_Ball = 341,
            Storage_Minecart = 342,
            Powered_Minecart = 343,
            Egg = 344,
            Compas = 345,
            Fishing_Rod = 346,

            Gold_Record = 2256,
            Green_Record = 2257    
            */
            
                
            private final int id;
            private final ItemType type;
            
            Id(int id)
            {
                this.id = id;
                this.type = ItemType.OTHER;
                
            }
            
            Id(int id, ItemType type)
            {
                this.id = id;
                this.type = type;
            }
            
            public int getValue()
            {
                return id;
            }
            
            public byte getByteValue()
            {
                return (byte)id;
            }

            public ItemType getType() {
                return type;
            }
            
}