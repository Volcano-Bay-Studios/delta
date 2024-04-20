package xyz.volcanobay.modog.networking.enums;

public enum HostRoutingHeader {
    TO_ALL_CLIENTS(0),
    TO_CLIENT(1),
    TO_SERVER(2),
    TO_ALL_OTHERS(3);
    final int id;
    
    HostRoutingHeader(int id) {
        this.id = id;
    }
    
    public int getId() {
        return id;
    }
    
}
