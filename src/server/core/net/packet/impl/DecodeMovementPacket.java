package server.core.net.packet.impl;

import server.core.net.buffer.PacketBuffer;
import server.core.net.packet.PacketDecoder;
import server.world.entity.npc.NpcDialogue;
import server.world.entity.player.Player;
import server.world.entity.player.skill.SkillEvent;
import server.world.map.Position;

/**
 * Sent whenever the makes a yellow-x click, red-x click, or clicks the minimap.
 * 
 * @author lare96
 */
public class DecodeMovementPacket extends PacketDecoder {

    @Override
    public void decode(Player player, PacketBuffer.ReadBuffer in) {
        int length = player.getSession().getPacketLength();

        // minimap click
        if (player.getSession().getPacketOpcode() == 248) {
            SkillEvent.fireSkillEvents(player);
            length -= 14;
        }

        // yellow x click
        if (player.getSession().getPacketOpcode() == 164) {
            SkillEvent.fireSkillEvents(player);
            // player.getRS2Packet().sendMessage("164");

            // red x click
        } else if (player.getSession().getPacketOpcode() == 98) {
            // player.getRS2Packet().sendMessage("98");
        }

        if (player.getMovementQueue().isLockMovement()) {
            return;
        }

        if (player.getNpcDialogue() != 0) {
            NpcDialogue.getDialogueMap().get(player.getNpcDialogue()).stop(player);
        }

        if (player.getTradeSession().inTrade()) {
            player.getTradeSession().resetTrade(false);
        }

        player.getPacketBuilder().closeWindows();
        player.setOpenShopId(-1);

        int steps = (length - 5) / 2;
        int[][] path = new int[steps][2];
        int firstStepX = in.readShort(PacketBuffer.ValueType.A, PacketBuffer.ByteOrder.LITTLE);
        for (int i = 0; i < steps; i++) {
            path[i][0] = in.readByte();
            path[i][1] = in.readByte();
        }
        int firstStepY = in.readShort(PacketBuffer.ByteOrder.LITTLE);

        player.getMovementQueue().reset();
        player.getMovementQueue().setRunPath(in.readByte(PacketBuffer.ValueType.C) == 1);
        player.getMovementQueue().addToPath(new Position(firstStepX, firstStepY));

        for (int i = 0; i < steps; i++) {
            path[i][0] += firstStepX;
            path[i][1] += firstStepY;
            player.getMovementQueue().addToPath(new Position(path[i][0], path[i][1]));
        }
        player.getMovementQueue().finish();

    }

    @Override
    public int[] opcode() {
        return new int[] { 248, 164, 98 };
    }
}
