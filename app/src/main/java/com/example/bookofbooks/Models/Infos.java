package com.example.bookofbooks.Models;

public class Infos {
    private Integer unseenCount;
    public Infos(){

    }

    public Infos(Integer unseenCount) {
        this.unseenCount = unseenCount;
    }

    public Integer getUnseenCount() {
        return unseenCount;
    }

    public void setUnseenCount(Integer unseenCount) {
        this.unseenCount = unseenCount;
    }

}
