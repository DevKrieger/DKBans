package de.fridious.bansystem.extension.gui.api.inventory.gui;

import ch.dkrieger.bansystem.lib.BanSystem;
import ch.dkrieger.bansystem.lib.player.NetworkPlayer;
import de.fridious.bansystem.extension.gui.api.inventory.item.ItemBuilder;
import de.fridious.bansystem.extension.gui.api.inventory.item.ItemStorage;
import de.fridious.bansystem.extension.gui.utils.GuiExtensionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.*;

/*
 * (C) Copyright 2018 The DKBans Project (Davide Wietlisbach)
 *
 * @author Philipp Elvin Friedhoff
 * @since 30.12.18 20:25
 * @Website https://github.com/DevKrieger/DKBans
 *
 * The DKBans Project is under the Apache License, version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

public abstract class GUI<T> implements InventoryHolder {

    protected Inventory inventory;
    private int currentPage, pageSize;
    private List<? extends T> pageEntries;
    private Map<Integer, T> entryBySlot;
    private List<Class<? extends Event>> updateEvents;


    protected GUI() {
        this.currentPage = 1;
        this.pageSize = 36;
        this.updateEvents = new LinkedList<>();
    }

    public GUI(Inventory inventory) {
        this();
        this.inventory = inventory;
    }

    public GUI(String name, int size){
        this();
        this.inventory = Bukkit.createInventory(this,size,name);
    }

    public GUI(InventoryType inventoryType) {
        this();
        this.inventory = Bukkit.createInventory(this, inventoryType);
    }

    public ItemStack getItem(int place){
        return this.inventory.getItem(place);
    }

    public Inventory getInventory() {
        return inventory;
    }

    /**
     * This method is for anvil guis and retun the input string of the anvil
     * It might be null, if the player doesn't edit the title in the anvil
     * @return String of input in anvil
     */
    public String getInput() {
        ItemStack itemStack = getItem(AnvilSlot.OUTPUT);
        if(itemStack == null) return null;
        return itemStack.getItemMeta().hasDisplayName() ? itemStack.getItemMeta().getDisplayName() : itemStack.getType().toString();
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public boolean hasNextPage() {
        return (this.currentPage)*this.pageSize < this.pageEntries.size();
    }

    public boolean hasPreviousPage() {
        return this.currentPage > 1;
    }

    @Nullable
    public List<? extends T> getPageEntries() {
        return pageEntries;
    }

    public int getMaxPages() {
        int maxPages = (int)Math.ceil((double)this.pageEntries.size()/this.pageSize);
        return maxPages < 1 ? 1 : maxPages;
    }

    public List<Class<? extends Event>> getUpdateEvents() {
        return updateEvents;
    }

    public void setPageEntries(List<? extends T> pageEntries) {
        this.pageEntries = pageEntries;
        this.entryBySlot = new LinkedHashMap<>();
        setPage(this.currentPage);
        setArea(ItemStorage.get("placeholder"), this.pageSize, this.pageSize+8);
    }

    public Map<Integer, T> getEntryBySlot() {
        return entryBySlot;
    }

    public void setPage(int page) {
        if(getPageEntries() != null) {
            getEntryBySlot().clear();
            clear(0, (this.pageSize-1));
            clear(this.pageSize+9, this.pageSize+17);
            List<T> rangeList = GuiExtensionUtils.getListRange(getPageEntries(),
                    (page == 1 ? 0  : (page-1)*(this.pageSize-1)) + (page>1 ? (page-1) : 0),
                    (page == 1 ? (this.pageSize-1) : page*(this.pageSize-1)) + (page>1 ? (page-1) : 0));
            int slot = 0;
            for (T t : rangeList) {
                getEntryBySlot().put(slot, t);
                setPageItem(slot, t);
                slot++;
            }
            setPageSwitchItems();
        }
    }

    public void setPageItem(int slot, T t) {

    }

    public void beforeUpdatePage() {

    }

    public void updatePage(Event event) {
        System.out.println("PRE UPDATE PAGE " + this.getClass() + ":" + updateEvents);
        if(event == null || this.updateEvents.contains(event.getClass())) {
            System.out.println("UPDATE PAGE " + this.getClass());
            beforeUpdatePage();
            setPage(this.currentPage);
        }
    }

    public void setPageSwitchItems() {
        setItem(this.pageSize+13, ItemStorage.get("currentpage", this.currentPage, getMaxPages()));
        if(hasNextPage()) {
            setItem(this.pageSize+17, ItemStorage.get("nextpage", this.currentPage, getMaxPages()));
        } else if(hasPreviousPage()) {
            setItem(this.pageSize+17, ItemStorage.get("previouspage", this.currentPage, getMaxPages()));
            return;
        }
        if(hasPreviousPage()) {
            setItem(this.pageSize+16, ItemStorage.get("previouspage", this.currentPage, getMaxPages()));
        }
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setItem(int place, ItemStack item){
        this.inventory.setItem(place,item);
    }

    public void setItem(int place, ItemBuilder itembuilder){
        this.inventory.setItem(place,itembuilder.build());
    }

    public void removeItem(int place){
        this.inventory.setItem(place,null);
    }

    public void addItem(ItemStack item){
        this.inventory.addItem(item);
    }

    public void addItem(ItemBuilder itembuilder){
        addItem(itembuilder.build());
    }

    public void fill(ItemBuilder builder){
        fill(builder.build());
    }

    public void fill(ItemStack item){
        for(int i = 0; i < inventory.getContents().length; i++){
            if(this.inventory.getItem(i) == null || this.inventory.getItem(i).getType() == Material.AIR)
                this.inventory.setItem(i,item);
        }
    }

    public void setArea(ItemBuilder itemBuilder, int startPlace, int lastPlace) {
        setArea(itemBuilder.build(), startPlace, lastPlace);
    }

    public void setArea(ItemStack itemStack, int startPlace, int lastPlace) {
        for(int i = startPlace; i <= lastPlace; i++) setItem(i, itemStack);
    }

    public void clear(){
        this.inventory.clear();
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public void createInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public void createInventory(String name, int size) {
        this.inventory = Bukkit.createInventory(this,size,name);
    }

    public void createInventory(InventoryType inventoryType) {
        this.inventory = Bukkit.createInventory(this, inventoryType);
    }

    public void clear(int startPlace, int lastPlace){
        for(int i = startPlace; i <= lastPlace;i++) removeItem(i);
    }

    public void clear(int slot) {
        removeItem(slot);
    }

    public void open(Player player){
        if(player.getOpenInventory().getTopInventory().equals(inventory))return;
        player.openInventory(this.inventory);
    }

    public void handleClick(InventoryClickEvent event) {
        if(this.pageEntries != null) {
            if(event.getSlot() == this.inventory.getSize()-1) {
                if(hasNextPage()) {
                    this.currentPage++;
                    setPage(this.currentPage);
                } else if(hasPreviousPage()) {
                    this.currentPage--;
                    setPage(this.currentPage);
                }
            } else if(event.getSlot() == this.inventory.getSize()-2) {
                if(hasPreviousPage()) {
                    this.currentPage--;
                    setPage(this.currentPage);
                }
            }
        }
        if(this instanceof PrivateGUI){
            Player clicker = (Player) event.getWhoClicked();
            if(((PrivateGUI)this).getOwner() == clicker) onClick(event);
        }else onClick(event);
    }

    public void handleClose(InventoryCloseEvent event){
        if(this instanceof PrivateGUI){
            Player clicker = (Player) event.getPlayer();
            if(((PrivateGUI)this).getOwner() == clicker) onClose(event);
        }else onClose(event);
    }

    public void handleOpen(InventoryOpenEvent event) {
        if(this instanceof PrivateGUI){
            Player clicker = (Player) event.getPlayer();
            if(((PrivateGUI)this).getOwner() == clicker) onOpen(event);
        }else onOpen(event);
    }

    protected abstract void onOpen(InventoryOpenEvent event);
    protected abstract void onClick(InventoryClickEvent event);
    protected abstract void onClose(InventoryCloseEvent event);
}