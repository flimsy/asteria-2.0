package server.world.entity.player.skill.impl;

import java.util.HashMap;
import java.util.Map;

import server.core.Rs2Engine;
import server.core.worker.Worker;
import server.util.Misc;
import server.util.Misc.Chance;
import server.world.entity.Animation;
import server.world.entity.player.Player;
import server.world.entity.player.skill.SkillEvent;
import server.world.entity.player.skill.SkillManager.SkillConstant;
import server.world.entity.player.skill.impl.Smithing.SmithingTable.TableIndex;
import server.world.item.Item;

@SuppressWarnings("unused")
public class Smithing extends SkillEvent {

    // TODO: finish this already.. fuck lol
    // FIXME: finish smelting interface and test it
    // FIXME: smithing exp!! - done, needs testing!
    // FIXME: smelting interface
    // FIXME: amount of bars doesn't show for battleaxe (on steel, mith and run)

    /**
     * The singleton instance.
     */
    private static Smithing singleton;

    /**
     * The animation played when the player smelts ore.
     */
    private static final Animation SMELT_ANIMATION = new Animation(899);

    /**
     * The animation played when the player makes armor or weapons.
     */
    private static final Animation SMITH_ANIMATION = new Animation(898);

    /**
     * All of the possible bars that can be smelted using a furnace.
     * 
     * @author lare96
     */
    public enum Smelt {
        BRONZE(1, 100, Chance.ALWAYS, 2349, new Item(438), new Item(436)),
        IRON(15, 150, Chance.SOMETIMES, 2351, new Item(440)),
        SILVER(20, 400, Chance.ALWAYS, 2355, new Item(442)),
        STEEL(30, 400, Chance.ALWAYS, 2353, new Item(440), new Item(453, 2)),
        GOLD(40, 450, Chance.ALWAYS, 2357, new Item(444)),
        MITHRIL(50, 700, Chance.ALWAYS, 2359, new Item(447), new Item(453, 4)),
        ADAMANT(70, 900, Chance.ALWAYS, 2361, new Item(449), new Item(453, 6)),
        RUNE(85, 1200, Chance.ALWAYS, 2363, new Item(451), new Item(453, 8));

        /**
         * The level needed to be able to smelt this bar.
         */
        private int level;

        /**
         * The experience awarded for smelting this bar.
         */
        private int experience;

        /**
         * Chance of smelting this bar.
         */
        private Chance successChance;

        /**
         * The bar received after you successfully smelt the ore.
         */
        private int bar;

        /**
         * The items needed to smelt this bar.
         */
        private Item[] itemsNeeded;

        /**
         * Construct this data.
         * 
         * @param level
         *        the level needed to be able to smelt this bar.
         * @param experience
         *        the experience awarded for smelting this bar.
         * @param successChance
         *        the chance of smelting this bar.
         * @param bar
         *        the bar received after you successfully smelt the ore.
         * @param itemsNeeded
         *        the items needed to smelt this bar.
         */
        Smelt(int level, int experience, Chance successChance, int bar, Item... itemsNeeded) {
            this.setLevel(level);
            this.setExperience(experience);
            this.setSuccessChance(successChance);
            this.setBar(bar);
            this.setItemsNeeded(itemsNeeded);
        }

        /**
         * The map that allows us to retrieve a constant by its id.
         */
        private static Map<Integer, Smelt> smelt = new HashMap<Integer, Smelt>();

        /**
         * Begins loading the data for this enum.
         */
        static {
            for (Smelt s : Smelt.values()) {
                smelt.put(s.getBar(), s);
            }
        }

        /**
         * Gets the instance by its id.
         * 
         * @param id
         *        the id to get the instance of.
         * @return the instance.
         */
        public static Smelt forId(int id) {
            return smelt.get(id);
        }

        /**
         * @return the level.
         */
        public int getLevel() {
            return level;
        }

        /**
         * @param level
         *        the level to set.
         */
        public void setLevel(int level) {
            this.level = level;
        }

        /**
         * @return the experience.
         */
        public int getExperience() {
            return experience;
        }

        /**
         * @param experience
         *        the experience to set.
         */
        public void setExperience(int experience) {
            this.experience = experience;
        }

        /**
         * @return the successChance.
         */
        public Chance getSuccessChance() {
            return successChance;
        }

        /**
         * @param successChance
         *        the successChance to set.
         */
        public void setSuccessChance(Chance successChance) {
            this.successChance = successChance;
        }

        /**
         * @return the itemsNeeded.
         */
        public Item[] getItemsNeeded() {
            return itemsNeeded;
        }

        /**
         * @param itemsNeeded
         *        the itemsNeeded to set.
         */
        public void setItemsNeeded(Item[] itemsNeeded) {
            this.itemsNeeded = itemsNeeded;
        }

        /**
         * @return the bar.
         */
        public int getBar() {
            return bar;
        }

        /**
         * @param bar
         *        the bar to set.
         */
        public void setBar(int bar) {
            this.bar = bar;
        }
    }

