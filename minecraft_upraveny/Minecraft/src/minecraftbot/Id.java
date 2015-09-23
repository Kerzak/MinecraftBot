package minecraftbot;

/**
 * Block identifier.
 */
public enum Id {

            
            None(-1),
            Begin(-2),
            End(-3),
            // Block Tiles 
            Air(0),
            Stone(1, ItemType.Rock),
            Grass (2, ItemType.Ground),
            Dirt (3, ItemType.Ground),
            Cobblestone (4, ItemType.Rock),
            Wood (5, ItemType.Wood),
            Sapling (6, ItemType.Plant),
            Bedrock (7),
            Water (8, ItemType.Liquid),
            StillWater (9, ItemType.Liquid),
            Lava (10, ItemType.Liquid),
            StillLava (11, ItemType.Liquid),
            Sand (12, ItemType.Ground),
            Gravel (13, ItemType.Ground),
            GoldOre (14),
            IronOre (15),
            CoalOre (16),
            Log (17, ItemType.Wood),
            Leaves (18, ItemType.Leaves),
            Sponge(19),
            Glass (20, ItemType.Glass),
            Lapis_Azuli_Ore(21, ItemType.Rock),
            Sandstone(24,ItemType.Rock),
            CobWeb (30, ItemType.Plant),
            GrassPlant (31, ItemType.Plant),
            Wool (35, ItemType.Wool),
            Flower (37, ItemType.Plant),
            Rose (38, ItemType.Plant),
            Mushroom (39),
            Red_Mushroom (40),
            GoldBlock (41),
            IronBlock (42),
            Double_Stair (43),
            Stair (44),
            Brick (45),
            TNT (46),
            Bookshelf (47, ItemType.Wood),
            MossStone (48, ItemType.Rock),
            MossyCobblestone (49, ItemType.Rock),
            Obsidian (49),
            Torch (50),
            Fire (51),
            MobSpawner (52),
            WoodenStairs (53),
            Chest (54),
            RedstoneWire (55),
            DiamondOre (56),
            DiamondBlock (57),
            Workbench (58),
            Crops (59),
            Soil (60),
            Furnace (61),
            BurningFurnace (62),
            SignPost (63),
            WoodenDoor (64, ItemType.Door),
            Ladder (65),
            Rails (66),
            Stairs (67),
            Lever (69),
            StonePressure_Plate (70),
            WoodenPressure_Plate ( 72),
            RedstoneOre (73),
            RestoneTorch (76),
            StoneButton (77),
            Snow (78, ItemType.Snow),
            Ice (79, ItemType.Ice),
            SnowBlock (80),
            Cactus ( 81),
            Clay (82),
            Reed (83),
            Jukebox(84),
            Fence (85),
            MonsterEgg (97,ItemType.Rock),
            GlassPane (102),
            BrickStairs (108),
            EmeraldOre (129, ItemType.Rock),
            Carrots (141,ItemType.Plant),
            Potatoes (142,ItemType.Plant),
            AcaciaLeaves (161, ItemType.Leaves),
            AcaciaWood (162, ItemType.Wood),
            Sunflower (175, ItemType.Plant),

            //Items
            IronShovel(256, ItemType.Shovel),
            IronPickaxe(257, ItemType.Pickaxe),
            IronAxe(258, ItemType.Axe),
            Apple(260),
            IronSword(267, ItemType.Sword),
            WoodenSword(268, ItemType.Sword),
            WoodenShovel(269, ItemType.Shovel),
            WoodenPickaxe(270, ItemType.Pickaxe),
            WoodenAxe(271, ItemType.Axe),
            StoneSword(272, ItemType.Sword),
            StoneShovel(273, ItemType.Shovel),
            StonePickaxe(274, ItemType.Pickaxe),
            StoneAxe(275, ItemType.Axe),
            DiamondSword(276, ItemType.Sword),
            DiamondShovel(277, ItemType.Shovel),
            DiamondPickaxe(278, ItemType.Pickaxe),
            DiamondAxe(279, ItemType.Axe),
            Stick(280),
            GoldenSword(283, ItemType.Sword),
            GoldenShovel(284, ItemType.Shovel),
            GoldenPickaxe(285, ItemType.Pickaxe),
            GoldenAxe(286, ItemType.Axe),
            WoodenHoe(290, ItemType.Hoe),
            StoneHoe(291, ItemType.Hoe),
            IronHoe(292, ItemType.Hoe),
            DiamondHoe(293, ItemType.Hoe),
            GoldenHoe(294, ItemType.Hoe),
            
            Bow(261, ItemType.Bow),
            
            
            
            

            FlintAndSteel(259),
            Lighter(259),
            Arrow( 262),
            Coal ( 263),
            Diamond ( 264),
            IronIngot ( 265),
            GoldIngot ( 266),


            Bowl ( 281),
            Mushroom_Soup ( 282),

            GoldSword ( 283, ItemType.Sword),
            GoldSpade  (284),
            GoldPickaxe ( 285,ItemType.Pickaxe),
            GoldAxe ( 286,ItemType.Axe),

            Bow_String ( 287),
            Feather ( 288),
            Gunpowder ( 289),

            Wooden_Hoe ( 290),
            Stone_Hoe ( 291),
            Iron_Hoe (292),
            Diamond_Hoe ( 293),
            Gold_Hoe( 294),

            Seeds ( 295),
            Wheat (296),
            Bread ( 297),

            LeatherHelmet ( 298),
            LeatherChestplate ( 299),
            LeatherPants ( 300),
            LeatherBoots ( 301),

            ChainmailHelmet ( 302),
            ChainmailChestplate ( 303),
            ChainmailPants( 304),
            ChainmailBoots ( 305),

            IronHelmet (306),
            IronChestplate ( 307),
            IronPants (308),
            IronBoots ( 309),

            DiamondHelmet ( 310),
            DiamondChestplate ( 311),
            DiamondPants (312),
            DiamondBoots ( 313),

            GoldHelmet (314),
            GoldChestplate ( 315),
            GoldPants (316),
            GoldBoots (317),

            Flint ( 318),
            Pork( 319),
            GrilledPork (320),
            Paintings ( 321),
            GoldenApple ( 322),
            Sign ( 323),
            Wooden_Door ( 324),
            Bucket (325),
            WaterBucket ( 326),
            LavaBucket ( 327),
            MineCart (328),
            Saddle ( 329),
            IronDoor( 330),
            Redstone ( 331),
            Snowball ( 332),
            Boat ( 333),
            Leather ( 334),
            MilkBucket( 335),
            ClayBrick( 336),
            ClayBalls ( 337),
            Reeds (338),
            Paper ( 339),
            Book ( 340),
            SlimeBall ( 341),
            StorageMinecart ( 342),
            PoweredMinecart ( 343),
            Egg(344),
            Compas ( 345),
            Fishing_Rod ( 346),

            Gold_Record( 2256),
            Green_Record( 2257 );   
            
            
                
            private final int id;
            private final ItemType type;
            
            Id(int id)
            {
                this.id = id;
                this.type = ItemType.Other;
            }
            
            Id(int id, ItemType type)
            {
                this.id = id;
                this.type = type;
            }
            
            /**
             * @return Numerical value of Id defined by protocol.
             */
            public int getValue()
            {
                return id;
            }
            
            /**
             * @return Byte value of Id defined by protocol.
             */
            public byte getByteValue()
            {
                return (byte)id;
            }

            /**
             * @return Type of item with this Id.
             */
            public ItemType getType() {
                return type;
            }
            
            
}
