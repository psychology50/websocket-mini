package com.example.socket.chats.common.guid;

import org.springframework.stereotype.Component;
import com.github.f4b6a3.tsid.TsidCreator;

@Component
public class TsidGenerator implements IdGenerator<Long> {
    @Override
    public Long execute() {
        return TsidCreator.getTsid256().toLong();
    }
}
