package dev.tazer.post_mortem.client;

import java.util.ArrayList;
import java.util.List;

public class SpiritMenu {
    public final List<SpiritMenuItem> items;
    public int selectedSlot = 0;
    public float currentIndex = selectedSlot;

    public SpiritMenu() {
        items = new ArrayList<>(2);

        items.add(new DeathPointMenuItem());
        items.add(new SpawnPointMenuItem());
        items.add(new GravestoneMenuItem());
    }

    public SpiritMenuItem getSelectedItem() {
        return items.get(selectedSlot);
    }

    public void selectSlot(int slot) {
        if (slot >= 0 && slot < items.size()) {
            selectedSlot = slot;
        }
    }
}

