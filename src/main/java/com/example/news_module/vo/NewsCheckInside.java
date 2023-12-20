package com.example.news_module.vo;

public class NewsCheckInside {

    private int index;
    
    private boolean checked;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public NewsCheckInside() {
        super();
        // TODO Auto-generated constructor stub
    }

    public NewsCheckInside(int index, boolean checked) {
        super();
        this.index = index;
        this.checked = checked;
    } 
    
}
