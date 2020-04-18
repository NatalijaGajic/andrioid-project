package com.example.bookofbooks.Models;

import java.util.ArrayList;

public class MessagesCollection {
    private ArrayList<Message> messages;

    public MessagesCollection(){

    }

    public MessagesCollection(ArrayList<Message> messages) {
        this.messages = messages;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }
}
