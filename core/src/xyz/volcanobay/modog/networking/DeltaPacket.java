package xyz.volcanobay.modog.networking;

import xyz.volcanobay.modog.networking.packets.connection.A2ANotifyNewConnectionPacket;
import xyz.volcanobay.modog.networking.packets.connection.S2CRespondConnectionAssignmentsPacket;
import xyz.volcanobay.modog.networking.packets.world.*;
import xyz.volcanobay.modog.networking.packets.S2CUnimportantPacket;
import xyz.volcanobay.modog.networking.packets.connection.C2SValidateNetworkVersionPacket;

import java.util.Arrays;
import java.util.function.Supplier;

import static xyz.volcanobay.modog.networking.DeltaNetwork.LAST_PACKET_ID;

public enum DeltaPacket {
    VALIDATE_PACKET_VERSION(C2SValidateNetworkVersionPacket::new),
    UPDATE_OBJECT_DELEGATION(A2ADelegatedObjectUpdatePacket::new),
    NOT_IMPORTANT_PACKET_DONT_LOOK(S2CUnimportantPacket::new),
    CURSOR_UPDATE_PACKET(A2ACursorUpdatePacket::new),
    //TODO RENAME TO ACTUAL ENUM NAMES ICBA
    A2AObjectUpdateStatePacket(A2AObjectUpdateStatePacket::new),
    S2CFillStageContentsUpdatePacket(S2CFillLevelContentsPacket::new),
    S2CJointCreatedPacket(S2CJointCreatedPacket::new),
    S2CRemoveJointsPacket(S2CRemoveJointsPacket::new),
    S2CRemoveObjectsPacket(S2CRemoveObjectsPacket::new),
    S2CRespondConnectionAssignmentsPacket(S2CRespondConnectionAssignmentsPacket::new),
    C2SRequestConnectionAssignmentsPacket(A2ANotifyNewConnectionPacket::new),
    C2SRequestFillLevelContents(C2SRequestLevelContents::new);
    final Supplier<Packet> packetFactory;
    final int id;
    
    DeltaPacket(Supplier<Packet> packetFactory) {
        this.packetFactory = packetFactory;
        this.id = LAST_PACKET_ID;
        LAST_PACKET_ID++;
    }
    
    public static DeltaPacket getPacketById(int packetId) {
        return Arrays.stream(values()).filter(deltaPacket -> deltaPacket.id == packetId)
            .findFirst().orElseThrow();
    }
}
