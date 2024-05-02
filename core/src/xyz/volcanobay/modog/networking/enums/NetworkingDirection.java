package xyz.volcanobay.modog.networking.enums;

public enum NetworkingDirection {
    C2S(NetworkingSide.CLIENT, NetworkingSide.SERVER),
    S2C(NetworkingSide.CLIENT, NetworkingSide.SERVER),
    A2A(null, null);
    final NetworkingSide from;
    final NetworkingSide to;
    
    NetworkingDirection(NetworkingSide from, NetworkingSide to) {
        this.from = from;
        this.to = to;
    }
    
    public NetworkingSide getFrom() {
        return from;
    }
    
    public NetworkingSide getTo() {
        return to;
    }
    
}