    /**
     * All of the possible bars that can be made into armor.
     * 
     * @author lare96
     */
    public enum Smith {
        BRONZE(2349, new SmithingTable().setTableItem(TableIndex.DAGGER, new SmithingTableItem(new Item(1205), 1, 1, 1125, 1094, 150)).setTableItem(TableIndex.AXE, new SmithingTableItem(new Item(1351), 1, 1, 1126, 1091, 150)).setTableItem(TableIndex.CHAIN_BODY, new SmithingTableItem(new Item(1103), 11, 3, 1109, 1098, 250)).setTableItem(TableIndex.MED_HELM, new SmithingTableItem(new Item(1139), 3, 1, 1127, 1102, 150)).setTableItem(TableIndex.DART_TIPS, new SmithingTableItem(new Item(819, 10), 4, 1, 1124, 1107, 150)).setTableItem(TableIndex.SWORD, new SmithingTableItem(new Item(1277), 3, 1, 1128, 1085, 150)).setTableItem(TableIndex.MACE, new SmithingTableItem(new Item(1422), 2, 1, 1129, 1093, 150)).setTableItem(TableIndex.PLATELEGS, new SmithingTableItem(new Item(1075), 16, 3, 1110, 1099, 250)).setTableItem(TableIndex.FULL_HELM, new SmithingTableItem(new Item(1155), 7, 2, 1113, 1103, 150)).setTableItem(TableIndex.ARROW_TIPS, new SmithingTableItem(new Item(39, 15), 5, 1, 1130, 1108, 150)).setTableItem(TableIndex.SCIMITAR, new SmithingTableItem(new Item(1321), 5, 2, 1116, 1087, 200)).setTableItem(TableIndex.WARHAMMER, new SmithingTableItem(new Item(1337), 9, 3, 1118, 1083, 250)).setTableItem(TableIndex.PLATESKIRT, new SmithingTableItem(new Item(1087), 16, 3, 1111, 1100, 250)).setTableItem(TableIndex.SQ_SHIELD, new SmithingTableItem(new Item(1173), 8, 2, 1114, 1104, 200)).setTableItem(TableIndex.KNIVES, new SmithingTableItem(new Item(864, 5), 7, 1, 1131, 1106, 150)).setTableItem(TableIndex.LONGSWORD, new SmithingTableItem(new Item(1291), 6, 2, 1089, 1086, 200)).setTableItem(TableIndex.BATTLE_AXE, new SmithingTableItem(new Item(1375), 10, 3, 1095, 1092, 250)).setTableItem(TableIndex.PLATEBODY, new SmithingTableItem(new Item(1117), 18, 5, 1112, 1101, 300)).setTableItem(TableIndex.KITESHIELD, new SmithingTableItem(new Item(1189), 12, 3, 1115, 1105, 250)).setTableItem(TableIndex.WIRE, new SmithingTableItem(new Item(1794), 4, 1, 1132, 1096, 150)).setTableItem(TableIndex.TWO_HANDED_SWORD, new SmithingTableItem(new Item(1307), 14, 3, 1090, 1088, 250)).setTableItem(TableIndex.CLAWS, new SmithingTableItem(new Item(3095), 13, 2, 8428, 8429, 200)).setTableItem(TableIndex.OIL_FRAME_LANTERN, new SmithingTableItem(null, 0, 1, 11459, 11461, -1)).setTableItem(TableIndex.NAILS, new SmithingTableItem(new Item(4819, 15), 4, 1, 13357, 13358, 150)).setTableItem(TableIndex.STUDS, new SmithingTableItem(null, 0, 1, 1135, 1134, -1))),
        IRON(2351, new SmithingTable().setTableItem(TableIndex.DAGGER, new SmithingTableItem(new Item(1203), 15, 1, 1125, 1094, 200)).setTableItem(TableIndex.AXE, new SmithingTableItem(new Item(1349), 16, 1, 1126, 1091, 200)).setTableItem(TableIndex.CHAIN_BODY, new SmithingTableItem(new Item(1101), 26, 3, 1109, 1098, 300)).setTableItem(TableIndex.MED_HELM, new SmithingTableItem(new Item(1137), 18, 1, 1127, 1102, 200)).setTableItem(TableIndex.DART_TIPS, new SmithingTableItem(new Item(820, 10), 19, 1, 1124, 1107, 200)).setTableItem(TableIndex.SWORD, new SmithingTableItem(new Item(1279), 19, 1, 1128, 1085, 200)).setTableItem(TableIndex.MACE, new SmithingTableItem(new Item(1420), 17, 1, 1129, 1093, 200)).setTableItem(TableIndex.PLATELEGS, new SmithingTableItem(new Item(1067), 31, 3, 1110, 1099, 300)).setTableItem(TableIndex.FULL_HELM, new SmithingTableItem(new Item(1153), 22, 2, 1113, 1103, 200)).setTableItem(TableIndex.ARROW_TIPS, new SmithingTableItem(new Item(40, 15), 20, 1, 1130, 1108, 200)).setTableItem(TableIndex.SCIMITAR, new SmithingTableItem(new Item(1323), 20, 2, 1116, 1087, 250)).setTableItem(TableIndex.WARHAMMER, new SmithingTableItem(new Item(1335), 24, 3, 1118, 1083, 300)).setTableItem(TableIndex.PLATESKIRT, new SmithingTableItem(new Item(1081), 31, 3, 1111, 1100, 300)).setTableItem(TableIndex.SQ_SHIELD, new SmithingTableItem(new Item(1175), 23, 2, 1114, 1104, 250)).setTableItem(TableIndex.KNIVES, new SmithingTableItem(new Item(863, 5), 22, 1, 1131, 1106, 200)).setTableItem(TableIndex.LONGSWORD, new SmithingTableItem(new Item(1293), 21, 2, 1089, 1086, 250)).setTableItem(TableIndex.BATTLE_AXE, new SmithingTableItem(new Item(1363), 25, 3, 1095, 1092, 300)).setTableItem(TableIndex.PLATEBODY, new SmithingTableItem(new Item(1115), 33, 5, 1112, 1101, 500)).setTableItem(TableIndex.KITESHIELD, new SmithingTableItem(new Item(1191), 27, 3, 1115, 1105, 300)).setTableItem(TableIndex.WIRE, new SmithingTableItem(null, 4, 1, 1132, 1096, -1)).setTableItem(TableIndex.TWO_HANDED_SWORD, new SmithingTableItem(new Item(1309), 29, 3, 1090, 1088, 300)).setTableItem(TableIndex.CLAWS, new SmithingTableItem(new Item(3096), 28, 2, 8428, 8429, 250)).setTableItem(TableIndex.OIL_FRAME_LANTERN, new SmithingTableItem(new Item(4540), 26, 1, 11459, 11461, 200)).setTableItem(TableIndex.NAILS, new SmithingTableItem(new Item(4820, 15), 19, 1, 13357, 13358, 200)).setTableItem(TableIndex.STUDS, new SmithingTableItem(null, 0, 1, 1135, 1134, -1))),
        STEEL(2353, new SmithingTable().setTableItem(TableIndex.DAGGER, new SmithingTableItem(new Item(1207), 30, 1, 1125, 1094, 250)).setTableItem(TableIndex.AXE, new SmithingTableItem(new Item(1353), 31, 1, 1126, 1091, 250)).setTableItem(TableIndex.CHAIN_BODY, new SmithingTableItem(new Item(1105), 41, 3, 1109, 1098, 350)).setTableItem(TableIndex.MED_HELM, new SmithingTableItem(new Item(1141), 33, 1, 1127, 1102, 250)).setTableItem(TableIndex.DART_TIPS, new SmithingTableItem(new Item(821, 10), 34, 1, 1124, 1107, 250)).setTableItem(TableIndex.SWORD, new SmithingTableItem(new Item(1281), 34, 1, 1128, 1085, 250)).setTableItem(TableIndex.MACE, new SmithingTableItem(new Item(1424), 32, 1, 1129, 1093, 250)).setTableItem(TableIndex.PLATELEGS, new SmithingTableItem(new Item(1069), 46, 3, 1110, 1099, 350)).setTableItem(TableIndex.FULL_HELM, new SmithingTableItem(new Item(1157), 37, 2, 1113, 1103, 250)).setTableItem(TableIndex.ARROW_TIPS, new SmithingTableItem(new Item(41, 15), 35, 1, 1130, 1108, 250)).setTableItem(TableIndex.SCIMITAR, new SmithingTableItem(new Item(1325), 35, 2, 1116, 1087, 300)).setTableItem(TableIndex.WARHAMMER, new SmithingTableItem(new Item(1339), 39, 3, 1118, 1083, 350)).setTableItem(TableIndex.PLATESKIRT, new SmithingTableItem(new Item(1083), 46, 3, 1111, 1100, 350)).setTableItem(TableIndex.SQ_SHIELD, new SmithingTableItem(new Item(1177), 38, 2, 1114, 1104, 300)).setTableItem(TableIndex.KNIVES, new SmithingTableItem(new Item(865, 5), 37, 1, 1131, 1106, 250)).setTableItem(TableIndex.LONGSWORD, new SmithingTableItem(new Item(1295), 36, 2, 1089, 1086, 300)).setTableItem(TableIndex.BATTLE_AXE, new SmithingTableItem(new Item(1365), 40, 3, 1095, 1092, 350)).setTableItem(TableIndex.PLATEBODY, new SmithingTableItem(new Item(1119), 48, 5, 1112, 1101, 700)).setTableItem(TableIndex.KITESHIELD, new SmithingTableItem(new Item(1193), 42, 3, 1115, 1105, 350)).setTableItem(TableIndex.WIRE, new SmithingTableItem(null, 4, 1, 1095, 1132, -1)).setTableItem(TableIndex.TWO_HANDED_SWORD, new SmithingTableItem(new Item(1311), 44, 3, 1090, 1088, 350)).setTableItem(TableIndex.CLAWS, new SmithingTableItem(new Item(3097), 43, 2, 8428, 8429, 300)).setTableItem(TableIndex.OIL_FRAME_LANTERN, new SmithingTableItem(new Item(4544), 49, 1, 11459, 11461, 250)).setTableItem(TableIndex.NAILS, new SmithingTableItem(new Item(1539, 15), 34, 1, 13357, 13358, 250)).setTableItem(TableIndex.STUDS, new SmithingTableItem(new Item(2370), 36, 1, 1135, 1134, 250))),
        MITHRIL(2359, new SmithingTable().setTableItem(TableIndex.DAGGER, new SmithingTableItem(new Item(1209), 50, 1, 1125, 1094, 300)).setTableItem(TableIndex.AXE, new SmithingTableItem(new Item(1355), 51, 1, 1126, 1091, 300)).setTableItem(TableIndex.CHAIN_BODY, new SmithingTableItem(new Item(1109), 61, 3, 1109, 1098, 400)).setTableItem(TableIndex.MED_HELM, new SmithingTableItem(new Item(1143), 53, 1, 1127, 1102, 300)).setTableItem(TableIndex.DART_TIPS, new SmithingTableItem(new Item(822, 10), 54, 1, 1124, 1107, 300)).setTableItem(TableIndex.SWORD, new SmithingTableItem(new Item(1285), 53, 1, 1128, 1085, 300)).setTableItem(TableIndex.MACE, new SmithingTableItem(new Item(1428), 52, 1, 1129, 1093, 300)).setTableItem(TableIndex.PLATELEGS, new SmithingTableItem(new Item(1071), 66, 3, 1110, 1099, 400)).setTableItem(TableIndex.FULL_HELM, new SmithingTableItem(new Item(1159), 57, 2, 1113, 1103, 300)).setTableItem(TableIndex.ARROW_TIPS, new SmithingTableItem(new Item(42, 15), 55, 1, 1130, 1108, 300)).setTableItem(TableIndex.SCIMITAR, new SmithingTableItem(new Item(1329), 55, 2, 1116, 1087, 350)).setTableItem(TableIndex.WARHAMMER, new SmithingTableItem(new Item(1343), 59, 3, 1118, 1083, 400)).setTableItem(TableIndex.PLATESKIRT, new SmithingTableItem(new Item(1085), 66, 3, 1111, 1100, 400)).setTableItem(TableIndex.SQ_SHIELD, new SmithingTableItem(new Item(1181), 58, 2, 1114, 1104, 350)).setTableItem(TableIndex.KNIVES, new SmithingTableItem(new Item(866, 5), 57, 1, 1131, 1106, 300)).setTableItem(TableIndex.LONGSWORD, new SmithingTableItem(new Item(1299), 56, 2, 1089, 1086, 350)).setTableItem(TableIndex.BATTLE_AXE, new SmithingTableItem(new Item(1369), 60, 3, 1095, 1092, 400)).setTableItem(TableIndex.PLATEBODY, new SmithingTableItem(new Item(1121), 68, 5, 1112, 1101, 900)).setTableItem(TableIndex.KITESHIELD, new SmithingTableItem(new Item(1197), 62, 3, 1115, 1105, 400)).setTableItem(TableIndex.WIRE, new SmithingTableItem(null, 4, 1, 1095, 1132, -1)).setTableItem(TableIndex.TWO_HANDED_SWORD, new SmithingTableItem(new Item(1315), 64, 3, 1090, 1088, 400)).setTableItem(TableIndex.CLAWS, new SmithingTableItem(new Item(3099), 63, 2, 8428, 8429, 350)).setTableItem(TableIndex.OIL_FRAME_LANTERN, new SmithingTableItem(null, 0, 1, 11459, 11461, -1)).setTableItem(TableIndex.NAILS, new SmithingTableItem(new Item(4822, 15), 54, 1, 13357, 13358, 300)).setTableItem(TableIndex.STUDS, new SmithingTableItem(null, 0, 1, 1135, 1134, -1))),
        ADAMANT(2361, new SmithingTable().setTableItem(TableIndex.DAGGER, new SmithingTableItem(new Item(1211), 70, 1, 1125, 1094, 350)).setTableItem(TableIndex.AXE, new SmithingTableItem(new Item(1357), 71, 1, 1126, 1091, 350)).setTableItem(TableIndex.CHAIN_BODY, new SmithingTableItem(new Item(1111), 81, 3, 1109, 1098, 450)).setTableItem(TableIndex.MED_HELM, new SmithingTableItem(new Item(1145), 73, 1, 1127, 1102, 350)).setTableItem(TableIndex.DART_TIPS, new SmithingTableItem(new Item(823, 10), 74, 1, 1124, 1107, 350)).setTableItem(TableIndex.SWORD, new SmithingTableItem(new Item(1287), 74, 1, 1128, 1085, 350)).setTableItem(TableIndex.MACE, new SmithingTableItem(new Item(1430), 72, 1, 1129, 1093, 350)).setTableItem(TableIndex.PLATELEGS, new SmithingTableItem(new Item(1073), 86, 3, 1110, 1099, 450)).setTableItem(TableIndex.FULL_HELM, new SmithingTableItem(new Item(1161), 77, 2, 1113, 1103, 350)).setTableItem(TableIndex.ARROW_TIPS, new SmithingTableItem(new Item(43, 15), 75, 1, 1130, 1108, 350)).setTableItem(TableIndex.SCIMITAR, new SmithingTableItem(new Item(1331), 75, 2, 1116, 1087, 400)).setTableItem(TableIndex.WARHAMMER, new SmithingTableItem(new Item(1345), 79, 3, 1118, 1083, 450)).setTableItem(TableIndex.PLATESKIRT, new SmithingTableItem(new Item(1091), 86, 3, 1111, 1100, 450)).setTableItem(TableIndex.SQ_SHIELD, new SmithingTableItem(new Item(1183), 78, 2, 1114, 1104, 400)).setTableItem(TableIndex.KNIVES, new SmithingTableItem(new Item(867, 5), 77, 1, 1131, 1106, 350)).setTableItem(TableIndex.LONGSWORD, new SmithingTableItem(new Item(1301), 76, 2, 1089, 1086, 400)).setTableItem(TableIndex.BATTLE_AXE, new SmithingTableItem(new Item(1371), 80, 3, 1095, 1092, 450)).setTableItem(TableIndex.PLATEBODY, new SmithingTableItem(new Item(1123), 88, 5, 1112, 1101, 1100)).setTableItem(TableIndex.KITESHIELD, new SmithingTableItem(new Item(1199), 82, 3, 1115, 1105, 450)).setTableItem(TableIndex.WIRE, new SmithingTableItem(null, 4, 1, 1095, 1132, -1)).setTableItem(TableIndex.TWO_HANDED_SWORD, new SmithingTableItem(new Item(1317), 84, 3, 1090, 1088, 450)).setTableItem(TableIndex.CLAWS, new SmithingTableItem(new Item(3100), 83, 2, 8428, 8429, 400)).setTableItem(TableIndex.OIL_FRAME_LANTERN, new SmithingTableItem(null, 0, 1, 11459, 11461, -1)).setTableItem(TableIndex.NAILS, new SmithingTableItem(new Item(4823, 15), 74, 1, 13357, 13358, 350)).setTableItem(TableIndex.STUDS, new SmithingTableItem(null, 0, 1, 1135, 1134, -1))),
        RUNE(2363, new SmithingTable().setTableItem(TableIndex.DAGGER, new SmithingTableItem(new Item(1213), 85, 1, 1125, 1094, 400)).setTableItem(TableIndex.AXE, new SmithingTableItem(new Item(1359), 86, 1, 1126, 1091, 400)).setTableItem(TableIndex.CHAIN_BODY, new SmithingTableItem(new Item(1113), 96, 3, 1109, 1098, 500)).setTableItem(TableIndex.MED_HELM, new SmithingTableItem(new Item(1147), 88, 1, 1127, 1102, 400)).setTableItem(TableIndex.DART_TIPS, new SmithingTableItem(new Item(824, 10), 89, 1, 1124, 1107, 400)).setTableItem(TableIndex.SWORD, new SmithingTableItem(new Item(1289), 89, 1, 1128, 1085, 400)).setTableItem(TableIndex.MACE, new SmithingTableItem(new Item(1432), 87, 1, 1129, 1093, 400)).setTableItem(TableIndex.PLATELEGS, new SmithingTableItem(new Item(1079), 99, 3, 1110, 1099, 500)).setTableItem(TableIndex.FULL_HELM, new SmithingTableItem(new Item(1163), 92, 2, 1113, 1103, 400)).setTableItem(TableIndex.ARROW_TIPS, new SmithingTableItem(new Item(44, 15), 90, 1, 1130, 1108, 400)).setTableItem(TableIndex.SCIMITAR, new SmithingTableItem(new Item(1333), 90, 2, 1116, 1087, 450)).setTableItem(TableIndex.WARHAMMER, new SmithingTableItem(new Item(1347), 94, 3, 1118, 1083, 500)).setTableItem(TableIndex.PLATESKIRT, new SmithingTableItem(new Item(1093), 99, 3, 1111, 1100, 500)).setTableItem(TableIndex.SQ_SHIELD, new SmithingTableItem(new Item(1185), 93, 2, 1114, 1104, 450)).setTableItem(TableIndex.KNIVES, new SmithingTableItem(new Item(868, 5), 92, 1, 1131, 1106, 400)).setTableItem(TableIndex.LONGSWORD, new SmithingTableItem(new Item(1303), 91, 2, 1089, 1086, 450)).setTableItem(TableIndex.BATTLE_AXE, new SmithingTableItem(new Item(1373), 95, 3, 1095, 1092, 500)).setTableItem(TableIndex.PLATEBODY, new SmithingTableItem(new Item(1127), 99, 5, 1112, 1101, 1300)).setTableItem(TableIndex.KITESHIELD, new SmithingTableItem(new Item(1201), 97, 3, 1115, 1105, 500)).setTableItem(TableIndex.WIRE, new SmithingTableItem(null, 4, 1, 1095, 1132, -1)).setTableItem(TableIndex.TWO_HANDED_SWORD, new SmithingTableItem(new Item(1319), 99, 3, 1090, 1088, 500)).setTableItem(TableIndex.CLAWS, new SmithingTableItem(new Item(3101), 98, 2, 8428, 8429, 450)).setTableItem(TableIndex.OIL_FRAME_LANTERN, new SmithingTableItem(null, 0, 1, 11459, 11461, -1)).setTableItem(TableIndex.NAILS, new SmithingTableItem(new Item(4824, 15), 89, 1, 13357, 13358, 400)).setTableItem(TableIndex.STUDS, new SmithingTableItem(null, 0, 1, 1135, 1134, -1)));

