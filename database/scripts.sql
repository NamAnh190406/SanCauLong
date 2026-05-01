
-- ============================================================
-- 1. TAIKHOAN
-- ============================================================
CREATE TABLE TAIKHOAN (
    Ma_TK       VARCHAR2(20) NOT NULL PRIMARY KEY,
    Username    VARCHAR2(50) NOT NULL UNIQUE,
    Password    VARCHAR2(255) NOT NULL,
    VaiTro      VARCHAR2(20) NOT NULL,
    TrangThai   VARCHAR2(20) DEFAULT 'HoatDong' NOT NULL,
    CONSTRAINT chk_tk_role      CHECK (VaiTro IN ('Admin','NhanVien','KhachHang')),
    CONSTRAINT chk_tk_trangthai CHECK (TrangThai IN ('HoatDong','KhoaAccount')),
    CONSTRAINT chk_tk_username  CHECK (LENGTH(Username) >= 4)
);

-- ============================================================
-- 2. NHAN_VIEN
-- ============================================================
CREATE TABLE NHAN_VIEN (
    Ma_NV       VARCHAR2(20) NOT NULL PRIMARY KEY,
    Hoten_nv    VARCHAR2(100) NOT NULL,
    SDT         VARCHAR2(15) NOT NULL UNIQUE,
    ChucVu      VARCHAR2(50) NOT NULL,
    CaLamViec   VARCHAR2(10) NOT NULL,
    Ma_TK       VARCHAR2(20) UNIQUE,
    -- Oracle không có ON UPDATE CASCADE, mình chỉ giữ lại ON DELETE SET NULL thôi
    CONSTRAINT fk_nv_taikhoan FOREIGN KEY (Ma_TK) 
        REFERENCES TAIKHOAN(Ma_TK) ON DELETE SET NULL,
    -- Sửa lại hàm check SĐT chuẩn Oracle
    CONSTRAINT chk_nv_sdt CHECK (REGEXP_LIKE(SDT, '^[0-9]{9,11}$')),
    CONSTRAINT chk_nv_calamviec CHECK (CaLamViec IN ('Ca1','Ca2','Ca3'))
);

-- ============================================================
-- 3. KHACHHANG
CREATE TABLE KHACHHANG (
    MaKH            VARCHAR2(20) NOT NULL PRIMARY KEY,
    HoTen           VARCHAR2(100) NOT NULL,
    SDT             VARCHAR2(15) NOT NULL UNIQUE,
    Email           VARCHAR2(100) UNIQUE,
    -- Oracle dùng SYSDATE để lấy ngày hiện tại
    NgayDK          DATE DEFAULT SYSDATE NOT NULL,
    HangThanhVien   VARCHAR2(20) DEFAULT 'Dong' NOT NULL,
    DiemTichLuy     NUMBER DEFAULT 0 NOT NULL,
    Ma_TK           VARCHAR2(20) UNIQUE,
    -- Chỉ giữ lại ON DELETE SET NULL
    CONSTRAINT fk_kh_taikhoan FOREIGN KEY (Ma_TK) 
        REFERENCES TAIKHOAN(Ma_TK) ON DELETE SET NULL,
    -- Cú pháp check SĐT chuẩn Oracle
    CONSTRAINT chk_kh_sdt     CHECK (REGEXP_LIKE(SDT, '^[0-9]{9,11}$')),
    CONSTRAINT chk_kh_email   CHECK (Email LIKE '%@%.%'),
    CONSTRAINT chk_kh_diem    CHECK (DiemTichLuy >= 0),
    CONSTRAINT chk_kh_hang    CHECK (HangThanhVien IN ('Dong','Bac','Vang','BachKim'))
);
-- ============================================================
CREATE TABLE KHACHHANG (
    MaKH            VARCHAR2(20) NOT NULL PRIMARY KEY,
    HoTen           VARCHAR2(100) NOT NULL,
    SDT             VARCHAR2(15) NOT NULL UNIQUE,
    Email           VARCHAR2(100) UNIQUE,
    -- Oracle dùng SYSDATE để lấy ngày hiện tại
    NgayDK          DATE DEFAULT SYSDATE NOT NULL,
    HangThanhVien   VARCHAR2(20) DEFAULT 'Dong' NOT NULL,
    DiemTichLuy     NUMBER DEFAULT 0 NOT NULL,
    Ma_TK           VARCHAR2(20) UNIQUE,
    -- Chỉ giữ lại ON DELETE SET NULL
    CONSTRAINT fk_kh_taikhoan FOREIGN KEY (Ma_TK) 
        REFERENCES TAIKHOAN(Ma_TK) ON DELETE SET NULL,
    -- Cú pháp check SĐT chuẩn Oracle
    CONSTRAINT chk_kh_sdt     CHECK (REGEXP_LIKE(SDT, '^[0-9]{9,11}$')),
    CONSTRAINT chk_kh_email   CHECK (Email LIKE '%@%.%'),
    CONSTRAINT chk_kh_diem    CHECK (DiemTichLuy >= 0),
    CONSTRAINT chk_kh_hang    CHECK (HangThanhVien IN ('Dong','Bac','Vang','BachKim'))
);

-- ============================================================
-- 4. SAN
-- ============================================================
CREATE TABLE SAN (
    MaSan               VARCHAR2(20) NOT NULL PRIMARY KEY,
    TenSan              VARCHAR2(100) NOT NULL,
    LoaiSan             VARCHAR2(50) NOT NULL,
    LoaiMatSan          VARCHAR2(50) NOT NULL,
    KhongGian           VARCHAR2(50),
    SLNguoiChoi         NUMBER NOT NULL,
    GiaThueTheoGio      NUMBER NOT NULL,
    TrangThai           VARCHAR2(20) DEFAULT 'HoatDong' NOT NULL,
    MoTa                VARCHAR2(500),
    DiaChi              VARCHAR2(255) NOT NULL,
    CONSTRAINT chk_san_gia      CHECK (GiaThueTheoGio > 0),
    CONSTRAINT chk_san_slng     CHECK (SLNguoiChoi > 0),
    CONSTRAINT chk_san_tt       CHECK (TrangThai IN ('HoatDong','BaoDuong','Dong'))
);

-- ============================================================
-- 5. KHUNGGIO
-- ============================================================
CREATE TABLE KHUNGGIO (
    MaKG        VARCHAR2(20) NOT NULL PRIMARY KEY,
    -- Trong Oracle dùng TIMESTAMP để lưu cả ngày và giờ, hoặc chỉ giờ
    GioBD       TIMESTAMP NOT NULL,
    GioKT       TIMESTAMP NOT NULL,
    CONSTRAINT chk_kg_gio CHECK (GioKT > GioBD)
);


