package xyz.volcanobay.modog.networking;

import xyz.volcanobay.modog.networking.packets.connection.C2SRequestConnectionAssignmentsPacket;
import xyz.volcanobay.modog.networking.packets.connection.S2CRespondConnectionAssignmentsPacket;
import xyz.volcanobay.modog.networking.packets.world.*;
import xyz.volcanobay.modog.networking.packets.S2CUnimportantPacket;
import xyz.volcanobay.modog.networking.packets.connection.C2SValidateNetworkVersionPacket;
import xyz.volcanobay.modog.networking.stream.NetworkByteReadStream;

import java.util.Arrays;
import java.util.function.Function;

import static xyz.volcanobay.modog.networking.DeltaNetwork.LAST_PACKET_ID;

public enum DeltaPacket {
    VALIDATE_PACKET_VERSION(C2SValidateNetworkVersionPacket::new),
    UPDATE_STAGE(S2CStageUpdatePacket::new),
    NOT_IMPORTANT_PACKET_DONT_LOOK(S2CUnimportantPacket::new),
    CURSOR_UPDATE_PACKET(A2ACursorUpdatePacket::new),
    //TODO RENAME TO ACTUAL ENUIM NAMES ICBA
    A2AObjectUpdateStatePacket(A2AObjectUpdateStatePacket::new),
    S2CFillStageContentsUpdatePacket(S2CFillStageContentsUpdatePacket::new),
    S2CJointCreatedPacket(S2CJointCreatedPacket::new),
    S2CRemoveJointsPacket(S2CRemoveJointsPacket::new),
    S2CRemoveObjectsPacket(S2CRemoveObjectsPacket::new),
    S2CStageUpdatePacket(S2CStageUpdatePacket::new),
    S2CRespondConnectionAssignmentsPacket(S2CRespondConnectionAssignmentsPacket::new),
    C2SRequestConnectionAssignmentsPacket(C2SRequestConnectionAssignmentsPacket::new),
    C2SValidateNetworkVersionPacket(C2SValidateNetworkVersionPacket::new),
    A2ACursorUpdateStatePacket(A2AObjectUpdateStatePacket::new);
    final Function<NetworkByteReadStream, Packet> packetFactory;
    final int id;
    
    DeltaPacket(Function<NetworkByteReadStream, Packet> packetFactory) {
        this.packetFactory = packetFactory;
        this.id = LAST_PACKET_ID;
        LAST_PACKET_ID++;
    }
    
    public static DeltaPacket getPacketById(int packetId) {
        return Arrays.stream(values()).filter(deltaPacket -> deltaPacket.id == packetId)
            .findFirst().orElseThrow();
    }
}