        /**
         * The id of the bar being made into armor.
         */
        private int barId;

        /**
         * The smithing table for this bar.
         */
        private SmithingTable smithingTable;

        /**
         * Create a new smithing constant.
         * 
         * @param barId
         *        the id of the bar being made into armor.
         * @param smithingTable
         *        the smithing table for this bar.
         */
        Smith(int barId, SmithingTable smithingTable) {
            this.setBarId(barId);
            this.setSmithingTable(smithingTable);
        }

        /**
         * A map of items and the corresponding smithing indexes.
         */
        private static Map<Integer, SmithingTableItem> index = new HashMap<Integer, SmithingTableItem>();

        /**
         * A map of items and the corresponding smithing tables.
         */
        private static Map<Integer, Smith> table = new HashMap<Integer, Smith>();

        /**
         * Loads the two maps.
         */
        static {
            for (Smith s : Smith.values()) {
                for (SmithingTableItem item : s.getSmithingTable().getTable()) {
                    if (item.getItemCreated() == null) {
                        continue;
                    }

                    index.put(item.getItemCreated().getId(), item);
                    table.put(item.getItemCreated().getId(), s);
                }
            }
        }

        /**
         * Gets the table index for this item.
         * 
         * @param item
         *        the item to get the table index for.
         * @return the table index.
         */
        public static SmithingTableItem getIndex(int item) {
            return index.get(item);
        }

