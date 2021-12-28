package com.neo.protocol.my.struct;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Data
@ToString
public
class Head {
    /**
     * 协议版本[32]：0xabef + 主版本 + 次版本
     */
    private int version = 0xabef0101;

    /**
     * 消息长度[32]：head + body
     */
    private int length;

    /**
     * 会话id[64]: 集群内唯一
     */
    private long sessionId;

    /**
     * 消息类型[8]：{@link MsgType}
     */
    private byte type;

    /**
     * 消息优先级[8]:
     */
    private byte priority;

    /**
     * 扩展字段[变长]
     */
    private Map<String, Object> attachment = new HashMap<>();


}
