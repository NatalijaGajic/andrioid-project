package com.example.bookofbooks.Models;

import java.util.ArrayList;
import java.util.Iterator;

public class ChatCollection {
    ArrayList<Chat> chats = new ArrayList<>();

    public ChatCollection(){

    }

    public ChatCollection(ArrayList<Chat> chats){
        this.chats = chats;
    }

    public void add(Chat chat){
        Iterator<Chat> it = chats.iterator();
        int i = 0;
        while(it.hasNext()){
            if(it.next().getPostID().equals(chat.getPostID())){
                chats.remove(i);
                break;
            }
            i++;
        }
        chats.add(0,chat);
    }

    public ArrayList<Chat> getChats() {
        return chats;
    }

    public void setChats(ArrayList<Chat> chats) {
        this.chats = chats;
    }
}
