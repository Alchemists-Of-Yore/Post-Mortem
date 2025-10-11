package dev.tazer.post_mortem.client;

import java.util.ArrayList;
import java.util.List;

public class SpectreMenu {
    public final List<SpectreMenuItem> items;
    public int selectedSlot = 0;
    public float currentIndex = selectedSlot;

    public SpectreMenu() {
        items = new ArrayList<>(2);

        items.add(new DeathPointMenuItem());
        items.add(new SpawnPointMenuItem());
        items.add(new GravestoneMenuItem());
    }

    public SpectreMenuItem getSelectedItem() {
        return items.get(selectedSlot);
    }

    public void selectSlot(int slot) {
        if (slot >= 0 && slot < items.size()) {
            selectedSlot = slot;
        }
    }
}