        /**
         * Gets the table for this item.
         * 
         * @param item
         *        the item to get the table for.
         * @return the table.
         */
        public static Smith getTable(int item) {
            return table.get(item);
        }

        /**
         * @return the bar.
         */
        public int getBarId() {
            return barId;
        }

        /**
         * @param bar
         *        the bar to set.
         */
        public void setBarId(int barId) {
            this.barId = barId;
        }

        /**
         * @return the smithingTable.
         */
        public SmithingTable getSmithingTable() {
            return smithingTable;
        }

        /**
         * @param smithingTable
         *        the smithingTable to set.
         */
        public void setSmithingTable(SmithingTable smithingTable) {
            this.smithingTable = smithingTable;
        }
    }

    /**
     * Opens the smelting interface in the chatbox.
     * 
     * @param player
     *        the player to open the smelting interface for.
     */
    public void smeltInterface(Player player) {
        // private final int[] SMELT_BARS =
        // {2349,2351,2355,2353,2357,2359,2361,2363};
        // private final int[] SMELT_FRAME =
        // {2405,2406,2407,2409,2410,2411,2412,2413};
        // for (int j = 0; j < SMELT_FRAME.length; j++) {
        // c.getPA().sendFrame246(SMELT_FRAME[j], 150, SMELT_BARS[j]);
        // }
        player.getPacketBuilder().sendChatInterface(2400);
    }

