package com.hadi.incredibleinfo.ProfDetailsClasses;

public class ProfessionalDetailsData {

    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ProfessionalDetailsData{" +
                "data=" + data +
                '}';
    }
}
