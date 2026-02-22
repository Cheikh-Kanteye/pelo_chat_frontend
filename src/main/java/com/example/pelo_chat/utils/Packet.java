package com.example.pelo_chat.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Packet {

    private String action;
    private String from;
    private String to;
    private String content;


}
