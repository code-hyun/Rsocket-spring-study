package com.rsocket.client_sender.protocol;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Header {
    private String tid;
    private String nadid;
    private String vrCodec;
    private String vin;
    private String hostName;
    private String brand;

    public Header(String tid, String nadid, String vin, String vrCodec, String hostName, String brand) {
        this.tid = tid;
        this.nadid = nadid;
        this.vin = vin;
        this.vrCodec = vrCodec;
        this.hostName = hostName;
        this.brand = brand;
    }
    public Header(String tid, String nadid, String vin, String vrCodec) {
        this.tid = tid;
        this.nadid = nadid;
        this.vin = vin;
        this.vrCodec = vrCodec;
    }
}