    /**
     * Opens the smithing interface.
     * 
     * @param player
     *        the player to open the smithing interface for.
     * @param smith
     *        the bar you are smithing.
     */
    public void smithInterface(Player player, Smith smith) {
        player.getPacketBuilder().sendInterface(994);

        for (SmithingTableItem s : smith.getSmithingTable().getTable()) {
            if (s.getItemCreated() == null) {
                player.getPacketBuilder().sendString("", s.getSendNameTextLine());
                player.getPacketBuilder().sendString("", s.getSendAmountOfBarsTextLine());
                player.getPacketBuilder().sendItemOnInterfaceSlot(s.getIndex().getFrame(), new Item(-1), s.getIndex().getSlot());
                continue;
            }

            if (player.getInventory().getContainer().getCount(smith.getBarId()) < s.getBarsRequired()) {
                player.getPacketBuilder().sendString(s.getBarsRequired() == 1 ? "@red@" + s.getBarsRequired() + "bar" : "@red@" + s.getBarsRequired() + "bars", s.getSendNameTextLine());
            } else {
                player.getPacketBuilder().sendString(s.getBarsRequired() == 1 ? "@gre@" + s.getBarsRequired() + "bar" : "@gre@" + s.getBarsRequired() + "bars", s.getSendNameTextLine());
            }

            if (!player.getSkills()[Misc.SMITHING].reqLevel(s.getLevelRequired())) {
                player.getPacketBuilder().sendString("@bla@" + s.getIndex().getName(), s.getSendAmountOfBarsTextLine());
            } else {
                player.getPacketBuilder().sendString("@whi@" + s.getIndex().getName(), s.getSendAmountOfBarsTextLine());
            }

            player.getPacketBuilder().sendItemOnInterfaceSlot(s.getIndex().getFrame(), s.getItemCreated(), s.getIndex().getSlot());
        }
    }

