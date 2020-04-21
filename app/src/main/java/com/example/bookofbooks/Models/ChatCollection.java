package com.example.bookofbooks.Models;

import java.util.ArrayList;
import java.util.Iterator;

public class ChatCollection {
    ArrayList<Chat> chats = new ArrayList<>();
    private Integer newMessages = 0;

    public ChatCollection(){

    }

    public ChatCollection(ArrayList<Chat> chats){

        this.chats = chats;
    }

    public int add(Chat chat, boolean addNewMessages){
        Iterator<Chat> it = chats.iterator();
        int i = 0;
        int newMessages = -1;
        while(it.hasNext()){
            if(it.next().getPostID().equals(chat.getPostID())){
                newMessages =  chats.get(i).getNewMessages();
                chats.remove(i);
                break;
            }
            i++;
        }
        if(addNewMessages){
            if(newMessages != -1){
                chat.setNewMessages(newMessages+1);
            }else {
                chat.setNewMessages(1);
            }
        }
        chats.add(0,chat);
        if(newMessages!=-1){
            return newMessages;
        }else{
            return 0;
        }

    }

    public int getChat(String postID){
        Iterator<Chat> it = chats.iterator();
        int i = 0;
        while(it.hasNext()){
            if(it.next().getPostID().equals(postID)){
                return i;
            }
            i++;
        }
        return -1;
    }



    public ArrayList<Chat> getChats() {
        return chats;
    }

    public void setChats(ArrayList<Chat> chats) {
        this.chats = chats;
    }

    public Integer getNewMessages() {
        return newMessages;
    }

    public void setNewMessages(Integer newMessages) {
        this.newMessages = newMessages;
    }
}
