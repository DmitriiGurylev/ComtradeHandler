package ru.gur;

public class Comtrade {

    private StringBuilder dat = new StringBuilder();

    private String cfg;

    public Comtrade() {
    }

    public Comtrade(String cfg, StringBuilder dat) {
        this.cfg = cfg;
        this.dat = dat;
    }

    public StringBuilder getDat() {
        return dat;
    }

    public String getCfg() {
        return cfg;
    }

    public void setCfg(String cfg) {
        this.cfg = cfg;
    }

    public void setDat(StringBuilder dat) {
        this.dat = dat;
    }
}
