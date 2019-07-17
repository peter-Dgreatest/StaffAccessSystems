package com.itcrusaders.staffaccesssystems.Admin;

public class VisitorLoginRecords {

    public String vid;
    public String vname;
    public String vmobile;
    public String vappoin;
    public String vpurpose;
    public String vimgsrc;
    public String appId;
    public String vChurch;
    public String vgroup;
    public String vwho2see;
    public String time;
    public String vmail;
    public String appoindate;

    public VisitorLoginRecords(String id, String vname, String vmobile, String vappoin,
                               String vpurpose, String vimgsrc, String vChurch, String vgroup, String vwho2see, String time) {
        this.vid=id;
        this.vname = vname;
        this.vmobile = vmobile;
        this.vappoin = vappoin;
        this.vpurpose = vpurpose;
        this.vimgsrc = vimgsrc;
        this.vChurch = vChurch;
        this.vgroup = vgroup;
        this.vwho2see = vwho2see;
        this.time=time;
    }

    public VisitorLoginRecords(String id,String vname, String vmobile, String vappoin,
                               String vpurpose, String vimgsrc, String vChurch, String vgroup, String vwho2see) {
        this.vid=id;
        this.vname = vname;
        this.vmobile = vmobile;
        this.vappoin = vappoin;
        this.vpurpose = vpurpose;
        this.vimgsrc = vimgsrc;
        this.vChurch = vChurch;
        this.vgroup = vgroup;
        this.vwho2see = vwho2see;
    }

    public VisitorLoginRecords(String id,String appId,String vname, String vmobile,
                                String vimgsrc) {
        this.vid=id;
        this.vname = vname;
        this.vmobile = vmobile;
        this.vimgsrc = vimgsrc;
        this.appId = appId;
    }

}
