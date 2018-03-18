package ninja.amp.tradeup.collection;

import java.util.Set;

import ninja.amp.tradeup.Item;

public interface Collection {

    String getName();

    Set<Skin> getSkins();

    Set<Item> getItems();

    void addItem(Item item);

}
