package com.vettiankal;

import java.util.List;

public class TransformerConfiguration {

    private List<String> classes;
    private String hook;
    private boolean default_enabled;

    public List<String> getClasses() {
        return classes;
    }

    public void setClasses(List<String> classes) {
        this.classes = classes;
    }

    public String getHook() {
        return hook;
    }

    public void setHook(String hook) {
        this.hook = hook;
    }
}
