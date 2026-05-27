package Model;
import java.sql.Timestamp;

public class THONGBAO {
    private int maTB;
    private String tieuDe;
    private String noiDung;
    private Timestamp thoiGian;
    private String loai;
    private boolean daDoc;

    public THONGBAO(int maTB, String tieuDe, String noiDung, Timestamp thoiGian, String loai, boolean daDoc) {
        this.maTB = maTB;
        this.tieuDe = tieuDe;
        this.noiDung = noiDung;
        this.thoiGian = thoiGian;
        this.loai = loai;
        this.daDoc = daDoc;
    }
    public int getMaTB() {
        return maTB;
    }

    public void setMaTB(int maTB) {
        this.maTB = maTB;
    }

    public String getTieuDe() {
        return tieuDe;
    }

    public void setTieuDe(String tieuDe) {
        this.tieuDe = tieuDe;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public Timestamp getThoiGian() {
        return thoiGian;
    }

    public void setThoiGian(Timestamp thoiGian) {
        this.thoiGian = thoiGian;
    }

    public String getLoai() {
        return loai;
    }

    public void setLoai(String loai) {
        this.loai = loai;
    }

    public boolean isDaDoc() {
        return daDoc;
    }

    public void setDaDoc(boolean daDoc) {
        this.daDoc = daDoc;
    }
}