-- ============================================================
-- 6. BANGGIA
-- ============================================================
CREATE TABLE BANGGIA (
    MaBG        VARCHAR2(20) NOT NULL PRIMARY KEY,
    DonGia      NUMBER NOT NULL,
    MaSan       VARCHAR2(20) NOT NULL,
    MaKG        VARCHAR2(20) NOT NULL,
    -- Oracle mặc định là RESTRICT nên mình không ghi gì thêm sau REFERENCES
    CONSTRAINT fk_bg_san FOREIGN KEY (MaSan) 
        REFERENCES SAN(MaSan),
    CONSTRAINT fk_bg_khunggio FOREIGN KEY (MaKG) 
        REFERENCES KHUNGGIO(MaKG),
    CONSTRAINT chk_bg_dongie CHECK (DonGia > 0),
    CONSTRAINT uq_bg_san_kg UNIQUE (MaSan, MaKG)
);

-- ============================================================
-- 7. NGAYLE
-- ============================================================
CREATE TABLE NGAYLE (
    MaNL        VARCHAR2(20) NOT NULL PRIMARY KEY,
    TenNL       VARCHAR2(100) NOT NULL,
    NgayCuThe   DATE NOT NULL UNIQUE,
    -- Sửa lại tên cột cho đúng chính tả: GiaPhuThu
    GiaPhuThu   NUMBER DEFAULT 0 NOT NULL,
    CONSTRAINT chk_nl_giaphuthu CHECK (GiaPhuThu >= 0)
);

-- ============================================================
-- 8. DATSAN
-- ============================================================
CREATE TABLE DATSAN (
    MaDS                VARCHAR2(20) NOT NULL PRIMARY KEY,
    -- Oracle dùng SYSDATE và DEFAULT phải đứng trước NOT NULL
    NgayDat             DATE DEFAULT SYSDATE NOT NULL,
    TrangThai           VARCHAR2(20) DEFAULT 'ChoDuyet' NOT NULL,
    TongTienTamTinh     NUMBER DEFAULT 0 NOT NULL,
    MaKH                VARCHAR2(20) NOT NULL,
    MaSan               VARCHAR2(20) NOT NULL,
    MaKG                VARCHAR2(20) NOT NULL,
    -- Cấu hình khóa ngoại chuẩn Oracle (không có RESTRICT/CASCADE UPDATE)
    CONSTRAINT fk_ds_khach FOREIGN KEY (MaKH) 
        REFERENCES KHACHHANG(MaKH),
    CONSTRAINT fk_ds_san FOREIGN KEY (MaSan) 
        REFERENCES SAN(MaSan),
    CONSTRAINT fk_ds_khunggio FOREIGN KEY (MaKG) 
        REFERENCES KHUNGGIO(MaKG),
    CONSTRAINT chk_ds_tt CHECK (TrangThai IN ('ChoDuyet','DaDuyet','DaHuy','HoanThanh')),
    CONSTRAINT chk_ds_tien CHECK (TongTienTamTinh >= 0)
);

-- ============================================================
-- 9. HOADON
-- ============================================================
CREATE TABLE HOADON (
    MaHoaDon        VARCHAR2(20) NOT NULL PRIMARY KEY,
    TongTienDV      NUMBER DEFAULT 0 NOT NULL,
    SoTienGG        NUMBER DEFAULT 0 NOT NULL,
    ThanhTien       NUMBER DEFAULT 0 NOT NULL,
    Ghichu          VARCHAR2(500),
    MaDS            VARCHAR2(20) NOT NULL UNIQUE,
    -- Khóa ngoại trỏ về bảng DATSAN
    CONSTRAINT fk_hd_datsan FOREIGN KEY (MaDS) 
        REFERENCES DATSAN(MaDS),
    -- Các ràng buộc kiểm tra số tiền
    CONSTRAINT chk_hd_tiendv    CHECK (TongTienDV >= 0),
    CONSTRAINT chk_hd_gg        CHECK (SoTienGG >= 0),
    CONSTRAINT chk_hd_tt        CHECK (ThanhTien >= 0)
);

-- ============================================================
-- 10. THANHTOAN
-- ============================================================
CREATE TABLE THANHTOAN (
    MaTT            VARCHAR2(20) NOT NULL PRIMARY KEY,
    PTTT            VARCHAR2(30) NOT NULL,
    -- Oracle dùng TIMESTAMP và mặc định CURRENT_TIMESTAMP không cần ngoặc
    ThoiGianTT      TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    TrangThai       VARCHAR2(20) DEFAULT 'DangXuLy' NOT NULL,
    MaHoaDon        VARCHAR2(20) NOT NULL,
    -- Khóa ngoại trỏ về bảng HOADON (đã bỏ RESTRICT/CASCADE)
    CONSTRAINT fk_tt_hoadon FOREIGN KEY (MaHoaDon) 
        REFERENCES HOADON(MaHoaDon),
    CONSTRAINT chk_tt_pttt  CHECK (PTTT IN ('TienMat','ChuyenKhoan','TheNganHang','Vi')),
    CONSTRAINT chk_tt_trangthai CHECK (TrangThai IN ('ThanhCong','ThatBai','DangXuLy'))
);

-- ============================================================
-- 11. DICHVU
-- ============================================================
CREATE TABLE DICHVU (
    MaDV        VARCHAR2(20) NOT NULL PRIMARY KEY,
    TenDV       VARCHAR2(100) NOT NULL,
    DonViTinh   VARCHAR2(30) NOT NULL,
    GiaBan      NUMBER NOT NULL,
    SLTonkho    NUMBER DEFAULT 0 NOT NULL,
    CONSTRAINT chk_dv_gia   CHECK (GiaBan > 0),
    CONSTRAINT chk_dv_sl    CHECK (SLTonkho >= 0)
);

-- ============================================================
-- 12. CTDV
-- ============================================================
CREATE TABLE CTDV (
    MaCTDV      VARCHAR2(20) NOT NULL PRIMARY KEY,
    SoLuong     NUMBER DEFAULT 1 NOT NULL,
    ThanhTien   NUMBER NOT NULL,
    MaDS        VARCHAR2(20) NOT NULL,
    MaDV        VARCHAR2(20) NOT NULL,
    -- Giữ lại ON DELETE CASCADE cho MaDS nếu Anh muốn xóa dây chuyền
    CONSTRAINT fk_ctdv_datsan FOREIGN KEY (MaDS) 
        REFERENCES DATSAN(MaDS) ON DELETE CASCADE,
    -- MaDV để mặc định (Restrict)
    CONSTRAINT fk_ctdv_dichvu FOREIGN KEY (MaDV) 
        REFERENCES DICHVU(MaDV),
    CONSTRAINT chk_ctdv_sl      CHECK (SoLuong > 0),
    CONSTRAINT chk_ctdv_tt      CHECK (ThanhTien >= 0)
);
-- ============================================================
-- 13. KHUYENMAI
-- ============================================================
CREATE TABLE KHUYENMAI (
    MaKM            VARCHAR2(20) NOT NULL PRIMARY KEY,
    TenKM           VARCHAR2(100) NOT NULL,
    PhanTramGG      NUMBER NOT NULL,
    GTriToiDa       NUMBER,
    NgayBD          DATE NOT NULL,
    NgayKT          DATE NOT NULL,
    -- Kiểm tra phần trăm từ 1-100%
    CONSTRAINT chk_km_phantram  CHECK (PhanTramGG > 0 AND PhanTramGG <= 100),
    -- Đảm bảo ngày kết thúc không trước ngày bắt đầu
    CONSTRAINT chk_km_ngay      CHECK (NgayKT >= NgayBD),
    -- Kiểm tra giá trị tối đa nếu có nhập thì phải > 0
    CONSTRAINT chk_km_gtrimax   CHECK (GTriToiDa IS NULL OR GTriToiDa > 0)
);

