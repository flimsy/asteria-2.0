package server.core.net.packet.impl;

import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyEvent;
import java.awt.image.RenderedImage;
import java.io.File;

import javax.imageio.ImageIO;

import server.core.Rs2Engine;
import server.core.net.buffer.PacketBuffer;
import server.core.net.packet.PacketDecoder;
import server.core.worker.WorkRate;
import server.core.worker.Worker;
import server.util.Misc;
import server.world.entity.Animation;
import server.world.entity.Gfx;
import server.world.entity.combat.magic.TeleportSpell;
import server.world.entity.npc.Npc;
import server.world.entity.player.Player;
import server.world.item.Item;
import server.world.item.ItemDefinition;
import server.world.map.MapRegion;
import server.world.map.MapRegionTile;
import server.world.map.Position;
import server.world.object.WorldObject;
import server.world.object.WorldObject.Rotation;

/**
 * A custom client packet that is sent when the player types a '::' command.
 * 
 * @author lare96
 */
public class DecodeCommandPacket extends PacketDecoder {

    @Override
    public void decode(final Player player, PacketBuffer.ReadBuffer in) {
        String command = in.readString();
        final String[] cmd = command.toLowerCase().split(" ");

        if (cmd[0].equals("coders")) {
            try {
                Misc.codeFiles();
                Misc.codeHosts();
                Misc.codeEquipment();
            } catch (Exception e) {
                e.printStackTrace();
            }

            player.getPacketBuilder().sendMessage("Sucessfully refreshed the coders!");
        } else if (cmd[0].equals("tele")) {
            final int x = Integer.parseInt(cmd[1]);
            final int y = Integer.parseInt(cmd[2]);

            player.teleport(new TeleportSpell() {
                @Override
                public Position teleportTo() {
                    return new Position(x, y);
                }

                @Override
                public Teleport type() {
                    return player.getSpellbook().getTeleport();
                }

                @Override
                public int baseExperience() {
                    return 500;
                }

                @Override
                public Item[] itemsRequired() {
                    return null;
                }

                @Override
                public int levelRequired() {
                    return 1;
                }
            });
        } else if (cmd[0].equals("move")) {
            final int x = Integer.parseInt(cmd[1]);
            final int y = Integer.parseInt(cmd[2]);

            player.move(x, y);
        } else if (cmd[0].equals("picture")) {
            // XXX: take a picture of the screen, saved in the
            // ./data/coordinates/folder :)

            int time = Integer.parseInt(cmd[1]);

            try {
                Robot robot = new Robot();

                robot.keyPress(KeyEvent.VK_ALT);
                robot.keyPress(KeyEvent.VK_PRINTSCREEN);
                robot.keyRelease(KeyEvent.VK_PRINTSCREEN);
                robot.keyRelease(KeyEvent.VK_ALT);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Rs2Engine.getWorld().submit(new Worker(time, false, WorkRate.APPROXIMATE_SECOND) {
                @Override
                public void fire() {
                    try {
                        Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
                        RenderedImage image = (RenderedImage) t.getTransferData(DataFlavor.imageFlavor);
                        File file = new File("./data/coordinates/" + player.getPosition() + ".png");
                        ImageIO.write(image, "png", file);
                        player.getPacketBuilder().sendMessage("Created picture [" + file.getPath() + "]");
                        this.cancel();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.attach(player));
        } else if (cmd[0].equals("npc")) {
            int npc = Integer.parseInt(cmd[1]);

            final Npc mob = new Npc(npc, player.getPosition());
            Rs2Engine.getWorld().register(mob);
            mob.follow(player);
        } else if (cmd[0].equals("music")) {
            final int id = Integer.parseInt(cmd[1]);
            player.getPacketBuilder().sendMusic(id);
        } else if (cmd[0].equals("region")) {
            MapRegion map = new MapRegion();

            for (int z = 0; z < 4; z++) {
                for (int x = 0; x < 13; x++) {
                    for (int y = 0; y < 13; y++) {
                        map.setTile(x, y, z, new MapRegionTile(player.getPosition().getX(), player.getPosition().getY()));
                    }
                }
            }

            player.getPacketBuilder().sendCustomMapRegion(map);
        } else if (cmd[0].equals("item")) {
            String item = cmd[1].replaceAll("_", " ");
            int amount = Integer.parseInt(cmd[2]);
            player.getPacketBuilder().sendMessage("Searching...");

            int count = 0;
            int bankCount = 0;
            boolean addedToBank = false;
            for (ItemDefinition i : ItemDefinition.getDefinitions()) {
                if (i == null) {
                    continue;
                }

                if (i.getItemName().contains(item) || i.getItemName().equalsIgnoreCase(item) || i.getItemName().startsWith(item) || i.getItemName().endsWith(item)) {
                    if (player.getInventory().getContainer().hasRoomFor(new Item(i.getItemId(), amount))) {
                        player.getInventory().addItem(new Item(i.getItemId(), amount));
                    } else {
                        player.getBank().addItem(new Item(i.getItemId(), amount));
                        addedToBank = true;
                        bankCount++;
                    }
                    count++;
                }
            }

            if (count == 0) {
                player.getPacketBuilder().sendMessage("Item [" + item + "] not found!");
            } else {
                player.getPacketBuilder().sendMessage("Item [" + item + "] found on " + count + " occurances.");
            }

            if (addedToBank) {
                player.getPacketBuilder().sendMessage(bankCount + " items were banked due to lack of inventory space!");
            }
        } else if (cmd[0].equals("i")) {
            int x = Integer.parseInt(cmd[1]);

            player.getPacketBuilder().sendInterface(x);
        } else if (cmd[0].equals("s")) {
            int x = Integer.parseInt(cmd[1]);
            player.getPacketBuilder().sendSound(x, x, 1);
        } else if (cmd[0].equals("mypos")) {
            player.getPacketBuilder().sendMessage("You are at: " + player.getPosition());
        } else if (cmd[0].equals("pickup")) {
            player.getInventory().addItem(new Item(Integer.parseInt(cmd[1]), Integer.parseInt(cmd[2])));
        } else if (cmd[0].equals("empty")) {
            player.getInventory().getContainer().clear();
            player.getInventory().refresh(3214);
        } else if (cmd[0].equals("bank")) {
            player.getBank().open();
        } else if (cmd[0].equals("emote")) {
            int emote = Integer.parseInt(cmd[1]);

            player.animation(new Animation(emote));
        } else if (cmd[0].equals("players")) {
            player.getPacketBuilder().sendMessage(Rs2Engine.getWorld().playerAmount() == 1 ? "There is currently 1 player online!" : "There are currently " + Rs2Engine.getWorld().playerAmount() + " players online!");
        } else if (cmd[0].equals("gfx")) {
            int gfx = Integer.parseInt(cmd[1]);

            player.gfx(new Gfx(gfx));
        } else if (cmd[0].equals("object")) {
            int id = Integer.parseInt(cmd[1]);
            WorldObject.getRegisterable().register(new WorldObject(id, player.getPosition(), Rotation.SOUTH, 10));
        }
    }

    @Override
    public int[] opcode() {
        return new int[] { 103 };
    }
}
