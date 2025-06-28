import java.util.Scanner; // Digunakan untuk mengambil input dari pengguna lewat terminal
import java.io.FileWriter; // Digunakan untuk menulis data ke dalam file
import java.io.IOException; // Digunakan untuk menangani exception saat file gagal ditulis

// Interface Pegawai berisi kontrak yang harus diimplementasikan oleh semua tipe pegawai
interface Pegawai {
    double hitungGaji();       // Method untuk menghitung total gaji pegawai
    String getNama();          // Mengambil nama pegawai
    String getJenis();         // Mengambil jenis pegawai (Tetap/Kontrak)
    double getGajiDasar();     // Mengambil nilai gaji pokok/honor
    double getTunjangan();     // Mengambil tunjangan (kalau ada)
    int getJamKerja();         // Mengambil jumlah jam kerja
}

// Kelas untuk pegawai tetap yang mengimplementasikan interface Pegawai
class PegawaiTetap implements Pegawai {
    private String nama;           // Nama pegawai
    private double gajiPokok;      // Gaji pokok bulanan
    private boolean sudahMenikah;  // Status pernikahan

    // Konstruktor untuk inisialisasi objek
    public PegawaiTetap(String nama, double gajiPokok, boolean sudahMenikah) {
        this.nama = nama;
        this.gajiPokok = gajiPokok;
        this.sudahMenikah = sudahMenikah;
    }

    // Menghitung gaji akhir: gaji pokok + tunjangan - pajak (5%)
    public double hitungGaji() {
        return gajiPokok + getTunjangan() - 0.05 * gajiPokok;
    }

    // Tunjangan 10% jika menikah, jika tidak 8%
    public double getTunjangan() {
        return sudahMenikah ? 0.10 * gajiPokok : 0.08 * gajiPokok;
    }

    public double getGajiDasar() {
        return gajiPokok;
    }

    // Jam kerja default pegawai tetap = 160 jam
    public int getJamKerja() {
        return 160;
    }

    public String getNama() {
        return nama;
    }

    public String getJenis() {
        return "Tetap";
    }
}

// Kelas untuk pegawai kontrak yang mengimplementasikan interface Pegawai
class PegawaiKontrak implements Pegawai {
    private String nama;
    private double honorPerJam;
    private int jumlahJam;

    // Konstruktor pegawai kontrak
    public PegawaiKontrak(String nama, double honorPerJam, int jumlahJam) {
        this.nama = nama;
        this.honorPerJam = honorPerJam;
        this.jumlahJam = jumlahJam;
    }

    // Gaji kotor - potongan 3%
    public double hitungGaji() {
        return getGajiDasar() - 0.03 * getGajiDasar();
    }

    // Pegawai kontrak tidak mendapat tunjangan
    public double getTunjangan() {
        return 0;
    }

    // Menghitung gaji dasar = honor per jam * jumlah jam kerja
    public double getGajiDasar() {
        return honorPerJam * jumlahJam;
    }

    public int getJamKerja() {
        return jumlahJam;
    }

    public String getNama() {
        return nama;
    }

    public String getJenis() {
        return "Kontrak";
    }
}

// Kelas utama untuk menjalankan program
public class GajiPegawaiTerminal {

    // Method bantu untuk memformat angka: jika angka bulat, hilangkan .0
    private static String formatNumber(double number) {
        if (number == (long) number) {
            return String.format("%d", (long) number); // misal 950.0 → 950
        } else {
            return String.format("%.2f", number); // misal 950.55 → 950.55
        }
    }

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in); // Buat objek Scanner untuk input
        Pegawai pegawai = null; // Variabel pegawai untuk menyimpan objek yang dibuat

        // Input nama pegawai
        System.out.print("Masukkan nama pegawai: ");
        String nama = input.nextLine();

        // Input jenis pegawai
        System.out.print("Masukkan jenis pegawai (Tetap/Kontrak): ");
        String jenis = input.nextLine().trim().toLowerCase();

        // Jika pegawai tetap, minta input gaji pokok dan status menikah
        if (jenis.equals("tetap")) {
            System.out.print("Masukkan gaji pokok: ");
            double gajiPokok = input.nextDouble();
            input.nextLine(); // flush newline

            System.out.print("Apakah sudah menikah? (ya/tidak): ");
            boolean menikah = input.nextLine().trim().equalsIgnoreCase("ya");

            // Buat objek PegawaiTetap
            pegawai = new PegawaiTetap(nama, gajiPokok, menikah);
        } 
        // Jika pegawai kontrak, minta input honor dan jam kerja
        else if (jenis.equals("kontrak")) {
            System.out.print("Masukkan honor per jam: ");
            double honor = input.nextDouble();
            System.out.print("Masukkan jumlah jam kerja: ");
            int jam = input.nextInt();

            // Buat objek PegawaiKontrak
            pegawai = new PegawaiKontrak(nama, honor, jam);
        } else {
            // Jika jenis pegawai tidak valid
            System.out.println("Jenis pegawai tidak dikenal.");
            input.close();
            return;
        }

        // Hitung total gaji
        double gajiTotal = pegawai.hitungGaji();

        // Tampilkan ke terminal
        System.out.println("Gaji untuk " + pegawai.getNama() + " adalah: " + formatNumber(gajiTotal));

        // Simpan ke file database.txt
        try (FileWriter writer = new FileWriter("database.txt", true)) {
            writer.write("Nama: " + pegawai.getNama() +
                    " | Jenis: " + pegawai.getJenis() +
                    " | Gaji: " + formatNumber(pegawai.getGajiDasar()) +
                    " | Tunjangan: " + formatNumber(pegawai.getTunjangan()) +
                    " | JamKerja: " + pegawai.getJamKerja() +
                    " | TotalGaji: " + formatNumber(gajiTotal) + "\n");

            System.out.println("Data berhasil ditambahkan ke database.txt");
        } catch (IOException e) {
            // Jika gagal menyimpan
            System.out.println("Gagal menyimpan ke file: " + e.getMessage());
        }

        // Tutup Scanner
        input.close();
    }
}