    /**
     * Smith armor for the specified player.
     * 
     * @param player
     *        the player to smith armor for.
     * @param item
     *        the item on the smithing table we are making.
     * @param table
     *        the smithing table being used.
     * @param amount
     *        the amount of sets of this armor to make.
     */
    public void smith(final Player player, final SmithingTableItem item, final Smith table, final int amount) {

        /** Block if we're already smithing. */
        if (player.getSkillEvent()[eventFireIndex()]) {
            return;
        }

        /** Block if we don't have a hammer. */
        if (!player.getInventory().getContainer().contains(2347)) {
            player.getPacketBuilder().sendMessage("You'll need a hammer if you want to make armor and weapons!");
            player.getPacketBuilder().closeWindows();
            return;
        }

        /** Block if we don't have the correct bars needed to make this item. */
        if (player.getInventory().getContainer().getCount(table.getBarId()) < item.getBarsRequired()) {
            player.getPacketBuilder().sendMessage("You need " + item.getBarsRequired() + " " + table.name().toLowerCase().replaceAll("_", " ") + " bars in order to be able to make the " + item.getItemCreated().getDefinition().getItemName() + ".");
            return;
        }

        /** Block if we do not have the required smithing level. */
        if (!player.getSkills()[Misc.SMITHING].reqLevel(item.getLevelRequired())) {
            player.getPacketBuilder().sendMessage("You need a Smithing level of " + item.getLevelRequired() + " to make the " + item.getItemCreated().getDefinition().getItemName() + ".");
            return;
        }

        /** We are now skilling! */
        player.getSkillEvent()[eventFireIndex()] = true;

        /** Close the smithing interface! */
        player.getPacketBuilder().closeWindows();

        /** Stop movement. */
        player.getMovementQueue().reset();

        /** Reset the smithing amount. */
        player.setSmithAmount(0);

        Rs2Engine.getWorld().submit(new Worker(5, true) {
            @Override
            public void fire() {

                /**
                 * If we have reached the amount we are smithing, stop the task
                 * and reset smithing.
                 */
                if (player.getSmithAmount() == amount) {
                    fireResetEvent(player);
                    this.cancel();
                    return;
                }

                /**
                 * If we have ran out of bars to smith, stop the task and reset
                 * smithing.
                 */
                if (player.getInventory().getContainer().getCount(table.getBarId()) < item.getBarsRequired()) {
                    player.getPacketBuilder().sendMessage("You need " + item.getBarsRequired() + " " + table.name().toLowerCase().replaceAll("_", " ") + " bars in order to be able to make the " + item.getItemCreated().getDefinition().getItemName() + ".");
                    fireResetEvent(player);
                    this.cancel();
                    return;
                }

                /**
                 * If for some reason we are no longer smithing (walked, dropped
                 * something, equipped, etc.), stop the task and reset smithing.
                 */
                if (!player.getSkillEvent()[eventFireIndex()]) {
                    fireResetEvent(player);
                    this.cancel();
                    return;
                }

                /** Close the windows. */
                player.getPacketBuilder().closeWindows();

                /** Perform animation. */
                player.animation(SMITH_ANIMATION);

                /** Make the armor. */
                player.getInventory().deleteItem(new Item(table.getBarId(), item.getBarsRequired()));
                player.getInventory().addItem(item.getItemCreated());
                player.getPacketBuilder().sendMessage("You bang the hammer on the anvil and make the " + item.getItemCreated().getDefinition().getItemName() + ".");
                exp(player, item.getExperience());
                player.addSmithAmount();
            }
        }.attach(player));
    }

