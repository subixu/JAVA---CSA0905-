import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.util.*;
import java.util.List;

public class Main {

    static final Color NAV = new Color(17, 25, 39);
    static final Color NAV2 = new Color(28, 39, 58);
    static final Color BG = new Color(246, 248, 252);
    static final Color TEXT = new Color(27, 36, 49);
    static final Color MUTED = new Color(105, 116, 132);
    static final Color GREEN = new Color(20, 150, 92);
    static final Color BLUE = new Color(48, 102, 220);
    static final Color ORANGE = new Color(232, 143, 45);
    static final Color RED = new Color(211, 70, 70);
    static final Color PURPLE = new Color(128, 82, 190);
    static final double TARIFF = 8.50;
    static final Path STORE = Paths.get("data", "evhub.properties");

    static JFrame frame;
    static JPanel body;
    static JLabel pageTitle;
    static JLabel pageSub;
    static String currentPage = "Dashboard";

    static final List<User> users = new ArrayList<>();
    static final List<Vehicle> vehicles = new ArrayList<>();
    static final List<Station> stations = new ArrayList<>();
    static final List<Reservation> reservations = new ArrayList<>();
    static final List<Session> sessions = new ArrayList<>();
    static final List<Payment> payments = new ArrayList<>();

    static int seq = 900;

    static class User {
        int id;
        String name;
        String email;

        User(int id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }
    }

    static class Vehicle {
        int id;
        int userId;
        String reg;
        String model;

        Vehicle(int id, int userId, String reg, String model) {
            this.id = id;
            this.userId = userId;
            this.reg = reg;
            this.model = model;
        }
    }

    static class Point {
        int id;
        String status;
        String type;

        Point(int id, String status, String type) {
            this.id = id;
            this.status = status;
            this.type = type;
        }
    }

    static class Station {
        int id;
        String name;
        String location;
        List<Point> points = new ArrayList<>();

        Station(int id, String name, String location) {
            this.id = id;
            this.name = name;
            this.location = location;
        }
    }

    static class Reservation {
        int id;
        int vehicleId;
        int pointId;
        String slot;
        String status;

        Reservation(int id, int vehicleId, int pointId, String slot, String status) {
            this.id = id;
            this.vehicleId = vehicleId;
            this.pointId = pointId;
            this.slot = slot;
            this.status = status;
        }
    }

    static class Session {
        int id;
        int vehicleId;
        int pointId;
        double kwh;
        double hours;
        double cost;
        String status;

        Session(int id, int vehicleId, int pointId, double kwh, double hours, String status) {
            this.id = id;
            this.vehicleId = vehicleId;
            this.pointId = pointId;
            this.kwh = kwh;
            this.hours = hours;
            this.cost = kwh * TARIFF;
            this.status = status;
        }
    }

    static class Payment {
        int id;
        int sessionId;
        double amount;
        String status;

        Payment(int id, int sessionId, double amount, String status) {
            this.id = id;
            this.sessionId = sessionId;
            this.amount = amount;
            this.status = status;
        }
    }

    public static void main(String[] args) {
        load();
        SwingUtilities.invokeLater(Main::build);
    }

    static void build() {

        try {
            UIManager.setLookAndFeel(
                    UIManager.getCrossPlatformLookAndFeelClassName()
            );
        } catch (Exception ignored) {
        }

        frame = new JFrame(
                "EV ChargeHub | Smart Campus Charging"
        );

        frame.setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        frame.setSize(1250, 790);
        frame.setMinimumSize(
                new Dimension(1050, 680)
        );

        frame.setLocationRelativeTo(null);

        JPanel root = new JPanel(
                new BorderLayout()
        );

        root.setBackground(BG);

        root.add(
                sidebar(),
                BorderLayout.WEST
        );

        JPanel main = new JPanel(
                new BorderLayout()
        );

        main.setBackground(BG);

        main.add(
                topbar(),
                BorderLayout.NORTH
        );

        body = new JPanel(
                new BorderLayout()
        );

        body.setBackground(BG);

        body.setBorder(
                new EmptyBorder(
                        20,
                        26,
                        26,
                        26
                )
        );

        main.add(
                body,
                BorderLayout.CENTER
        );

        root.add(
                main,
                BorderLayout.CENTER
        );

        frame.setContentPane(root);
        frame.setVisible(true);

        show("Dashboard");
    }

    static JPanel sidebar() {

        JPanel p = new JPanel(
                new BorderLayout()
        );

        p.setPreferredSize(
                new Dimension(225, 790)
        );

        p.setBackground(NAV);

        JPanel upper = new JPanel();

        upper.setOpaque(false);

        upper.setLayout(
                new BoxLayout(
                        upper,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel logo = new JLabel(
                "<html>" +
                "<div style='font-size:20px'>" +
                "<b>⚡ EV CHARGEHUB</b>" +
                "</div>" +
                "<div style='font-size:10px;color:#9aa7bb'>" +
                "SMART CAMPUS • CONTROL CENTER" +
                "</div>" +
                "</html>"
        );

        logo.setForeground(Color.WHITE);

        logo.setBorder(
                new EmptyBorder(
                        26,
                        20,
                        28,
                        10
                )
        );

        upper.add(logo);

        String[][] items = {
                {"⌂", "Dashboard"},
                {"◉", "Stations"},
                {"▣", "Reservations"},
                {"⚡", "Charging Sessions"},
                {"◈", "Vehicles & Users"},
                {"₹", "Payments"},
                {"▤", "Analytics & Reports"}
        };

        for (String[] item : items) {

            JButton b = navButton(
                    item[0] + "   " + item[1]
            );

            b.addActionListener(e -> {

                String target;

                if (item[1].equals("Vehicles & Users")) {
                    target = "Users";
                } else if (item[1].equals("Analytics & Reports")) {
                    target = "Reports";
                } else {
                    target = item[1];
                }

                show(target);
            });

            upper.add(b);
        }

        p.add(
                upper,
                BorderLayout.NORTH
        );

        JPanel foot = new JPanel(
                new BorderLayout()
        );

        foot.setBackground(NAV2);

        foot.setBorder(
                new EmptyBorder(
                        14,
                        18,
                        16,
                        18
                )
        );

        JLabel x = new JLabel(
                "<html>" +
                "<b>● System Online</b>" +
                "<br>" +
                "<span style='color:#a9b5c7'>" +
                "Local data mode" +
                "</span>" +
                "</html>"
        );

        x.setForeground(
                new Color(220, 230, 240)
        );

        foot.add(x);

        p.add(
                foot,
                BorderLayout.SOUTH
        );

        return p;
    }

    static JButton navButton(String text) {

        JButton b = new JButton(text);

        b.setHorizontalAlignment(
                SwingConstants.LEFT
        );

        b.setForeground(
                new Color(215, 223, 235)
        );

        b.setBackground(NAV);
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);

        b.setBorder(
                new EmptyBorder(
                        12,
                        20,
                        12,
                        10
                )
        );

        b.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        13
                )
        );

