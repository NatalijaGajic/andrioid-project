package com.example.bookofbooks.Models;

import java.util.ArrayList;

public class MessagesSplitCollection {
    private int messagesCount;
    private int collectionCount = 0;
    private ArrayList<String> users;

    private ArrayList<Message> messages;

    public MessagesSplitCollection(){

    }

    public MessagesSplitCollection(ArrayList<Message> messages, String firstUser, String secondUser) {
        this.messages = messages;
        this.messagesCount = messages.size();
        users = new ArrayList<String>();
        users.add(firstUser);
        users.add(secondUser);
    }

    public void add(Message message) {
        messages.add(message);
        this.messagesCount = messages.size();
    }

    public void remove(Message message){
        messages.remove(message);
        this.messagesCount = messages.size();
    }

    public int getMessagesCount() {
        return messagesCount;
    }

    public void setMessagesCount(int messagesCount) {
        this.messagesCount = messagesCount;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }

    public int getCollectionCount() {
        return collectionCount;
    }

    public void setCollectionCount(int collectionCount) {
        this.collectionCount = collectionCount;
    }


}