    /**
     * Smelt the designated bar for the specified player.
     * 
     * @param player
     *        the player to open the smelting interface for.
     * @param smelt
     *        the bar to smelt.
     * @param amount
     *        the amount of bars to smelt.
     */
    public void smelt(final Player player, final Smelt smelt, final int amount) {

        /** If we are already smelting, stop. */
        if (player.getSkillEvent()[eventFireIndex()]) {
            fireResetEvent(player);
            return;
        }

        /** If we don't have the items in our inventory, stop. */
        for (Item item : smelt.getItemsNeeded()) {
            if (item == null) {
                continue;
            }

            if (!player.getInventory().getContainer().contains(item)) {
                player.getPacketBuilder().sendMessage("You do not have the required items to smelt this bar.");
                player.getPacketBuilder().closeWindows();
                return;
            }
        }

        /** If we do not have the required smithing level, stop. */
        if (!player.getSkills()[Misc.SMITHING].reqLevel(smelt.getLevel())) {
            player.getPacketBuilder().sendMessage("You need a Smithing level of " + smelt.getLevel() + " to smelt this bar.");
            player.getPacketBuilder().closeWindows();
            return;
        }

        /** We are now skilling! */
        player.getSkillEvent()[eventFireIndex()] = true;

        /** Close the smelting interface! */
        player.getPacketBuilder().closeWindows();

        /** Stop movement. */
        player.getMovementQueue().reset();

        /** Reset the smelting amount. */
        player.setSmeltAmount(0);

        Rs2Engine.getWorld().submit(new Worker(4, true) {
            @Override
            public void fire() {

                /**
                 * If we have reached the amount we are smelting, stop the task
                 * and reset smithing.
                 */
                if (player.getSmeltAmount() == amount) {
                    fireResetEvent(player);
                    this.cancel();
                    return;
                }

                /**
                 * If we have ran out of ore to smelt, stop the task and reset
                 * smithing.
                 */
                for (Item item : smelt.getItemsNeeded()) {
                    if (item == null) {
                        continue;
                    }

                    if (!player.getInventory().getContainer().contains(item)) {
                        player.getPacketBuilder().sendMessage("You do not have the required items to smelt this bar.");
                        fireResetEvent(player);
                        this.cancel();
                        return;
                    }
                }

                /**
                 * If for some reason we are no longer smelting (walked, dropped
                 * something, equipped, etc.), stop the task and reset smithing.
                 */
                if (!player.getSkillEvent()[eventFireIndex()]) {
                    fireResetEvent(player);
                    this.cancel();
                    return;
                }

                /** Close the windows. */
                player.getPacketBuilder().closeWindows();

                /** Perform animation. */
                player.animation(SMELT_ANIMATION);

                /** Smelt ore and make bar. */
                if (Misc.getRandom().nextInt(100) < smelt.getSuccessChance().getPercentage()) {
                    for (Item item : smelt.getItemsNeeded()) {
                        if (item == null) {
                            continue;
                        }

                        player.getInventory().deleteItem(item);
                    }

                    exp(player, smelt.getExperience());
                    player.getInventory().addItem(new Item(smelt.getBar()));
                    player.getPacketBuilder().sendMessage("You smelt the ores and make a " + smelt.name().toLowerCase().replaceAll("_", " ") + " bar.");
                } else {
                    for (Item item : smelt.getItemsNeeded()) {
                        if (item == null) {
                            continue;
                        }

                        player.getInventory().deleteItem(item);
                    }

                    player.getPacketBuilder().sendMessage("Oh no! You fail to smelt the ores properly!");
                }

                player.addSmeltAmount();
            }
        }.attach(player));
    }

    /**
     * @return the singleton.
     */
    public static Smithing getSingleton() {
        if (singleton == null) {
            singleton = new Smithing();
        }

        return singleton;
    }

    @Override
    public void fireResetEvent(Player player) {
        player.getPacketBuilder().resetAnimation();
        player.setSmelt(null);
        player.setSmeltAmount(0);
        player.setSmithAmount(0);
    }

    @Override
    public int eventFireIndex() {
        return SkillEvent.SMITHING;
    }

    @Override
    public SkillConstant skillConstant() {
        return SkillConstant.SMITHING;
    }

    /**
     * A table of smithing items that will be used to display which items can
     * and cannot be made.
     * 
     * @author lare96
     */
    public static class SmithingTable {

        /**
         * The table of smithing items.
         */
        private SmithingTableItem[] table;

        /**
         * Create a new smithing table.
         */
        public SmithingTable() {
            this.setTable(new SmithingTableItem[25]);
        }

        /**
         * Constants that represent each index on the table.
         * 
         * @author lare96
         */
        public enum TableIndex {
            DAGGER(1119, 0, "Dagger"),
            AXE(1120, 0, "Axe"),
            CHAIN_BODY(1121, 0, "Chain body"),
            MED_HELM(1122, 0, "Medium helm"),
            DART_TIPS(1123, 0, "Dart tips"),
            SWORD(1119, 1, "Sword"),
            MACE(1120, 1, "Mace"),
            PLATELEGS(1121, 1, "Plate legs"),
            FULL_HELM(1122, 1, "Full helm"),
            ARROW_TIPS(1123, 1, "Arrowtips"),
            SCIMITAR(1119, 2, "Scimitar"),
            WARHAMMER(1120, 2, "Warhammer"),
            PLATESKIRT(1121, 2, "Plate skirt"),
            SQ_SHIELD(1122, 2, "Square shield"),
            KNIVES(1123, 2, "Throwing knives"),
            LONGSWORD(1119, 3, "Long sword"),
            BATTLE_AXE(1120, 3, "Battle axe"),
            PLATEBODY(1121, 3, "Plate body"),
            KITESHIELD(1122, 3, "Kite shield"),
            WIRE(1123, 3, "Wire"),
            TWO_HANDED_SWORD(1119, 4, "2 hand sword"),
            CLAWS(1120, 4, "Claws"),
            OIL_FRAME_LANTERN(1121, 4, "Oil lantern frame"),
            NAILS(1122, 4, "Nails"),
            STUDS(1123, 4, "Studs");

