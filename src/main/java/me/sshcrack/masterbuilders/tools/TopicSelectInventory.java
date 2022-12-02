package me.sshcrack.masterbuilders.tools;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import me.sshcrack.masterbuilders.Main;
import me.sshcrack.masterbuilders.message.MessageManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class TopicSelectInventory implements InventoryProvider {
    public static SmartInventory INVENTORY = SmartInventory.builder()
            .id("challenge-util-settings")
            .provider(new TopicSelectInventory())
            .manager(Main.plugin.invManager)
            .size(6, 9)
            .title(MessageManager.getMessage("start.topic.title"))
            .build();

    @Override
    public void init(Player player, InventoryContents contents) {
        Pagination pagination = contents.pagination();
        ClickableItem border = ClickableItem.empty(new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        List<String> topics = Main.plugin.getConfig().getStringList("topics");

        int topicSize = topics.size();
        int width = 7;

        ArrayList<ClickableItem> items = new ArrayList<>();
        for(int i = 0; i < topicSize; i++) {
            if(i % 7 == 0) {
                if(i != 0)
                    items.add(border);
                items.add(border);
            }
            List<Material> allWoolTypes = Arrays.stream(Material
                    .values())
                    .filter(e -> e.getKey().toString().toLowerCase().contains("wool"))
                        .collect(Collectors.toList());
            Random ran = new Random();

            int arrIndex = ran.nextInt(allWoolTypes.size());
            ItemStack item = new ItemStack(allWoolTypes.get(arrIndex));

            String topic = topics.get(i);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD + topic);

            item.setItemMeta(meta);
            items.add(
                    ClickableItem.of(item, (inv) -> {
                        inv.getInventory().close();
                        Player p = (Player) inv.getView().getPlayer();
                        Team team = Tools.getMainScoreboard().getEntityTeam(p);
                        if(team == null) {
                            player.sendMessage("You have to be in a team");
                            return;
                        }

                        Main.plugin.getConfig().set(String.format("selected.%s", team.getName()), topic);

                        MessageManager.sendMessageF(player, "start.topic.selected", topic);
                        player.performCommand("mb start");
                    })
            );
        }

        pagination.setItems(items.toArray(new ClickableItem[0]));
        pagination.setItemsPerPage(width * 3);

        pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 0));

        contents.fillBorders(border);
    }

    @Override
    public void update(Player player, InventoryContents inventoryContents) {

    }
}
