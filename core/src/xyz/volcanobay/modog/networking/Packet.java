package xyz.volcanobay.modog.networking;

public class Packet {
    public String type;
    public String value;
    public Packet(String type, String value) {
        this.type = type;
        this.value = value;
    }
}