            /**
             * The frame for the index.
             */
            private int frame;

            /**
             * The slot for this index.
             */
            private int slot;

            /**
             * The name of this index.
             */
            private String name;

            /**
             * Creates a new TableIndex constant.
             * 
             * @param frame
             *        the frame for the index.
             * @param slot
             *        the slot for this index.
             * @param name
             *        the name of this index.
             */
            TableIndex(int frame, int slot, String name) {
                this.setFrame(frame);
                this.setSlot(slot);
                this.setName(name);
            }

            /**
             * @return the frame.
             */
            public int getFrame() {
                return frame;
            }

            /**
             * @param frame
             *        the frame to set.
             */
            public void setFrame(int frame) {
                this.frame = frame;
            }

            /**
             * @return the slot.
             */
            public int getSlot() {
                return slot;
            }

            /**
             * @param slot
             *        the slot to set.
             */
            public void setSlot(int slot) {
                this.slot = slot;
            }

            /**
             * @return the name.
             */
            public String getName() {
                return name;
            }

            /**
             * @param name
             *        the name to set.
             */
            public void setName(String name) {
                this.name = name;
            }
        }

        /**
         * @return the table.
         */
        public SmithingTableItem[] getTable() {
            return table;
        }

        /**
         * @param table
         *        the table to set.
         */
        private void setTable(SmithingTableItem[] table) {
            this.table = table;
        }

        /**
         * Sets an item on the smithing table.
         * 
         * @param index
         *        the index on the table to set.
         * @param item
         *        the item to set on the index.
         */
        public SmithingTable setTableItem(TableIndex index, SmithingTableItem item) {
            item.setIndex(index);
            this.getTable()[index.ordinal()] = item;
            return this;
        }
    }

    /**
     * An item on the smithing table.
     * 
     * @author lare97
     */
    public static class SmithingTableItem {

        /**
         * The item created when smithing.
         */
        private Item itemCreated;

        /**
         * The level required to make this item.
         */
        private int levelRequired;

        /**
         * The amount of bars required to make this item.
         */
        private int barsRequired;

        /**
         * The line for the smithing table interface.
         */
        private int sendTextLine;

        /**
         * The next line for the smithing table interface.
         */
        private int sendNextTextLine;

        /**
         * The index of this item.
         */
        private TableIndex index;

        /**
         * The experience granted when this item is made.
         */
        private int experience;

        /**
         * Create an new item on the smithing table.
         * 
         * @param itemCreated
         *        the item created when smithing.
         * @param levelRequired
         *        the level required to make this item.
         * @param barsRequired
         *        the amount of bars required to make this item.
         * @param sendTextLine
         *        the line for the smithing table interface.
         * @param sendNextTextLine
         *        the next line for the smithing table interface.
         * @param experience
         *        the experience granted when this item is made.
         */
        public SmithingTableItem(Item itemCreated, int levelRequired, int barsRequired, int sendTextLine, int sendNextTextLine, int experience) {
            this.setItemCreated(itemCreated);
            this.setLevelRequired(levelRequired);
            this.setBarsRequired(barsRequired);
            this.setSendTextLine(sendTextLine);
            this.setSendNextTextLine(sendNextTextLine);
            this.setExperience(experience);
        }

        /**
         * @return the itemCreated.
         */
        public Item getItemCreated() {
            return itemCreated;
        }

        /**
         * @param itemCreated
         *        the itemCreated to set.
         */
        private void setItemCreated(Item itemCreated) {
            this.itemCreated = itemCreated;
        }

        /**
         * @return the levelRequired.
         */
        public int getLevelRequired() {
            return levelRequired;
        }

        /**
         * @param levelRequired
         *        the levelRequired to set.
         */
        private void setLevelRequired(int levelRequired) {
            this.levelRequired = levelRequired;
        }

        /**
         * @return the barsRequired.
         */
        public int getBarsRequired() {
            return barsRequired;
        }

        /**
         * @param barsRequired
         *        the barsRequired to set.
         */
        private void setBarsRequired(int barsRequired) {
            this.barsRequired = barsRequired;
        }

        /**
         * @return the sendTextLine.
         */
        public int getSendNameTextLine() {
            return sendTextLine;
        }

        /**
         * @param sendTextLine
         *        the sendTextLine to set.
         */
        private void setSendTextLine(int sendTextLine) {
            this.sendTextLine = sendTextLine;
        }

        /**
         * @return the sendNextTextLine.
         */
        public int getSendAmountOfBarsTextLine() {
            return sendNextTextLine;
        }

        /**
         * @param sendNextTextLine
         *        the sendNextTextLine to set.
         */
        private void setSendNextTextLine(int sendNextTextLine) {
            this.sendNextTextLine = sendNextTextLine;
        }

        /**
         * @return the index.
         */
        public TableIndex getIndex() {
            return index;
        }

        /**
         * @param index
         *        the index to set.
         */
        public void setIndex(TableIndex index) {
            this.index = index;
        }

        /**
         * @return the experience.
         */
        public int getExperience() {
            return experience;
        }

        /**
         * @param experience
         *        the experience to set.
         */
        public void setExperience(int experience) {
            this.experience = experience;
        }
    }
}