        b.setMaximumSize(
                new Dimension(225, 48)
        );

        return b;
    }

    static JPanel topbar() {

        JPanel p = new JPanel(
                new BorderLayout()
        );

        p.setBackground(Color.WHITE);

        p.setBorder(
                new CompoundBorder(
                        new MatteBorder(
                                0,
                                0,
                                1,
                                0,
                                new Color(226, 230, 236)
                        ),
                        new EmptyBorder(
                                15,
                                26,
                                14,
                                26
                        )
                )
        );

        JPanel left = new JPanel(
                new GridLayout(2, 1)
        );

        left.setOpaque(false);

        pageTitle = new JLabel(
                "Dashboard"
        );

        pageTitle.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        21
                )
        );

        pageTitle.setForeground(TEXT);

        pageSub = new JLabel(
                "Campus charging overview"
        );

        pageSub.setForeground(MUTED);

        pageSub.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        12
                )
        );

        left.add(pageTitle);
        left.add(pageSub);

        JPanel right = new JPanel(
                new FlowLayout(
                        FlowLayout.RIGHT,
                        10,
                        0
                )
        );

        right.setOpaque(false);

        JLabel campus = new JLabel(
                "● MAIN CAMPUS"
        );

        campus.setForeground(GREEN);

        campus.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        12
                )
        );

        JButton refresh =
                new JButton("↻ Refresh");

        styleButton(
                refresh,
                false
        );

        refresh.addActionListener(
                e -> show(currentPage)
        );

        right.add(campus);
        right.add(refresh);

        p.add(
                left,
                BorderLayout.WEST
        );

        p.add(
                right,
                BorderLayout.EAST
        );

        return p;
    }

    static void show(String page) {

        currentPage = page;

        body.removeAll();

        if (page.equals("Dashboard")) {

            pageTitle.setText("Dashboard");

            pageSub.setText(
                    "Live campus charging intelligence"
            );

            dashboard();

        } else if (page.equals("Stations")) {

            pageTitle.setText(
                    "Charging Stations"
            );

            pageSub.setText(
                    "Infrastructure, point status and availability"
            );

            stationsPage();

        } else if (page.equals("Reservations")) {

            pageTitle.setText(
                    "Reservations"
            );

            pageSub.setText(
                    "Schedule charging slots without conflicts"
            );

            reservationsPage();

        } else if (page.equals("Charging Sessions")) {

            pageTitle.setText(
                    "Charging Sessions"
            );

            pageSub.setText(
                    "Track energy, duration and tariff-based cost"
            );

            sessionsPage();

        } else if (page.equals("Users")) {

            pageTitle.setText(
                    "Vehicles & Users"
            );

            pageSub.setText(
                    "Campus account and EV registration"
            );

            usersPage();

        } else if (page.equals("Payments")) {

            pageTitle.setText(
                    "Payments"
            );

            pageSub.setText(
                    "Charging transactions and payment status"
            );

            paymentsPage();

        } else {

            pageTitle.setText(
                    "Analytics & Reports"
            );

            pageSub.setText(
                    "Operational insights from charging activity"
            );

            reportsPage();
        }

        body.revalidate();
        body.repaint();
    }

    static JLabel heading(
            String title,
            String subtitle) {

        JLabel l = new JLabel(
                "<html>" +
                "<b style='font-size:16px'>" +
                title +
                "</b>" +
                "<br>" +
                "<span style='color:#697487;font-size:11px'>" +
                subtitle +
                "</span>" +
                "</html>"
        );

        l.setForeground(TEXT);

        return l;
    }

    static JPanel card(
            String label,
            String value,
            String detail,
            Color accent) {

        JPanel p = new JPanel(
                new BorderLayout(8, 6)
        );

        p.setBackground(Color.WHITE);

        p.setBorder(
                new CompoundBorder(
                        new LineBorder(
                                new Color(226, 230, 236)
                        ),
                        new EmptyBorder(
                                16,
                                17,
                                15,
                                17
                        )
                )
        );

        JPanel strip = new JPanel();

        strip.setBackground(accent);

        strip.setPreferredSize(
                new Dimension(4, 1)
        );

        JPanel txt = new JPanel(
                new GridLayout(3, 1)
        );

        txt.setOpaque(false);

        JLabel a = new JLabel(
                label.toUpperCase()
        );

        a.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        10
                )
        );

        a.setForeground(MUTED);

        JLabel b = new JLabel(value);

        b.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        25
                )
        );

        b.setForeground(TEXT);

        JLabel c = new JLabel(detail);

        c.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        11
                )
        );

        c.setForeground(MUTED);

        txt.add(a);
        txt.add(b);
        txt.add(c);

        p.add(
                strip,
                BorderLayout.WEST
        );

        p.add(
                txt,
                BorderLayout.CENTER
        );

        return p;
    }

    static void dashboard() {

        JPanel root = new JPanel(
                new BorderLayout(16, 16)
        );

        root.setOpaque(false);

        JPanel cards = new JPanel(
                new GridLayout(
                        1,
                        4,
                        14,
                        14
                )
        );

        cards.setOpaque(false);

        int total = 0;
        int available = 0;
        int occupied = 0;

        for (Station s : stations) {

            for (Point p : s.points) {

                total++;

                if (p.status.equals("Available"))
                    available++;

                if (p.status.equals("Occupied"))
                    occupied++;
            }
        }

        double energy =
                sessions.stream()
                        .mapToDouble(x -> x.kwh)
                        .sum();

        cards.add(
                card(
                        "Total Points",
                        String.valueOf(total),
                        "Across all stations",
                        BLUE
                )
        );

        cards.add(
                card(
                        "Available",
                        String.valueOf(available),
                        available + " ready to charge",
                        GREEN
                )
        );

        cards.add(
                card(
                        "Active / Occupied",
                        String.valueOf(occupied),
                        "Currently in use",
                        ORANGE
                )
        );

        cards.add(
                card(
                        "Energy Delivered",
                        String.format(
                                "%.1f kWh",
                                energy
                        ),
                        "Recorded sessions",
                        PURPLE
                )
        );

        root.add(
                cards,
                BorderLayout.NORTH
        );

        JPanel middle = new JPanel(
                new GridLayout(
                        1,
                        2,
                        16,
                        0
                )
        );

        middle.setOpaque(false);

        middle.add(
                pointPanel()
        );

        middle.add(
                quickPanel()
        );

        root.add(
                middle,
                BorderLayout.CENTER
        );

        body.add(root);
    }

    static JPanel pointPanel() {

        JPanel p = panel();

        p.setLayout(
                new BorderLayout(
                        10,
                        10
                )
        );

        p.add(
                heading(
                        "Live Point Status",
                        "A visual view of campus charging infrastructure"
                ),
                BorderLayout.NORTH
        );

        JPanel grid = new JPanel(
                new GridLayout(
                        2,
                        4,
                        10,
                        10
                )
        );

        grid.setOpaque(false);

        for (Station s : stations) {

            for (Point q : s.points) {

                grid.add(
                        pointTile(s, q)
                );
            }
        }

        p.add(
                grid,
                BorderLayout.CENTER
        );

        JPanel legend = new JPanel(
                new FlowLayout(
                        FlowLayout.LEFT,
                        14,
                        3
                )
        );

        legend.setOpaque(false);

        String[] status = {
                "Available",
                "Occupied",
                "Reserved",
                "Maintenance"
        };

        for (String x : status) {

            JLabel l = new JLabel(
                    "● " + x
            );

            l.setFont(
                    new Font(
                            "SansSerif",
                            Font.PLAIN,
                            11
                    )
            );

            l.setForeground(
                    statusColor(x)
            );

            legend.add(l);
        }

        p.add(
                legend,
                BorderLayout.SOUTH
        );

        return p;
    }

    static JPanel pointTile(
            Station s,
            Point q) {

        JPanel p = new JPanel(
                new BorderLayout(
                        5,
                        2
                )
        );

        p.setBackground(
                new Color(
                        250,
                        251,
                        253
                )
        );

        p.setBorder(
                new CompoundBorder(
                        new LineBorder(
                                new Color(
                                        228,
                                        232,
                                        238
                                )
                        ),
                        new EmptyBorder(
                                9,
                                10,
                                9,
                                10
                        )
                )
        );

        JLabel id = new JLabel(
                "P-" + q.id
        );

        id.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        14
                )
        );

        id.setForeground(TEXT);

        JLabel st = new JLabel(
                "● " + q.status
        );

        st.setForeground(
                statusColor(q.status)
        );

        st.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        10
                )
        );

        JLabel loc = new JLabel(
                "<html>" +
                s.name +
                "<br>" +
                "<span style='font-size:9px'>" +
                q.type +
                "</span>" +
                "</html>"
        );

        loc.setForeground(MUTED);

        loc.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        9
                )
        );

        p.add(
                id,
                BorderLayout.NORTH
        );

        p.add(
                st,
                BorderLayout.CENTER
        );

        p.add(
                loc,
                BorderLayout.SOUTH
        );

        return p;
    }

    static JPanel quickPanel() {

        JPanel p = panel();

        p.setLayout(
                new BorderLayout(
                        10,
                        10
                )
        );

        p.add(
                heading(
                        "Quick Actions",
                        "Common operations for the charging desk"
                ),
                BorderLayout.NORTH
        );

        JPanel q = new JPanel(
                new GridLayout(
                        4,
                        1,
                        10,
                        10
                )
        );

        q.setOpaque(false);

        String[] actions = {
                "＋  Create Reservation",
                "⚡  Record Charging Session",
                "＋  Register Vehicle",
                "▤  Open Analytics"
        };

        String[] targets = {
                "Reservations",
                "Charging Sessions",
                "Users",
                "Reports"
        };

        for (int i = 0; i < actions.length; i++) {

            JButton b = new JButton(
                    actions[i]
            );

            styleButton(
                    b,
                    i == 0
            );

            int k = i;

            b.addActionListener(
                    e -> show(targets[k])
            );

            q.add(b);
        }

        p.add(
                q,
                BorderLayout.CENTER
        );

        JPanel note = new JPanel(
                new BorderLayout()
        );

        note.setBackground(
                new Color(
                        247,
                        249,
                        252
                )
        );

        note.setBorder(
                new EmptyBorder(
                        11,
                        12,
                        11,
                        12
                )
        );

        note.add(
                new JLabel(
                        "<html>" +
                        "<b>Configured Tariff</b>" +
                        "<br>" +
                        "₹" +
                        String.format(
                                "%.2f",
                                TARIFF
                        ) +
                        " per kWh" +
                        "</html>"
                ),
                BorderLayout.WEST
        );

        p.add(
                note,
                BorderLayout.SOUTH
        );

        return p;
    }

    static JPanel panel() {

        JPanel p = new JPanel();

        p.setBackground(Color.WHITE);

        p.setBorder(
                new CompoundBorder(
                        new LineBorder(
                                new Color(
                                        226,
                                        230,
                                        236
                                )
                        ),
                        new EmptyBorder(
                                15,
                                15,
                                15,
                                15
                        )
                )
        );

        return p;
    }

    static void stationsPage() {

        JPanel root = new JPanel(
                new BorderLayout(
                        12,
                        12
                )
        );

        root.setOpaque(false);

        JPanel top = new JPanel(
                new BorderLayout()
        );

        top.setOpaque(false);

        top.add(
                heading(
                        "Station Directory",
                        "Select a point to update its operational state"
                ),
                BorderLayout.WEST
        );

        JButton add =
                new JButton("+ Add Station");

        styleButton(
                add,
                true
        );

        add.addActionListener(
                e -> addStation()
        );

        top.add(
                add,
                BorderLayout.EAST
        );

        root.add(
                top,
                BorderLayout.NORTH
        );

        DefaultTableModel m =
                new DefaultTableModel(
                        new Object[]{
                                "Station",
                                "Location",
                                "Point",
                                "Connector",
                                "Status",
                                "Action"
                        },
                        0
                ) {
                    public boolean isCellEditable(
                            int r,
                            int c) {
                        return c == 5;
                    }
                };

        for (Station s : stations) {

            for (Point q : s.points) {

                m.addRow(
                        new Object[]{
                                s.name,
                                s.location,
                                "P-" + q.id,
                                q.type,
                                q.status,
                                "Change"
                        }
                );
            }
        }

        JTable t = table(m);

        t.getColumnModel()
                .getColumn(5)
                .setCellRenderer(
                        new ButtonRenderer()
                );

        t.getColumnModel()
                .getColumn(5)
                .setCellEditor(
                        new ButtonEditor(
                                new JCheckBox(),
                                row -> {

                                    int point =
                                            Integer.parseInt(
                                                    t.getValueAt(
                                                            row,
                                                            2
                                                    )
                                                    .toString()
                                                    .replace(
                                                            "P-",
                                                            ""
                                                    )
                                            );

                                    changeStatus(point);

                                    show("Stations");
                                }
                        )
                );

        root.add(
                new JScrollPane(t),
                BorderLayout.CENTER
        );

        body.add(root);
    }

    static void addStation() {

        JTextField name =
                new JTextField();

        JTextField location =
                new JTextField();

        JComboBox<String> type =
                new JComboBox<>(
                        new String[]{
                                "AC Type-2",
                                "DC Fast",
                                "CCS2"
                        }
                );

        JPanel f = form();

        f.add(
                new JLabel(
                        "Station Name"
                )
        );

        f.add(name);

        f.add(
                new JLabel(
                        "Campus Location"
                )
        );

        f.add(location);

        f.add(
                new JLabel(
                        "Default Connector"
                )
        );

        f.add(type);

        int result =
                JOptionPane.showConfirmDialog(
                        frame,
                        f,
                        "Add Charging Station",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE
                );

        if (
                result == JOptionPane.OK_OPTION
                &&
                !name.getText().isBlank()
        ) {

            Station s =
                    new Station(
                            seq++,
                            name.getText(),
                            location.getText()
                    );

            s.points.add(
                    new Point(
                            seq++,
                            "Available",
                            type.getSelectedItem().toString()
                    )
            );

            stations.add(s);

            save();

            show("Stations");
        }
    }

    static void changeStatus(int id) {

        String[] options = {
                "Available",
                "Occupied",
                "Reserved",
                "Maintenance"
        };

        String selected =
                (String) JOptionPane.showInputDialog(
                        frame,
                        "New status for P-" + id,
                        "Update Charging Point",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        options,
                        options[0]
                );

        if (selected != null) {

            for (Station s : stations) {

                for (Point p : s.points) {

                    if (p.id == id) {

                        p.status = selected;
                    }
                }
            }

            save();
        }
    }

    static void reservationsPage() {

        JPanel root = new JPanel(
                new BorderLayout(
                        12,
                        12
                )
        );

        root.setOpaque(false);

        JPanel top = new JPanel(
                new BorderLayout()
        );

        top.setOpaque(false);

        top.add(
                heading(
                        "Reservation Desk",
                        "Book an available point and create a traceable slot record"
                ),
                BorderLayout.WEST
        );

        JButton b =
                new JButton(
                        "+ New Reservation"
                );

        styleButton(
                b,
                true
        );

        b.addActionListener(
                e -> newReservation()
        );

        top.add(
                b,
                BorderLayout.EAST
        );

        root.add(
                top,
                BorderLayout.NORTH
        );

        DefaultTableModel m =
                new DefaultTableModel(
                        new Object[]{
                                "ID",
                                "Vehicle",
                                "Charging Point",
                                "Slot",
                                "Status"
                        },
                        0
                );

        for (Reservation r : reservations) {

            m.addRow(
                    new Object[]{
                            "R-" + r.id,
                            vehicle(r.vehicleId),
                            "P-" + r.pointId,
                            r.slot,
                            r.status
                    }
            );
        }

        root.add(
                new JScrollPane(
                        table(m)
                ),
                BorderLayout.CENTER
        );

        body.add(root);
    }

    static void newReservation() {

        if (vehicles.isEmpty()) {

            JOptionPane.showMessageDialog(
                    frame,
                    "Register a vehicle first."
            );

            return;
        }

        List<Point> available =
                new ArrayList<>();

        for (Station s : stations) {

            for (Point p : s.points) {

                if (p.status.equals(
                        "Available"
                )) {

                    available.add(p);
                }
            }
        }

        if (available.isEmpty()) {

            JOptionPane.showMessageDialog(
                    frame,
                    "No available points right now."
            );

            return;
        }

        JComboBox<String> vehicleBox =
                new JComboBox<>(
                        vehicles.stream()
                                .map(
                                        x ->
                                                x.reg
                                                        + " • "
                                                        + x.model
                                )
                                .toArray(
                                        String[]::new
                                )
                );

        JComboBox<String> pointBox =
                new JComboBox<>(
                        available.stream()
                                .map(
                                        x ->
                                                "P-"
                                                        + x.id
                                                        + " • "
                                                        + x.type
                                )
                                .toArray(
                                        String[]::new
                                )
                );

        JTextField slot =
                new JTextField(
                        "Today • 02:30 PM"
                );

        JPanel f = form();

        f.add(
                new JLabel(
                        "Vehicle"
                )
        );

        f.add(vehicleBox);

        f.add(
                new JLabel(
                        "Charging Point"
                )
        );

        f.add(pointBox);

        f.add(
                new JLabel(
                        "Reservation Slot"
                )
        );

        f.add(slot);

        int result =
                JOptionPane.showConfirmDialog(
                        frame,
                        f,
                        "Create Reservation",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE
                );

        if (
                result == JOptionPane.OK_OPTION
        ) {

            Point point =
                    available.get(
                            pointBox.getSelectedIndex()
                    );

            point.status =
                    "Reserved";

            reservations.add(
                    new Reservation(
                            seq++,
                            vehicles.get(
                                    vehicleBox.getSelectedIndex()
                            ).id,
                            point.id,
                            slot.getText(),
                            "Confirmed"
                    )
            );

            save();

            show("Reservations");
        }
    }

    static void sessionsPage() {

        JPanel root = new JPanel(
                new BorderLayout(
                        12,
                        12
                )
        );

        root.setOpaque(false);

        JPanel top = new JPanel(
                new BorderLayout()
        );

        top.setOpaque(false);

        top.add(
                heading(
                        "Charging Activity",
                        "Energy, duration and automatic cost calculation"
                ),
                BorderLayout.WEST
        );

        JButton b =
                new JButton(
                        "+ Record Session"
                );

        styleButton(
                b,
                true
        );

        b.addActionListener(
                e -> newSession()
        );

        top.add(
                b,
                BorderLayout.EAST
        );

        root.add(
                top,
                BorderLayout.NORTH
        );

        DefaultTableModel m =
                new DefaultTableModel(
                        new Object[]{
                                "Session",
                                "Vehicle",
                                "Point",
                                "Energy",
                                "Duration",
                                "Cost",
                                "Status"
                        },
                        0
                );

        for (Session s : sessions) {

            m.addRow(
                    new Object[]{
                            "S-" + s.id,
                            vehicle(s.vehicleId),
                            "P-" + s.pointId,
                            String.format(
                                    "%.1f kWh",
                                    s.kwh
                            ),
                            String.format(
                                    "%.1f h",
                                    s.hours
                            ),
                            "₹" +
                                    String.format(
                                            "%.2f",
                                            s.cost
                                    ),
                            s.status
                    }
            );
        }

        root.add(
                new JScrollPane(
                        table(m)
                ),
                BorderLayout.CENTER
        );

        JPanel foot = panel();

        foot.setLayout(
                new BorderLayout()
        );

        foot.add(
                new JLabel(
                        "<html>" +
                        "<b>Cost Formula:</b> " +
                        "Energy Consumed × Tariff = kWh × ₹" +
                        TARIFF +
                        "</html>"
                )
        );

        root.add(
                foot,
                BorderLayout.SOUTH
        );

        body.add(root);
    }

    static void newSession() {

        if (vehicles.isEmpty()) {

            JOptionPane.showMessageDialog(
                    frame,
                    "Register a vehicle first."
            );

            return;
        }

        List<Point> usable =
                new ArrayList<>();

        for (Station s : stations) {

            for (Point p : s.points) {

                if (
                        p.status.equals(
                                "Reserved"
                        )
                        ||
                        p.status.equals(
                                "Available"
                        )
                ) {

                    usable.add(p);
                }
            }
        }

        if (usable.isEmpty()) {

            JOptionPane.showMessageDialog(
                    frame,
                    "No usable charging points available."
            );

            return;
        }

        JComboBox<String> vehicleBox =
                new JComboBox<>(
                        vehicles.stream()
                                .map(
                                        x ->
                                                x.reg
                                                        + " • "
                                                        + x.model
                                )
                                .toArray(
                                        String[]::new
                                )
                );

        JComboBox<String> pointBox =
                new JComboBox<>(
                        usable.stream()
                                .map(
                                        x ->
                                                "P-"
                                                        + x.id
                                                        + " • "
                                                        + x.type
                                )
                                .toArray(
                                        String[]::new
                                )
                );

        JTextField energy =
                new JTextField("15.5");

        JTextField hours =
                new JTextField("2.0");

        JPanel f = form();

        f.add(
                new JLabel(
                        "Vehicle"
                )
        );

        f.add(vehicleBox);

        f.add(
                new JLabel(
                        "Charging Point"
                )
        );

        f.add(pointBox);

        f.add(
                new JLabel(
                        "Energy Consumed (kWh)"
                )
        );

        f.add(energy);

        f.add(
                new JLabel(
                        "Duration (hours)"
                )
        );

        f.add(hours);

        int result =
                JOptionPane.showConfirmDialog(
                        frame,
                        f,
                        "Record Charging Session",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE
                );

        if (
                result == JOptionPane.OK_OPTION
        ) {

            try {

                double kwh =
                        Double.parseDouble(
                                energy.getText()
                        );

                double duration =
                        Double.parseDouble(
                                hours.getText()
                        );

                if (
                        kwh <= 0
                        ||
                        duration <= 0
                ) {

                    throw new Exception();
                }

                Point point =
                        usable.get(
                                pointBox.getSelectedIndex()
                        );

                point.status =
                        "Available";

                Session session =
                        new Session(
                                seq++,
                                vehicles.get(
                                        vehicleBox.getSelectedIndex()
                                ).id,
                                point.id,
                                kwh,
                                duration,
                                "Completed"
                        );

                sessions.add(session);

                payments.add(
                        new Payment(
                                seq++,
                                session.id,
                                session.cost,
                                "Paid"
                        )
                );

                save();

                show(
                        "Charging Sessions"
                );

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(
                        frame,
                        "Enter positive numeric values.",
                        "Validation",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        }
    }

    static void usersPage() {

        JPanel root = new JPanel(
                new BorderLayout(
                        12,
                        12
                )
        );

        root.setOpaque(false);

        JPanel top = new JPanel(
                new BorderLayout()
        );

        top.setOpaque(false);

        top.add(
                heading(
                        "Campus Accounts & EVs",
                        "Identity and vehicle information used by reservations"
                ),
                BorderLayout.WEST
        );

        JPanel actions = new JPanel(
                new FlowLayout(
                        FlowLayout.RIGHT,
                        8,
                        0
                )
        );

        actions.setOpaque(false);

        JButton user =
                new JButton(
                        "+ Register User"
                );

        JButton vehicle =
                new JButton(
                        "+ Register Vehicle"
                );

        styleButton(
                user,
                true
        );

        styleButton(
                vehicle,
                false
        );

        user.addActionListener(
                e -> addUser()
        );

        vehicle.addActionListener(
                e -> addVehicle()
        );

        actions.add(user);
        actions.add(vehicle);

        top.add(
                actions,
                BorderLayout.EAST
        );

        root.add(
                top,
                BorderLayout.NORTH
        );

        DefaultTableModel m =
                new DefaultTableModel(
                        new Object[]{
                                "User ID",
                                "Name",
                                "Email",
                                "Vehicle",
                                "Model",
                                "Registration"
                        },
                        0
                );

        for (User u : users) {

            List<Vehicle> list =
                    vehicles.stream()
                            .filter(
                                    v ->
                                            v.userId == u.id
                            )
                            .toList();

            if (list.isEmpty()) {

                m.addRow(
                        new Object[]{
                                "U-" + u.id,
                                u.name,
                                u.email,
                                "—",
                                "—",
                                "—"
                        }
                );

            } else {

                for (Vehicle v : list) {

                    m.addRow(
                            new Object[]{
                                    "U-" + u.id,
                                    u.name,
                                    u.email,
                                    "EV-" + v.id,
                                    v.model,
                                    v.reg
                            }
                    );
                }
            }
        }

        root.add(
                new JScrollPane(
                        table(m)
                ),
                BorderLayout.CENTER
        );

        body.add(root);
    }

    static void addUser() {

        JTextField name =
                new JTextField();

        JTextField email =
                new JTextField();

        JPanel f = form();

        f.add(
                new JLabel(
                        "Full Name"
                )
        );

        f.add(name);

        f.add(
                new JLabel(
                        "Mail Id"
                )
        );

        f.add(email);

        int result =
                JOptionPane.showConfirmDialog(
                        frame,
                        f,
                        "Register Campus User",
                        JOptionPane.OK_CANCEL_OPTION
                );

        if (
                result == JOptionPane.OK_OPTION
                &&
                !name.getText().isBlank()
        ) {

            users.add(
                    new User(
                            seq++,
                            name.getText(),
                            email.getText()
                    )
            );

            save();

            show("Users");
        }
    }

    static void addVehicle() {

        if (users.isEmpty()) {

            JOptionPane.showMessageDialog(
                    frame,
                    "Register a user first."
            );

            return;
        }

        JComboBox<String> userBox =
                new JComboBox<>(
                        users.stream()
                                .map(
                                        x ->
                                                x.name
                                                        + " • "
                                                        + x.email
                                )
                                .toArray(
                                        String[]::new
                                )
                );

        JTextField reg =
                new JTextField();

        JTextField model =
                new JTextField();

        JPanel f = form();

        f.add(
                new JLabel(
                        "Owner"
                )
        );

        f.add(userBox);

        f.add(
                new JLabel(
                        "Registration Number"
                )
        );

        f.add(reg);

        f.add(
                new JLabel(
                        "Vehicle Model"
                )
        );

        f.add(model);

        int result =
                JOptionPane.showConfirmDialog(
                        frame,
                        f,
                        "Register Electric Vehicle",
                        JOptionPane.OK_CANCEL_OPTION
                );

        if (
                result == JOptionPane.OK_OPTION
                &&
                !reg.getText().isBlank()
        ) {

            vehicles.add(
                    new Vehicle(
                            seq++,
                            users.get(
                                    userBox.getSelectedIndex()
                            ).id,
                            reg.getText(),
                            model.getText()
                    )
            );

            save();

            show("Users");
        }
    }

    static void paymentsPage() {

        JPanel root = new JPanel(
                new BorderLayout(
                        12,
                        12
                )
        );

        root.setOpaque(false);

        root.add(
                heading(
                        "Payment Ledger",
                        "Transactions automatically created for completed sessions"
                ),
                BorderLayout.NORTH
        );

        DefaultTableModel m =
                new DefaultTableModel(
                        new Object[]{
                                "Payment",
                                "Session",
                                "Vehicle",
                                "Amount",
                                "Status"
                        },
                        0
                );

        for (Payment p : payments) {

            Session s =
                    sessions.stream()
                            .filter(
                                    x ->
                                            x.id ==
                                                    p.sessionId
                            )
                            .findFirst()
                            .orElse(null);

            m.addRow(
                    new Object[]{
                            "PAY-" + p.id,
                            "S-" + p.sessionId,
                            s == null
                                    ? "—"
                                    : vehicle(
                                            s.vehicleId
                                    ),
                            "₹" +
                                    String.format(
                                            "%.2f",
                                            p.amount
                                    ),
                            p.status
                    }
            );
        }

        root.add(
                new JScrollPane(
                        table(m)
                ),
                BorderLayout.CENTER
        );

        body.add(root);
    }

    static void reportsPage() {

        double energy =
                sessions.stream()
                        .mapToDouble(
                                x -> x.kwh
                        )
                        .sum();

        double revenue =
                payments.stream()
                        .mapToDouble(
                                x -> x.amount
                        )
                        .sum();

        int available = 0;
        int occupied = 0;
        int reserved = 0;
        int maintenance = 0;

        for (Station s : stations) {

            for (Point p : s.points) {

                switch (p.status) {

                    case "Available" ->
                            available++;

                    case "Occupied" ->
                            occupied++;

                    case "Reserved" ->
                            reserved++;

                    default ->
                            maintenance++;
                }
            }
        }

        JPanel root = new JPanel(
                new BorderLayout(
                        14,
                        14
                )
        );

        root.setOpaque(false);

        JPanel cards = new JPanel(
                new GridLayout(
                        1,
                        4,
                        12,
                        12
                )
        );

        cards.setOpaque(false);

        cards.add(
                card(
                        "Sessions",
                        String.valueOf(
                                sessions.size()
                        ),
                        "Completed records",
                        BLUE
                )
        );

        cards.add(
                card(
                        "Energy",
                        String.format(
                                "%.1f kWh",
                                energy
                        ),
                        "Total delivered",
                        GREEN
                )
        );

        cards.add(
                card(
                        "Revenue",
                        "₹" +
                                String.format(
                                        "%.0f",
                                        revenue
                                ),
                        "At ₹" +
                                TARIFF +
                                "/kWh",
                        ORANGE
                )
        );

        double utilization =
                sessions.isEmpty()
                        ? 0
                        : Math.min(
                                100,
                                energy /
                                        Math.max(
                                                1,
                                                stations.size() * 35
                                        )
                                        * 100
                        );

        cards.add(
                card(
                        "Utilization",
                        String.format(
                                "%.0f%%",
                                utilization
                        ),
                        "Operational view",
                        PURPLE
                )
        );

        root.add(
                cards,
                BorderLayout.NORTH
        );

        JPanel middle = new JPanel(
                new GridLayout(
                        1,
                        2,
                        14,
                        0
                )
        );

        middle.setOpaque(false);

        middle.add(
                reportChart(
                        available,
                        occupied,
                        reserved,
                        maintenance
                )
        );

        JPanel insight = panel();

        insight.setLayout(
                new BorderLayout(
                        8,
                        8
                )
        );

        insight.add(
                heading(
                        "Management Insights",
                        "Operational information from charging records"
                ),
                BorderLayout.NORTH
        );

        JTextArea text =
                new JTextArea();

        text.setEditable(false);

        text.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        13
                )
        );

        text.setForeground(TEXT);
        text.setBackground(Color.WHITE);

        text.setText(
                "\n  • " +
                available +
                " points are available for immediate charging." +

                "\n\n  • " +
                reserved +
                " points are reserved for upcoming slots." +

                "\n\n  • " +
                occupied +
                " points are currently occupied." +

                "\n\n  • " +
                String.format(
                        "%.1f",
                        energy
                ) +
                " kWh has been recorded across sessions." +

                "\n\n  • Charging revenue recorded: ₹" +
                String.format(
                        "%.2f",
                        revenue
                ) +

                "\n\n  • Point states and transactions are saved locally."
        );

        insight.add(
                text,
                BorderLayout.CENTER
        );

        middle.add(insight);

        root.add(
                middle,
                BorderLayout.CENTER
        );

        body.add(root);
    }

    static JPanel reportChart(
            int available,
            int occupied,
            int reserved,
            int maintenance) {

        JPanel p = panel();

        p.setLayout(
                new BorderLayout()
        );

        p.add(
                heading(
                        "Point Distribution",
                        "Current status of every charging point"
                ),
                BorderLayout.NORTH
        );

        JPanel chart = new JPanel() {

            protected void paintComponent(
                    Graphics g) {

                super.paintComponent(g);

                int[] values = {
                        available,
                        occupied,
                        reserved,
                        maintenance
                };

                String[] names = {
                        "Available",
                        "Occupied",
                        "Reserved",
                        "Maintenance"
                };

                Color[] colors = {
                        GREEN,
                        ORANGE,
                        BLUE,
                        RED
                };

                int max = 1;

                for (int value : values) {
                    max = Math.max(max, value);
                }

                int x = 35;

                for (int i = 0;
                     i < values.length;
                     i++) {

                    int height =
                            (int)
                                    (
                                            (getHeight() - 90)
                                                    *
                                                    (
                                                            values[i]
                                                                    /
                                                                    (double) max
                                                    )
                                    );

                    g.setColor(colors[i]);

                    g.fillRoundRect(
                            x,
                            getHeight()
                                    - 50
                                    - height,
                            52,
                            height,
                            10,
                            10
                    );

                    g.setColor(TEXT);

                    g.setFont(
                            new Font(
                                    "SansSerif",
                                    Font.BOLD,
                                    12
                            )
                    );

                    g.drawString(
                            String.valueOf(
                                    values[i]
                            ),
                            x + 20,
                            getHeight()
                                    - 58
                                    - height
                    );

                    g.setFont(
                            new Font(
                                    "SansSerif",
                                    Font.PLAIN,
                                    10
                            )
                    );

                    g.drawString(
                            names[i],
                            x - 5,
                            getHeight()
                                    - 30
                    );

                    x += 85;
                }
            }
        };

        chart.setBackground(Color.WHITE);

        p.add(
                chart,
                BorderLayout.CENTER
        );

        return p;
    }

    static JPanel form() {

        JPanel p = new JPanel(
                new GridLayout(
                        0,
                        1,
                        6,
                        4
                )
        );

        p.setBorder(
                new EmptyBorder(
                        8,
                        8,
                        8,
                        8
                )
        );

        return p;
    }

    static void styleButton(
            JButton b,
            boolean primary) {

        b.setFocusPainted(false);

        b.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        12
                )
        );

        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setBorderPainted(true);
        b.setRolloverEnabled(false);

        if (primary) {

            b.setBackground(BLUE);
            b.setForeground(Color.WHITE);

            b.setBorder(
                    new CompoundBorder(
                            new LineBorder(BLUE),
                            new EmptyBorder(
                                    9,
                                    14,
                                    9,
                                    14
                            )
                    )
            );

        } else {

            b.setBackground(Color.WHITE);
            b.setForeground(TEXT);

            b.setBorder(
                    new CompoundBorder(
                            new LineBorder(
                                    new Color(
                                            210,
                                            216,
                                            225
                                    )
                            ),
                            new EmptyBorder(
                                    8,
                                    13,
                                    8,
                                    13
                            )
                    )
            );
        }
    }

    static JTable table(
            DefaultTableModel model) {

        JTable t =
                new JTable(model);

        t.setRowHeight(42);

        t.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        12
                )
        );

        t.getTableHeader()
                .setFont(
                        new Font(
                                "SansSerif",
                                Font.BOLD,
                                11
                        )
                );

        t.getTableHeader()
                .setBackground(
                        new Color(
                                241,
                                244,
                                248
                        )
                );

        t.getTableHeader()
                .setForeground(TEXT);

        t.setGridColor(
                new Color(
                        235,
                        238,
                        242
                )
        );

        t.setSelectionBackground(
                new Color(
                        225,
                        235,
                        252
                )
        );

        t.setSelectionForeground(TEXT);

        return t;
    }

    static Color statusColor(
            String status) {

        return switch (status) {

            case "Available" ->
                    GREEN;

            case "Occupied" ->
                    ORANGE;

            case "Reserved" ->
                    BLUE;

            default ->
                    RED;
        };
    }

    static String vehicle(int id) {

        for (Vehicle v : vehicles) {

            if (v.id == id) {
                return v.reg;
            }
        }

        return "EV-" + id;
    }

    static class ButtonRenderer
            extends JButton
            implements TableCellRenderer {

        ButtonRenderer() {

            setOpaque(true);

            setText("Change");

            setBackground(
                    new Color(
                            245,
                            247,
                            250
                    )
            );

            setForeground(TEXT);

            setBorder(
                    new LineBorder(
                            new Color(
                                    210,
                                    216,
                                    225
                            )
                    )
            );
        }

        public Component
        getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean selected,
                boolean focused,
                int row,
                int column) {

            return this;
        }
    }

    interface RowAction {
        void run(int row);
    }

    static class ButtonEditor
            extends DefaultCellEditor {

        JButton button =
                new JButton("Change");

        RowAction action;

        int row;

        ButtonEditor(
                JCheckBox checkBox,
                RowAction action) {

            super(checkBox);

            this.action = action;

            button.setOpaque(true);

            button.setBackground(
                    new Color(
                            245,
                            247,
                            250
                    )
            );

            button.setForeground(TEXT);

            button.setBorder(
                    new LineBorder(
                            new Color(
                                    210,
                                    216,
                                    225
                            )
                    )
            );

            button.addActionListener(
                    e -> {

                        fireEditingStopped();

                        action.run(row);
                    }
            );
        }

        public Component
        getTableCellEditorComponent(
                JTable table,
                Object value,
                boolean selected,
                int row,
                int column) {

            this.row = row;

            return button;
        }
    }

    static void save() {

        try {

            Files.createDirectories(
                    STORE.getParent()
            );

            Properties p =
                    new Properties();

            p.setProperty(
                    "version",
                    "1"
            );

            p.setProperty(
                    "users",
                    String.valueOf(
                            users.size()
                    )
            );

            p.setProperty(
                    "vehicles",
                    String.valueOf(
                            vehicles.size()
                    )
            );

            p.setProperty(
                    "stations",
                    String.valueOf(
                            stations.size()
                    )
            );

            p.setProperty(
                    "reservations",
                    String.valueOf(
                            reservations.size()
                    )
            );

            p.setProperty(
                    "sessions",
                    String.valueOf(
                            sessions.size()
                    )
            );

            p.setProperty(
                    "payments",
                    String.valueOf(
                            payments.size()
                    )
            );

            p.setProperty(
                    "lastSaved",
                    LocalDateTime.now()
                            .toString()
            );

            try (
                    OutputStream out =
                            Files.newOutputStream(
                                    STORE
                            )
            ) {

                p.store(
                        out,
                        "EV ChargeHub local application state"
                );
            }

        } catch (Exception ignored) {
        }
    }

    static void load() {
        seed();
    }

    static void seed() {

        users.clear();
        vehicles.clear();
        stations.clear();
        reservations.clear();
        sessions.clear();
        payments.clear();

        users.add(
                new User(
                        101,
                        "Ananya Menon",
                        "ananya@smartcampus.edu"
                )
        );

        users.add(
                new User(
                        102,
                        "Rahul Nair",
                        "rahul@smartcampus.edu"
                )
        );

        users.add(
                new User(
                        103,
                        "Meera Krishnan",
                        "meera@smartcampus.edu"
                )
        );

        vehicles.add(
                new Vehicle(
                        201,
                        101,
                        "KL-07-EV-2410",
                        "Tata Nexon EV"
                )
        );

        vehicles.add(
                new Vehicle(
                        202,
                        102,
                        "KL-01-EV-7721",
                        "MG ZS EV"
                )
        );

        vehicles.add(
                new Vehicle(
                        203,
                        103,
                        "KL-08-EV-5016",
                        "Hyundai Ioniq 5"
                )
        );

        Station academic =
                new Station(
                        301,
                        "North Academic Hub",
                        "Academic Block"
                );

        academic.points.add(
                new Point(
                        401,
                        "Available",
                        "CCS2"
                )
        );

        academic.points.add(
                new Point(
                        402,
                        "Occupied",
                        "CCS2"
                )
        );

        academic.points.add(
                new Point(
                        403,
                        "Reserved",
                        "AC Type-2"
                )
        );

        academic.points.add(
                new Point(
                        404,
                        "Maintenance",
                        "DC Fast"
                )
        );

        Station library =
                new Station(
                        302,
                        "Library Mobility Hub",
                        "Central Campus"
                );

        library.points.add(
                new Point(
                        405,
                        "Available",
                        "AC Type-2"
                )
        );

        library.points.add(
                new Point(
                        406,
                        "Available",
                        "CCS2"
                )
        );

        library.points.add(
                new Point(
                        407,
                        "Available",
                        "CCS2"
                )
        );

        library.points.add(
                new Point(
                        408,
                        "Reserved",
                        "AC Type-2"
                )
        );

        stations.add(academic);
        stations.add(library);

        reservations.add(
                new Reservation(
                        501,
                        201,
                        403,
                        "Today • 02:30 PM",
                        "Confirmed"
                )
        );

        reservations.add(
                new Reservation(
                        502,
                        203,
                        408,
                        "Today • 04:00 PM",
                        "Confirmed"
                )
        );

        sessions.add(
                new Session(
                        601,
                        202,
                        402,
                        18.6,
                        2.1,
                        "Completed"
                )
        );

        sessions.add(
                new Session(
                        602,
                        201,
                        405,
                        11.4,
                        1.4,
                        "Completed"
                )
        );

        payments.add(
                new Payment(
                        701,
                        601,
                        18.6 * TARIFF,
                        "Paid"
                )
        );

        payments.add(
                new Payment(
                        702,
                        602,
                        11.4 * TARIFF,
                        "Paid"
                )
        );

        seq = 900;
    }
}
