package com.m4thg33k.tombmanygraves.network;

import com.m4thg33k.tombmanygraves.TombManyGraves;

public class GravePosTogglePacket{

    public void execute() {
        TombManyGraves.proxy.toggleGravePositionRendering();
    }
}
