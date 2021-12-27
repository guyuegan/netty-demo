package com.neo.protocol.my;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
class Msg {
    private Head head;
    private Object body;
}