-- ============================================================
-- 14. SUDUNGGG (SuDungKhuyenMai)
-- ============================================================
CREATE TABLE SuDungGG (
    MaCoupon        VARCHAR2(20) NOT NULL PRIMARY KEY,
    -- Dùng TIMESTAMP để lưu chính xác thời điểm sử dụng
    NgaySD          TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    MaKH            VARCHAR2(20) NOT NULL,
    MaKM            VARCHAR2(20) NOT NULL,
    -- Khóa ngoại trỏ về bảng KHACHHANG
    CONSTRAINT fk_sdgg_khach FOREIGN KEY (MaKH) 
        REFERENCES KHACHHANG(MaKH),
    -- Khóa ngoại trỏ về bảng KHUYENMAI
    CONSTRAINT fk_sdgg_khuyenmai FOREIGN KEY (MaKM) 
        REFERENCES KHUYENMAI(MaKM),
    -- Ràng buộc để mỗi khách hàng chỉ được dùng 1 mã khuyến mãi đúng 1 lần
    CONSTRAINT uq_sdgg_kh_km UNIQUE (MaKH, MaKM)
);

-- ============================================================
-- 15. DANHGIASAN
-- ============================================================
CREATE TABLE DANHGIASAN (
    MaDanhGia           VARCHAR2(20) NOT NULL PRIMARY KEY,
    DiemDG              NUMBER NOT NULL,
    NhanXet             VARCHAR2(1000),
    -- Dùng TIMESTAMP để lưu chính xác giờ phút giây đánh giá
    ThoiDiemDanhGia     TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    MaKH                VARCHAR2(20) NOT NULL,
    MaSan               VARCHAR2(20) NOT NULL,
    -- Khóa ngoại trỏ về KHACHHANG
    CONSTRAINT fk_dg_khach FOREIGN KEY (MaKH) 
        REFERENCES KHACHHANG(MaKH),
    -- Khóa ngoại trỏ về SAN
    CONSTRAINT fk_dg_san FOREIGN KEY (MaSan) 
        REFERENCES SAN(MaSan),
    -- Chặn điểm đánh giá từ 1 đến 5 sao
    CONSTRAINT chk_dg_diem  CHECK (DiemDG BETWEEN 1 AND 5),
    -- Đảm bảo mỗi khách chỉ đánh giá một sân 1 lần (tránh spam)
    CONSTRAINT uq_dg_kh_san UNIQUE (MaKH, MaSan)
);

-------------THÊM DỮ LIỆU-----------------------------------

INSERT INTO TAIKHOAN (Ma_TK, Username, Password, VaiTro, TrangThai) VALUES
    ('TK001', 'admin01',    'Admin@123',   'Admin',      'HoatDong');
INSERT INTO TAIKHOAN (Ma_TK, Username, Password, VaiTro, TrangThai) VALUES
    ('TK002', 'nhanvien01', 'Nv123456',    'NhanVien',   'HoatDong');
INSERT INTO TAIKHOAN (Ma_TK, Username, Password, VaiTro, TrangThai) VALUES
    ('TK003', 'nhanvien02', 'Nv123456',    'NhanVien',   'HoatDong');
INSERT INTO TAIKHOAN (Ma_TK, Username, Password, VaiTro, TrangThai) VALUES
    ('TK004', 'nguyen_an',  'Pass123456',  'KhachHang',  'HoatDong');
INSERT INTO TAIKHOAN (Ma_TK, Username, Password, VaiTro, TrangThai) VALUES
    ('TK005', 'tran_binh',  'Pass123456',  'KhachHang',  'HoatDong');
INSERT INTO TAIKHOAN (Ma_TK, Username, Password, VaiTro, TrangThai) VALUES
    ('TK006', 'le_cam',     'Pass123456',  'KhachHang',  'HoatDong');
INSERT INTO TAIKHOAN (Ma_TK, Username, Password, VaiTro, TrangThai) VALUES
    ('TK007', 'pham_dung',  'Pass123456',  'KhachHang',  'HoatDong');
INSERT INTO TAIKHOAN (Ma_TK, Username, Password, VaiTro, TrangThai) VALUES
    ('TK008', 'hoang_em',   'Pass123456',  'KhachHang',  'HoatDong');
INSERT INTO TAIKHOAN (Ma_TK, Username, Password, VaiTro, TrangThai) VALUES
    ('TK009', 'vu_phuong',  'Pass123456',  'KhachHang',  'KhoaAccount');
INSERT INTO TAIKHOAN (Ma_TK, Username, Password, VaiTro, TrangThai) VALUES
    ('TK010', 'nhanvien03', 'Nv123456',    'NhanVien',   'HoatDong');

-- -------------------------------------------------------
-- 3.2 NHAN_VIEN
-- -------------------------------------------------------
INSERT INTO NHAN_VIEN (Ma_NV, Hoten_nv, SDT, ChucVu, CaLamViec, Ma_TK) VALUES
    ('NV001', 'Trần Văn Hùng',    '0901234561', 'Quản Lý',      'Ca1', 'TK002');
INSERT INTO NHAN_VIEN (Ma_NV, Hoten_nv, SDT, ChucVu, CaLamViec, Ma_TK) VALUES
    ('NV002', 'Nguyễn Thị Lan',   '0901234562', 'Thu Ngân',     'Ca2', 'TK003');
INSERT INTO NHAN_VIEN (Ma_NV, Hoten_nv, SDT, ChucVu, CaLamViec, Ma_TK) VALUES
    ('NV003', 'Lê Quốc Bảo',      '0901234563', 'Bảo Vệ',       'Ca3', 'TK010');
INSERT INTO NHAN_VIEN (Ma_NV, Hoten_nv, SDT, ChucVu, CaLamViec, Ma_TK) VALUES
    ('NV004', 'Phạm Minh Tuấn',   '0901234564', 'Kỹ Thuật',     'Ca1', NULL);
