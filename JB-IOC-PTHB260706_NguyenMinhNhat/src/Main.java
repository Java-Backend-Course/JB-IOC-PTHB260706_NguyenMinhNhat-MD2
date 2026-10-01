import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Main {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/db_cinema_booking";
    private static final String DB_USER = "postgres";
    private static final String DB_PASS = "12345678";

    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // III
    public static void main(String[] args) {
        while(true) {
            System.out.println("\n========================================");
            System.out.println("    CINEMA TICKET BOOKING MANAGEMENT    ");
            System.out.println("========================================");
            System.out.println("1. Danh sách tất cả phiếu đặt vé");
            System.out.println("2. Thêm mới phiếu đặt vé");
            System.out.println("3. Cập nhật thông tin phiếu đặt vé");
            System.out.println("4. Xóa phiếu đặt vé");
            System.out.println("5. Tìm kiếm phiếu đặt vé theo tên khách hàng");
            System.out.println("6. Tìm kiếm phiếu đặt vé theo tên phim");
            System.out.println("7. Thoát");
            System.out.print("Chọn chức năng: ");

            int choice = -1;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            }
            catch (NumberFormatException e) {
                System.out.println("Vui lòng nhập từ 1-7!");
                continue;
            }
            switch (choice) {
                case 1: showAllBookings();
                break;
                case 2: addNewBooking();
                break;
                case 3: updateBooking();
                break;
                case 4: deleteBooking();
                break;
                case 5: searchByCustomer();
                break;
                case 6: searchByMovie();
                break;
                case 7: System.exit(0);
                default: System.out.println("Không hợp lệ");
            }
        }
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    // 1
    private static void showAllBookings() {
        String sql = "SELECT * FROM get_all_bookings()";
        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            printResultSet(rs);
        }
        catch (SQLException e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
    }

    // 2
    private static void addNewBooking() {
        System.out.println("\nTHÊM PHIẾU ĐẶT VÉ");
        System.out.print("Nhập tên phim: ");
        String movieTitle = scanner.nextLine();
        System.out.print("Nhập tên khách hàng: ");
        String customerName = scanner.nextLine();
        System.out.print("Suất chiếu: ");
        LocalDateTime showTime = LocalDateTime.parse(scanner.nextLine(), formatter);
        System.out.print("Ngày đặt vé: ");
        LocalDateTime bookingDate = LocalDateTime.parse(scanner.nextLine(), formatter);
        System.out.print("Số lượng ghế: ");
        int seatQuantity = Integer.parseInt(scanner.nextLine());
        System.out.print("Trạng thái: ");
        String status = scanner.nextLine();

        String sql = "CALL add_booking(?, ?, ?, ?, ?, ?)";
        try (Connection con = getConnection();
             CallableStatement stmt = con.prepareCall(sql)) {
            stmt.setString(1, movieTitle);
            stmt.setString(2, customerName);
            stmt.setTimestamp(3, Timestamp.valueOf(showTime));
            stmt.setTimestamp(4, Timestamp.valueOf(bookingDate));
            stmt.setInt(5, seatQuantity);
            stmt.setString(6, status);
            stmt.execute();
            System.out.println("Thêm phiếu thành công!");
        }
        catch (Exception e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
    }

    // 3
    private static void updateBooking() {
        System.out.println("CẬP NHẬT PHIẾU ĐẶT VÉ");
        System.out.print("Nhập mã: ");
        int bookingId = Integer.parseInt(scanner.nextLine());
        System.out.print("Nhập tên phim mới: ");
        String movieTitle = scanner.nextLine();
        System.out.print("Nhập tên khách mới: ");
        String customerName = scanner.nextLine();
        System.out.print("Nhập suất chiếu mới: ");
        LocalDateTime showTime = LocalDateTime.parse(scanner.nextLine(), formatter);
        System.out.print("Nhập ngày đặt mới: ");
        LocalDateTime bookingDate = LocalDateTime.parse(scanner.nextLine(), formatter);
        System.out.print("Nhập số lượng ghế mới: ");
        int seatQuantity = Integer.parseInt(scanner.nextLine());
        System.out.print("Trạng thái mới: ");
        String status = scanner.nextLine();

        String sql = "CALL update_booking(?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = getConnection();
             CallableStatement stmt = con.prepareCall(sql)) {
            stmt.setInt(1, bookingId);
            stmt.setString(2, movieTitle);
            stmt.setString(3, customerName);
            stmt.setTimestamp(4, Timestamp.valueOf(showTime));
            stmt.setTimestamp(5, Timestamp.valueOf(bookingDate));
            stmt.setInt(6, seatQuantity);
            stmt.setString(7, status);
            stmt.execute();
            System.out.println("Cập nhật phiếu thành công!");
        }
        catch (Exception e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
    }

    // 4
    private static void deleteBooking() {
        System.out.println("XÓA PHIẾU ĐẶT VÉ");
        System.out.print("Nhập mã cần xóa: ");
        int bookingId = Integer.parseInt(scanner.nextLine());
        String sql = "CALL delete_booking(?)";
        try (Connection con = getConnection();
             CallableStatement stmt = con.prepareCall(sql)) {
            stmt.setInt(1, bookingId);
            stmt.execute();
            System.out.println("Xóa thành công " + bookingId);
        }
        catch (SQLException e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
    }

    // 5
    private static void searchByCustomer() {
        System.out.println("TÌM KIẾM THEO TÊN KHÁCH HÀNG");
        System.out.print("Nhập tên khách hàng: ");
        String customerName = scanner.nextLine();
        String sql = "SELECT * FROM get_bookings_by_customer_name(?)";
        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, customerName);
            try (ResultSet rs = stmt.executeQuery()) {
                printResultSet(rs);
            }
        }
        catch (SQLException e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
    }

    // 6
    private static void searchByMovie() {
        System.out.println("TÌM KIẾM THEO TÊN PHIM");
        System.out.print("Nhập tên phim hoặc từ khóa: ");
        String movieTitle = scanner.nextLine();
        String sql = "SELECT * FROM search_bookings_by_movie_name(?)";
        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, movieTitle);
            try (ResultSet rs = stmt.executeQuery()) {
                printResultSet(rs);
            }
        }
        catch (SQLException e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
    }

    private static void printResultSet(ResultSet rs) throws SQLException {
        int count = 0;
        while (rs.next()) {
            System.out.printf("ID: %d, Phim: %s, KH: %s, Suất: %s, Đặt: %s, Ghế: %d, [%s]\n",
                    rs.getInt(1), rs.getString(2), rs.getString(3), rs.getTimestamp(4), rs.getTimestamp(5), rs.getInt(6), rs.getString(7));
            count++;
        }
        if (count == 0) System.out.println("Không có dữ liệu!");
    }
}