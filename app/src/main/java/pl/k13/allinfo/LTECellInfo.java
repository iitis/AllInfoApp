package pl.k13.allinfo;


public class LTECellInfo
{
    private String CI_NetName;
    private long CI_Ci;
    private int CI_Mnc;
    private int CI_Pci;
    private int CI_Tac;

    private int SS_Dbm;
    private int SS_Rsrp;
    private int SS_Rsrq;
    private long SS_Rssnr;


    public LTECellInfo()
    {
        this.CI_NetName = "";
        this.CI_Ci = -256;
        this.CI_Mnc = -256;
        this.CI_Pci = -256;
        this.CI_Tac = -256;
        this.SS_Dbm = -256;
        this.SS_Rsrp = -256;
        this.SS_Rsrq = -256;
        this.SS_Rssnr = -256;
    }

    public LTECellInfo(String CI_NetName, int CI_Ci, int CI_Mnc, int CI_Pci, int CI_Tac, int SS_Dbm, int SS_Rsrp, int SS_Rsrq, int SS_Rssnr)
    {
        this.CI_NetName = CI_NetName;
        this.CI_Ci = CI_Ci;
        this.CI_Mnc = CI_Mnc;
        this.CI_Pci = CI_Pci;
        this.CI_Tac = CI_Tac;
        this.SS_Dbm = SS_Dbm;
        this.SS_Rsrp = SS_Rsrp;
        this.SS_Rsrq = SS_Rsrq;
        this.SS_Rssnr = SS_Rssnr;
    }
    public String getCI_NetName()
    {
        return CI_NetName;
    }

    public void setCI_NetName(String CI_NetName)
    {
        this.CI_NetName = CI_NetName;
    }

    public long getCI_Ci()
    {
        return CI_Ci;
    }

    public void setCI_Ci(long CI_Ci)
    {
        this.CI_Ci = CI_Ci;
    }

    public int getCI_Mnc()
    {
        return CI_Mnc;
    }

    public void setCI_Mnc(int CI_Mnc)
    {
        this.CI_Mnc = CI_Mnc;
    }

    public int getCI_Pci()
    {
        return CI_Pci;
    }

    public void setCI_Pci(int CI_Pci)
    {
        this.CI_Pci = CI_Pci;
    }

    public int getCI_Tac()
    {
        return CI_Tac;
    }

    public void setCI_Tac(int CI_Tac)
    {
        this.CI_Tac = CI_Tac;
    }

    public int getSS_Dbm()
    {
        return SS_Dbm;
    }

    public void setSS_Dbm(int SS_Dbm)
    {
        this.SS_Dbm = SS_Dbm;
    }

    public int getSS_Rsrp()
    {
        return SS_Rsrp;
    }

    public void setSS_Rsrp(int SS_Rsrp)
    {
        this.SS_Rsrp = SS_Rsrp;
    }

    public int getSS_Rsrq()
    {
        return SS_Rsrq;
    }

    public void setSS_Rsrq(int SS_Rsrq)
    {
        this.SS_Rsrq = SS_Rsrq;
    }

    public long getSS_Rssnr()
    {
        return SS_Rssnr;
    }

    public void setSS_Rssnr(long SS_Rssnr)
    {
        this.SS_Rssnr = SS_Rssnr;
    }

    @Override
    public String toString()
    {
        return "LTECellInfo{" +
                "CI_NetName='" + CI_NetName + '\'' +
                ", CI_Ci=" + CI_Ci +
                ", CI_Mnc=" + CI_Mnc +
                ", CI_Pci=" + CI_Pci +
                ", CI_Tac=" + CI_Tac +
                ", SS_Dbm=" + SS_Dbm +
                ", SS_Rsrp=" + SS_Rsrp +
                ", SS_Rsrq=" + SS_Rsrq +
                ", SS_Rssnr=" + SS_Rssnr +
                '}';
    }
}