INSERT INTO NHAN_VIEN (Ma_NV, Hoten_nv, SDT, ChucVu, CaLamViec, Ma_TK) VALUES
    ('NV005', 'Đỗ Thị Hoa',       '0901234565', 'Lễ Tân',       'Ca2', NULL);

-- -------------------------------------------------------
-- 3.3 KHACHHANG
-- -------------------------------------------------------
INSERT INTO KHACHHANG (MaKH, HoTen, SDT, Email, NgayDK, HangThanhVien, DiemTichLuy, Ma_TK) VALUES
    ('KH001', 'Nguyễn Văn An',    '0912345671', 'an.nguyen@gmail.com',    DATE '2023-01-15', 'Vang',    650, 'TK004');
INSERT INTO KHACHHANG (MaKH, HoTen, SDT, Email, NgayDK, HangThanhVien, DiemTichLuy, Ma_TK) VALUES
    ('KH002', 'Trần Thị Bình',    '0912345672', 'binh.tran@gmail.com',    DATE '2023-03-20', 'Bac',     210, 'TK005');
INSERT INTO KHACHHANG (MaKH, HoTen, SDT, Email, NgayDK, HangThanhVien, DiemTichLuy, Ma_TK) VALUES
    ('KH003', 'Lê Thị Cẩm',       '0912345673', 'cam.le@gmail.com',       DATE '2023-06-10', 'Dong',    45,  'TK006');
INSERT INTO KHACHHANG (MaKH, HoTen, SDT, Email, NgayDK, HangThanhVien, DiemTichLuy, Ma_TK) VALUES
    ('KH004', 'Phạm Văn Dũng',    '0912345674', 'dung.pham@gmail.com',    DATE '2023-08-05', 'BachKim', 1200,'TK007');
INSERT INTO KHACHHANG (MaKH, HoTen, SDT, Email, NgayDK, HangThanhVien, DiemTichLuy, Ma_TK) VALUES
    ('KH005', 'Hoàng Thị Em',     '0912345675', 'em.hoang@gmail.com',     DATE '2024-01-12', 'Dong',    30,  'TK008');
INSERT INTO KHACHHANG (MaKH, HoTen, SDT, Email, NgayDK, HangThanhVien, DiemTichLuy, Ma_TK) VALUES
    ('KH006', 'Vũ Minh Phương',   '0912345676', 'phuong.vu@gmail.com',    DATE '2023-11-20', 'Dong',    0,   'TK009');
INSERT INTO KHACHHANG (MaKH, HoTen, SDT, Email, NgayDK, HangThanhVien, DiemTichLuy, Ma_TK) VALUES
    ('KH007', 'Đinh Thị Giang',   '0912345677', 'giang.dinh@gmail.com',   DATE '2024-02-14', 'Bac',     150, NULL);
INSERT INTO KHACHHANG (MaKH, HoTen, SDT, Email, NgayDK, HangThanhVien, DiemTichLuy, Ma_TK) VALUES
    ('KH008', 'Bùi Quang Hải',    '0912345678', 'hai.bui@yahoo.com',      DATE '2024-03-01', 'Dong',    80,  NULL);

-- -------------------------------------------------------
-- 3.4 SAN
-- -------------------------------------------------------
INSERT INTO SAN (MaSan, TenSan, LoaiSan, LoaiMatSan, KhongGian, SLNguoiChoi, GiaThueTheoGio, TrangThai, MoTa, DiaChi) VALUES
    ('S01', 'Sân A1', 'Đơn', 'Gỗ',       'NhàCầu',   2, 80000,  'HoatDong', 'Sân đơn gỗ tiêu chuẩn',          '123 Nguyễn Trãi, Q1, TP.HCM');
INSERT INTO SAN (MaSan, TenSan, LoaiSan, LoaiMatSan, KhongGian, SLNguoiChoi, GiaThueTheoGio, TrangThai, MoTa, DiaChi) VALUES
    ('S02', 'Sân A2', 'Đôi', 'Gỗ',       'NhàCầu',   4, 120000, 'HoatDong', 'Sân đôi gỗ cao cấp',             '123 Nguyễn Trãi, Q1, TP.HCM');
INSERT INTO SAN (MaSan, TenSan, LoaiSan, LoaiMatSan, KhongGian, SLNguoiChoi, GiaThueTheoGio, TrangThai, MoTa, DiaChi) VALUES
    ('S03', 'Sân B1', 'Đơn', 'Nhựa PVC', 'NgoàiTrời',2, 60000,  'HoatDong', 'Sân đơn ngoài trời giá rẻ',      '45 Lê Lợi, Q3, TP.HCM');
INSERT INTO SAN (MaSan, TenSan, LoaiSan, LoaiMatSan, KhongGian, SLNguoiChoi, GiaThueTheoGio, TrangThai, MoTa, DiaChi) VALUES
    ('S04', 'Sân B2', 'Đôi', 'Nhựa PVC', 'NgoàiTrời',4, 100000, 'BaoDuong', 'Sân đôi đang bảo dưỡng',         '45 Lê Lợi, Q3, TP.HCM');
INSERT INTO SAN (MaSan, TenSan, LoaiSan, LoaiMatSan, KhongGian, SLNguoiChoi, GiaThueTheoGio, TrangThai, MoTa, DiaChi) VALUES
    ('S05', 'Sân C1', 'Đôi', 'Cao Su',   'NhàCầu',   4, 150000, 'HoatDong', 'Sân đôi cao su cao cấp VIP',      '88 Trần Hưng Đạo, Q5, TP.HCM');
INSERT INTO SAN (MaSan, TenSan, LoaiSan, LoaiMatSan, KhongGian, SLNguoiChoi, GiaThueTheoGio, TrangThai, MoTa, DiaChi) VALUES
    ('S06', 'Sân C2', 'Đơn', 'Cao Su',   'NhàCầu',   2, 90000,  'HoatDong', 'Sân đơn cao su trong nhà',        '88 Trần Hưng Đạo, Q5, TP.HCM');
INSERT INTO SAN (MaSan, TenSan, LoaiSan, LoaiMatSan, KhongGian, SLNguoiChoi, GiaThueTheoGio, TrangThai, MoTa, DiaChi) VALUES
    ('S07', 'Sân D1', 'Đôi', 'Gỗ',       'NhàCầu',   4, 130000, 'Dong',     'Sân tạm đóng',                   '200 Cách Mạng Tháng 8, Q10, TP.HCM');

