package com.neo.protocol.my.struct;

import com.neo.protocol.my.struct.Head;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public
class Msg {
    private Head head;
    private Object body;

    public Msg authReq() {

    }
}