-- -------------------------------------------------------
-- 3.5 KHUNGGIO
-- (Dùng TO_TIMESTAMP để insert chính xác)
-- -------------------------------------------------------
INSERT INTO KHUNGGIO (MaKG, GioBD, GioKT) VALUES
    ('KG001', TO_TIMESTAMP('2025-01-01 06:00:00', 'YYYY-MM-DD HH24:MI:SS'),
              TO_TIMESTAMP('2025-01-01 07:30:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO KHUNGGIO (MaKG, GioBD, GioKT) VALUES
    ('KG002', TO_TIMESTAMP('2025-01-01 07:30:00', 'YYYY-MM-DD HH24:MI:SS'),
              TO_TIMESTAMP('2025-01-01 09:00:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO KHUNGGIO (MaKG, GioBD, GioKT) VALUES
    ('KG003', TO_TIMESTAMP('2025-01-01 09:00:00', 'YYYY-MM-DD HH24:MI:SS'),
              TO_TIMESTAMP('2025-01-01 10:30:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO KHUNGGIO (MaKG, GioBD, GioKT) VALUES
    ('KG004', TO_TIMESTAMP('2025-01-01 10:30:00', 'YYYY-MM-DD HH24:MI:SS'),
              TO_TIMESTAMP('2025-01-01 12:00:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO KHUNGGIO (MaKG, GioBD, GioKT) VALUES
    ('KG005', TO_TIMESTAMP('2025-01-01 13:00:00', 'YYYY-MM-DD HH24:MI:SS'),
              TO_TIMESTAMP('2025-01-01 14:30:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO KHUNGGIO (MaKG, GioBD, GioKT) VALUES
    ('KG006', TO_TIMESTAMP('2025-01-01 14:30:00', 'YYYY-MM-DD HH24:MI:SS'),
              TO_TIMESTAMP('2025-01-01 16:00:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO KHUNGGIO (MaKG, GioBD, GioKT) VALUES
    ('KG007', TO_TIMESTAMP('2025-01-01 16:00:00', 'YYYY-MM-DD HH24:MI:SS'),
              TO_TIMESTAMP('2025-01-01 17:30:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO KHUNGGIO (MaKG, GioBD, GioKT) VALUES
    ('KG008', TO_TIMESTAMP('2025-01-01 17:30:00', 'YYYY-MM-DD HH24:MI:SS'),
              TO_TIMESTAMP('2025-01-01 19:00:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO KHUNGGIO (MaKG, GioBD, GioKT) VALUES
    ('KG009', TO_TIMESTAMP('2025-01-01 19:00:00', 'YYYY-MM-DD HH24:MI:SS'),
              TO_TIMESTAMP('2025-01-01 20:30:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO KHUNGGIO (MaKG, GioBD, GioKT) VALUES
    ('KG010', TO_TIMESTAMP('2025-01-01 20:30:00', 'YYYY-MM-DD HH24:MI:SS'),
              TO_TIMESTAMP('2025-01-01 22:00:00', 'YYYY-MM-DD HH24:MI:SS'));

-- -------------------------------------------------------
-- 3.6 BANGGIA
-- -------------------------------------------------------
-- Sân A1 (80k/giờ) - các khung giờ sáng/chiều/tối
INSERT INTO BANGGIA (MaBG, DonGia, MaSan, MaKG) VALUES ('BG001', 80000,  'S01', 'KG001');
INSERT INTO BANGGIA (MaBG, DonGia, MaSan, MaKG) VALUES ('BG002', 80000,  'S01', 'KG002');
INSERT INTO BANGGIA (MaBG, DonGia, MaSan, MaKG) VALUES ('BG003', 100000, 'S01', 'KG008'); -- giờ cao điểm tối
INSERT INTO BANGGIA (MaBG, DonGia, MaSan, MaKG) VALUES ('BG004', 100000, 'S01', 'KG009');
-- Sân A2 (120k/giờ)
INSERT INTO BANGGIA (MaBG, DonGia, MaSan, MaKG) VALUES ('BG005', 120000, 'S02', 'KG003');
INSERT INTO BANGGIA (MaBG, DonGia, MaSan, MaKG) VALUES ('BG006', 120000, 'S02', 'KG004');
INSERT INTO BANGGIA (MaBG, DonGia, MaSan, MaKG) VALUES ('BG007', 150000, 'S02', 'KG008');
-- Sân B1 (60k/giờ)
INSERT INTO BANGGIA (MaBG, DonGia, MaSan, MaKG) VALUES ('BG008', 60000,  'S03', 'KG001');
INSERT INTO BANGGIA (MaBG, DonGia, MaSan, MaKG) VALUES ('BG009', 60000,  'S03', 'KG005');
INSERT INTO BANGGIA (MaBG, DonGia, MaSan, MaKG) VALUES ('BG010', 80000,  'S03', 'KG009');
-- Sân C1 (150k/giờ) VIP
INSERT INTO BANGGIA (MaBG, DonGia, MaSan, MaKG) VALUES ('BG011', 150000, 'S05', 'KG005');
INSERT INTO BANGGIA (MaBG, DonGia, MaSan, MaKG) VALUES ('BG012', 150000, 'S05', 'KG006');
INSERT INTO BANGGIA (MaBG, DonGia, MaSan, MaKG) VALUES ('BG013', 200000, 'S05', 'KG009');
-- Sân C2
INSERT INTO BANGGIA (MaBG, DonGia, MaSan, MaKG) VALUES ('BG014', 90000,  'S06', 'KG002');
INSERT INTO BANGGIA (MaBG, DonGia, MaSan, MaKG) VALUES ('BG015', 90000,  'S06', 'KG007');

-- -------------------------------------------------------
-- 3.7 NGAYLE
-- -------------------------------------------------------
INSERT INTO NGAYLE (MaNL, TenNL, NgayCuThe, GiaPhuThu) VALUES
    ('NL001', 'Tết Dương Lịch',         DATE '2025-01-01', 50000);
INSERT INTO NGAYLE (MaNL, TenNL, NgayCuThe, GiaPhuThu) VALUES
    ('NL002', 'Giỗ Tổ Hùng Vương',      DATE '2025-04-07', 30000);
INSERT INTO NGAYLE (MaNL, TenNL, NgayCuThe, GiaPhuThu) VALUES
    ('NL003', 'Ngày Giải Phóng',         DATE '2025-04-30', 50000);
INSERT INTO NGAYLE (MaNL, TenNL, NgayCuThe, GiaPhuThu) VALUES
    ('NL004', 'Ngày Quốc Tế Lao Động',  DATE '2025-05-01', 50000);
INSERT INTO NGAYLE (MaNL, TenNL, NgayCuThe, GiaPhuThu) VALUES
    ('NL005', 'Quốc Khánh 2/9',         DATE '2025-09-02', 50000);

-- -------------------------------------------------------
-- 3.8 KHUYENMAI
-- -------------------------------------------------------
INSERT INTO KHUYENMAI (MaKM, TenKM, PhanTramGG, GTriToiDa, NgayBD, NgayKT) VALUES
    ('KM001', 'Khuyến Mãi Tân Niên 2025',       10, 50000,  DATE '2025-01-01', DATE '2025-01-31');
INSERT INTO KHUYENMAI (MaKM, TenKM, PhanTramGG, GTriToiDa, NgayBD, NgayKT) VALUES
    ('KM002', 'Ưu Đãi Thành Viên Bạch Kim',      20, 100000, DATE '2025-01-01', DATE '2025-12-31');
INSERT INTO KHUYENMAI (MaKM, TenKM, PhanTramGG, GTriToiDa, NgayBD, NgayKT) VALUES
    ('KM003', 'Flash Sale Cuối Tuần',             15, 75000,  DATE '2025-03-01', DATE '2025-03-31');
INSERT INTO KHUYENMAI (MaKM, TenKM, PhanTramGG, GTriToiDa, NgayBD, NgayKT) VALUES
    ('KM004', 'Tri Ân Khách Hàng Tháng 6',        5, 30000,  DATE '2025-06-01', DATE '2025-06-30');
INSERT INTO KHUYENMAI (MaKM, TenKM, PhanTramGG, GTriToiDa, NgayBD, NgayKT) VALUES
    ('KM005', 'Siêu Ưu Đãi Ngày Sinh Nhật',      25, 150000, DATE '2025-01-01', DATE '2025-12-31');

-- -------------------------------------------------------
-- 3.9 DICHVU
-- -------------------------------------------------------
INSERT INTO DICHVU (MaDV, TenDV, DonViTinh, GiaBan, SLTonkho) VALUES
    ('DV001', 'Thuê vợt',    'Cái',     30000,  50);
INSERT INTO DICHVU (MaDV, TenDV, DonViTinh, GiaBan, SLTonkho) VALUES
    ('DV002', 'Cầu', 'Hộp',    120000,  100);
INSERT INTO DICHVU (MaDV, TenDV, DonViTinh, GiaBan, SLTonkho) VALUES
    ('DV003', 'Nước khoáng',   'Chai',     50000,  20);
INSERT INTO DICHVU (MaDV, TenDV, DonViTinh, GiaBan, SLTonkho) VALUES
    ('DV004', 'Nước ngọt',   'Chai',    15000,  200);
INSERT INTO DICHVU (MaDV, TenDV, DonViTinh, GiaBan, SLTonkho) VALUES
    ('DV005', 'Khăn thể thao',        'Cái',     20000,  80);

-- -------------------------------------------------------
-- 3.10 DATSAN
-- -------------------------------------------------------
INSERT INTO DATSAN (MaDS, NgayDat, TrangThai, TongTienTamTinh, MaKH, MaSan, MaKG) VALUES
    ('DS001', DATE '2025-04-10', 'HoanThanh', 120000, 'KH001', 'S01', 'KG001');
INSERT INTO DATSAN (MaDS, NgayDat, TrangThai, TongTienTamTinh, MaKH, MaSan, MaKG) VALUES
    ('DS002', DATE '2025-04-11', 'HoanThanh', 180000, 'KH002', 'S02', 'KG003');
INSERT INTO DATSAN (MaDS, NgayDat, TrangThai, TongTienTamTinh, MaKH, MaSan, MaKG) VALUES
    ('DS003', DATE '2025-04-12', 'HoanThanh', 225000, 'KH004', 'S05', 'KG005');
INSERT INTO DATSAN (MaDS, NgayDat, TrangThai, TongTienTamTinh, MaKH, MaSan, MaKG) VALUES
    ('DS004', DATE '2025-04-13', 'DaDuyet',   120000, 'KH001', 'S06', 'KG002');
INSERT INTO DATSAN (MaDS, NgayDat, TrangThai, TongTienTamTinh, MaKH, MaSan, MaKG) VALUES
    ('DS005', DATE '2025-04-14', 'DaDuyet',   135000, 'KH003', 'S03', 'KG005');
INSERT INTO DATSAN (MaDS, NgayDat, TrangThai, TongTienTamTinh, MaKH, MaSan, MaKG) VALUES
    ('DS006', DATE '2025-04-15', 'ChoDuyet',  150000, 'KH005', 'S01', 'KG002');
INSERT INTO DATSAN (MaDS, NgayDat, TrangThai, TongTienTamTinh, MaKH, MaSan, MaKG) VALUES
    ('DS007', DATE '2025-04-15', 'ChoDuyet',  180000, 'KH007', 'S02', 'KG004');
INSERT INTO DATSAN (MaDS, NgayDat, TrangThai, TongTienTamTinh, MaKH, MaSan, MaKG) VALUES
    ('DS008', DATE '2025-04-10', 'DaHuy',      60000, 'KH008', 'S03', 'KG001');
INSERT INTO DATSAN (MaDS, NgayDat, TrangThai, TongTienTamTinh, MaKH, MaSan, MaKG) VALUES
    ('DS009', DATE '2025-04-16', 'HoanThanh', 300000, 'KH004', 'S05', 'KG009');
INSERT INTO DATSAN (MaDS, NgayDat, TrangThai, TongTienTamTinh, MaKH, MaSan, MaKG) VALUES
    ('DS010', DATE '2025-04-17', 'HoanThanh', 120000, 'KH002', 'S01', 'KG003');

-- -------------------------------------------------------
-- 3.11 HOADON
-- -------------------------------------------------------
INSERT INTO HOADON (MaHoaDon, TongTienDV, SoTienGG, ThanhTien, Ghichu, MaDS) VALUES
    ('HD001', 120000,  0,     120000, NULL,              'DS001');
INSERT INTO HOADON (MaHoaDon, TongTienDV, SoTienGG, ThanhTien, Ghichu, MaDS) VALUES
    ('HD002', 210000,  30000, 180000, 'Áp dụng KM001',   'DS002');
INSERT INTO HOADON (MaHoaDon, TongTienDV, SoTienGG, ThanhTien, Ghichu, MaDS) VALUES
    ('HD003', 295000,  70000, 225000, 'KH BachKim KM002','DS003');
INSERT INTO HOADON (MaHoaDon, TongTienDV, SoTienGG, ThanhTien, Ghichu, MaDS) VALUES
    ('HD004', 120000,  0,     120000, NULL,              'DS004');
INSERT INTO HOADON (MaHoaDon, TongTienDV, SoTienGG, ThanhTien, Ghichu, MaDS) VALUES
    ('HD005', 135000,  0,     135000, NULL,              'DS005');
INSERT INTO HOADON (MaHoaDon, TongTienDV, SoTienGG, ThanhTien, Ghichu, MaDS) VALUES
    ('HD009', 400000, 100000, 300000, 'KH VIP KM002',    'DS009');
INSERT INTO HOADON (MaHoaDon, TongTienDV, SoTienGG, ThanhTien, Ghichu, MaDS) VALUES
    ('HD010', 120000,  0,     120000, NULL,              'DS010');

-- -------------------------------------------------------
-- 3.12 THANHTOAN
-- -------------------------------------------------------
INSERT INTO THANHTOAN (MaTT, PTTT, ThoiGianTT, TrangThai, MaHoaDon) VALUES
    ('TT001', 'TienMat',     TO_TIMESTAMP('2025-04-10 08:30:00','YYYY-MM-DD HH24:MI:SS'), 'ThanhCong', 'HD001');
INSERT INTO THANHTOAN (MaTT, PTTT, ThoiGianTT, TrangThai, MaHoaDon) VALUES
    ('TT002', 'ChuyenKhoan', TO_TIMESTAMP('2025-04-11 09:15:00','YYYY-MM-DD HH24:MI:SS'), 'ThanhCong', 'HD002');
INSERT INTO THANHTOAN (MaTT, PTTT, ThoiGianTT, TrangThai, MaHoaDon) VALUES
    ('TT003', 'Vi',          TO_TIMESTAMP('2025-04-12 10:00:00','YYYY-MM-DD HH24:MI:SS'), 'ThanhCong', 'HD003');
INSERT INTO THANHTOAN (MaTT, PTTT, ThoiGianTT, TrangThai, MaHoaDon) VALUES
    ('TT004', 'TheNganHang', TO_TIMESTAMP('2025-04-13 14:00:00','YYYY-MM-DD HH24:MI:SS'), 'DangXuLy',  'HD004');
INSERT INTO THANHTOAN (MaTT, PTTT, ThoiGianTT, TrangThai, MaHoaDon) VALUES
    ('TT005', 'TienMat',     TO_TIMESTAMP('2025-04-14 16:30:00','YYYY-MM-DD HH24:MI:SS'), 'ThanhCong', 'HD005');
INSERT INTO THANHTOAN (MaTT, PTTT, ThoiGianTT, TrangThai, MaHoaDon) VALUES
    ('TT009', 'Vi',          TO_TIMESTAMP('2025-04-16 20:00:00','YYYY-MM-DD HH24:MI:SS'), 'ThanhCong', 'HD009');
INSERT INTO THANHTOAN (MaTT, PTTT, ThoiGianTT, TrangThai, MaHoaDon) VALUES
    ('TT010', 'ChuyenKhoan', TO_TIMESTAMP('2025-04-17 07:30:00','YYYY-MM-DD HH24:MI:SS'), 'ThanhCong', 'HD010');

-- -------------------------------------------------------
-- 3.13 CTDV (Chi Tiết Dịch Vụ)
-- -------------------------------------------------------
INSERT INTO CTDV (MaCTDV, SoLuong, ThanhTien, MaDS, MaDV) VALUES
    ('CTDV001', 2, 60000,  'DS001', 'DV001');  -- 2 vợt thuê
INSERT INTO CTDV (MaCTDV, SoLuong, ThanhTien, MaDS, MaDV) VALUES
    ('CTDV002', 3, 45000,  'DS001', 'DV004');  -- 3 chai nước
INSERT INTO CTDV (MaCTDV, SoLuong, ThanhTien, MaDS, MaDV) VALUES
    ('CTDV003', 1, 120000, 'DS002', 'DV002');  -- 1 hộp cầu
INSERT INTO CTDV (MaCTDV, SoLuong, ThanhTien, MaDS, MaDV) VALUES
    ('CTDV004', 2, 40000,  'DS002', 'DV005');  -- 2 khăn
INSERT INTO CTDV (MaCTDV, SoLuong, ThanhTien, MaDS, MaDV) VALUES
    ('CTDV005', 1, 150000, 'DS003', 'DV001');  -- căng vợt
INSERT INTO CTDV (MaCTDV, SoLuong, ThanhTien, MaDS, MaDV) VALUES
    ('CTDV006', 2, 60000,  'DS003', 'DV004');  -- 2 chai nước
INSERT INTO CTDV (MaCTDV, SoLuong, ThanhTien, MaDS, MaDV) VALUES
    ('CTDV007', 1, 80000,  'DS009', 'DV001');  -- thay dây vợt
INSERT INTO CTDV (MaCTDV, SoLuong, ThanhTien, MaDS, MaDV) VALUES
    ('CTDV008', 4, 60000,  'DS009', 'DV004');  -- 4 chai nước

-- -------------------------------------------------------
-- 3.14 SUDUNGGG
-- -------------------------------------------------------
INSERT INTO SuDungGG (MaCoupon, NgaySD, MaKH, MaKM) VALUES
    ('SDKM001', TO_TIMESTAMP('2025-01-15 09:00:00','YYYY-MM-DD HH24:MI:SS'), 'KH001', 'KM001');
INSERT INTO SuDungGG (MaCoupon, NgaySD, MaKH, MaKM) VALUES
    ('SDKM002', TO_TIMESTAMP('2025-01-20 10:00:00','YYYY-MM-DD HH24:MI:SS'), 'KH002', 'KM001');
INSERT INTO SuDungGG (MaCoupon, NgaySD, MaKH, MaKM) VALUES
    ('SDKM003', TO_TIMESTAMP('2025-04-12 10:00:00','YYYY-MM-DD HH24:MI:SS'), 'KH004', 'KM002');
INSERT INTO SuDungGG (MaCoupon, NgaySD, MaKH, MaKM) VALUES
    ('SDKM004', TO_TIMESTAMP('2025-04-16 20:00:00','YYYY-MM-DD HH24:MI:SS'), 'KH004', 'KM005');

-- -------------------------------------------------------
-- 3.15 DANHGIASAN
-- -------------------------------------------------------
INSERT INTO DANHGIASAN (MaDanhGia, DiemDG, NhanXet, ThoiDiemDanhGia, MaKH, MaSan) VALUES
    ('DG001', 5, 'Sân rất tốt, sàn gỗ êm, ánh sáng tốt, sẽ quay lại.',
     TO_TIMESTAMP('2025-04-10 09:00:00','YYYY-MM-DD HH24:MI:SS'), 'KH001', 'S01');
INSERT INTO DANHGIASAN (MaDanhGia, DiemDG, NhanXet, ThoiDiemDanhGia, MaKH, MaSan) VALUES
    ('DG002', 4, 'Sân đôi khá rộng, thoải mái chơi, nhân viên thân thiện.',
     TO_TIMESTAMP('2025-04-11 10:30:00','YYYY-MM-DD HH24:MI:SS'), 'KH002', 'S02');
INSERT INTO DANHGIASAN (MaDanhGia, DiemDG, NhanXet, ThoiDiemDanhGia, MaKH, MaSan) VALUES
    ('DG003', 5, 'Sân VIP xứng đáng với giá tiền, cao su rất bám.',
     TO_TIMESTAMP('2025-04-12 11:00:00','YYYY-MM-DD HH24:MI:SS'), 'KH004', 'S05');
INSERT INTO DANHGIASAN (MaDanhGia, DiemDG, NhanXet, ThoiDiemDanhGia, MaKH, MaSan) VALUES
    ('DG004', 3, 'Sân ngoài trời ổn nhưng hơi nóng vào buổi trưa.',
     TO_TIMESTAMP('2025-04-14 17:00:00','YYYY-MM-DD HH24:MI:SS'), 'KH003', 'S03');
INSERT INTO DANHGIASAN (MaDanhGia, DiemDG, NhanXet, ThoiDiemDanhGia, MaKH, MaSan) VALUES
    ('DG005', 4, 'Sân cao su trong nhà mát mẻ, dịch vụ nhanh nhẹn.',
     TO_TIMESTAMP('2025-04-13 15:00:00','YYYY-MM-DD HH24:MI:SS'), 'KH001', 'S06');



--FUNCTION f_SanTrong
create or replace function f_SanTrong
(f_MaSan in SAN.MaSan%type, f_NgayDat in DATSAN.NgayDat%type, f_MaKG in DATSAN.MaKG%type)
return varchar2
is
    f_dem number;
begin
    select count(*) into f_dem
    from SAN s join DATSAN ds on s.MaSan=ds.MaSan
    where s.MaSan = f_MaSan and ds.NgayDat = f_NgayDat and ds.MaKG = f_MaKG and ds.TrangThai in ('DaDuyet','ChoDuyet');

    if f_dem = 0 then return 'Sân Trống';
    else return 'Sân Bận';
    end if;
end;

--f_DangNhap
create or replace function f_DangNhap
(f_username in TAIKHOAN.Username%type, f_pass in TAIKHOAN.Password%type)
return varchar2
is
    f_dem number;
begin
    select count(*) into f_dem
    from TAIKHOAN
    where Username = f_username and Password = f_pass;
    
    if f_dem > 0 then return 'Đăng nhập thành công!';
    else return 'Đăng nhập không thành công!';
    end if;
end
;

--f_DangKy
create or replace function f_DangKy
(f_Hoten in KHACHHANG.HoTen%type, f_sdt in KHACHHANG.SDT%type, f_email in KHACHHANG.Email%type, f_username in TAIKHOAN.Username%type,
f_pass in TAIKHOAN.Password%type)
return varchar2
is
    f_dem number;
begin
    select count(*) into f_dem
    from KHACHHANG kh join TAIKHOAN tk on kh.Ma_TK=tk.Ma_TK
    where kh.HoTen = f_Hoten and kh.SDT = f_sdt and kh.Email = f_email
    and tk.Username = f_username
    and tk.Password  = f_pass;
    
    if f_dem = 0 then return 'Đăng ký thành công!';
    else return 'Đăng ký không thành công!';
    end if;
end;

--f_TinhTienNgThuong
create or replace function f_TinhTienNgThuong 
(f_MaDS in varchar2) 
return number
is
    f_TongTien number := 0; f_GiaThue number; f_SoGio number; f_PhanTram number := 0; f_GtriMax number; f_GiamGia number := 0; f_MaKM varchar2(20); 
    f_MaKH varchar2(20);
begin
    select s.GiaThueTheoGio, 
           extract(hour from (kg.GioKT - kg.GioBD)) + extract(minute from (kg.GioKT - kg.GioBD))/60,
           ds.MaKH
    into f_GiaThue, f_SoGio, f_MaKH
    from DATSAN ds
    join SAN s on ds.MaSan = s.MaSan
    join KHUNGGIO kg on ds.MaKG = kg.MaKG
    where ds.MaDS = f_MaDS;
    f_TongTien := f_GiaThue * f_SoGio;
    begin
        select MaKM into f_MaKM 
        from SuDungGG 
        where MaKH = f_MaKH ;
    end;
    if f_MaKM is not null then
        select PhanTramGG, GTriToiDa 
        into f_PhanTram, f_GtriMax
        from KHUYENMAI 
        where MaKM = f_MaKM;

        f_GiamGia := f_TongTien * (f_PhanTram / 100);

        if f_GtriMax is not null and f_GiamGia > f_GtriMax then
            f_GiamGia := f_GtriMax;
        end if;
    end if;
    return f_TongTien - f_GiamGia;
end;
/

--f_HuySan
create or replace function f_HuySan (f_MaDS in varchar2)
return varchar2
is
    f_PTTT varchar2(30); f_TrangThaiTT varchar2(20); f_CheckIn varchar2(20); f_NgayTT date; f_HienTai date := sysdate; f_KhoangCach number;
begin
    select tt.PTTT, tt.TrangThai, tt.ThoiGianTT, ds.TrangThai
    into f_PTTT, f_TrangThaiTT, f_NgayTT, f_CheckIn
    from DATSAN ds left join HOADON hd on ds.MaDS = hd.MaDS
    left join THANHTOAN tt on hd.MaHoaDon = tt.MaHoaDon
    where ds.MaDS = f_MaDS;

    if f_CheckIn = 'CheckedIn' then return 'Khách đã check-in vào sân!'; end if;

    if f_PTTT = 'ChuyenKhoan' and f_TrangThaiTT = 'ThanhCong' then return 'Thanh toán CK không được hoàn tiền!'; end if;

    if f_NgayTT is not null then f_KhoangCach := f_HienTai - f_NgayTT;
    if f_KhoangCach > 3 then return 'Không được hủy sân sau 3 ngày kể từ ngày thanh toán!';
    end if; end if;
    return 'Hủy thành công!';
end;
/

--f_TinhTienNgLe
create or replace function f_TinhTienNgLe (f_MaDS in varchar2) 
return number 
is
    f_TienGoc number; f_GiaPhuThu number := 0; f_NgayDat date;
begin
    f_TienGoc := f_TinhTienNgThuong(f_MaDS);
    select NgayDat into f_NgayDat 
    from DATSAN 
    where MaDS = f_MaDS;

    begin
        select GiaPhuThu into f_GiaPhuThu
        from NGAYLE
        where trunc(NgayCuThe) = trunc(f_NgayDat);
    exception
        when no_data_found then f_GiaPhuThu := 0;
    end;
    return f_TienGoc + f_GiaPhuThu;
end;
/

--f_ThongKeDoanhThu
create or replace function f_ThongKeDoanhThu (f_NgayTK in date) 
return number
is
    f_TongDoanhThu number := 0;
begin
    select sum(hd.TongTienDV) 
    into f_TongDoanhThu
    from HOADON hd join DATSAN ds on hd.MaDS=ds.MaDS
    where trunc(ds.NgayDat) = trunc(f_NgayTK)
    and ds.TrangThai = 'ThanhCong';

    return NVL(f_TongDoanhThu, 0);
end;
/
    
--f_TinhTienDV
create or replace function f_TinhTienDV
(f_MaDv in varchar2, f_SoLuong in number)
return number
is
    f_GiaBan DICHVU.GiaBan%type;
    f_Tong number :=0;
begin
    select GiaBan into f_GiaBan
    from DICHVU
    where MaDV = f_MaDv;

    f_Tong := f_SoLuong * f_GiaBan;
    return f_Tong;
end;
/
    


