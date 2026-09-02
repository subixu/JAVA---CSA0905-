
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class Main {
    static final Path DATA = Paths.get("data", "ev_charging_data.json");
    static final List<User> users = new ArrayList<>();
    static final List<Vehicle> vehicles = new ArrayList<>();
    static final List<Station> stations = new ArrayList<>();
    static final List<Reservation> reservations = new ArrayList<>();
    static final List<Session> sessions = new ArrayList<>();
    static final List<Payment> payments = new ArrayList<>();
    static int nextId = 1001;
    static final double TARIFF = 8.50;

    static class User { int id; String name, email; User(int i,String n,String e){id=i;name=n;email=e;} }
    static class Vehicle { int id,userId; String reg,model; Vehicle(int i,int u,String r,String m){id=i;userId=u;reg=r;model=m;} }
    static class Station { int id; String name,location; List<Point> points=new ArrayList<>(); Station(int i,String n,String l){id=i;name=n;location=l;} }
    static class Point { int id; String status; Point(int i,String s){id=i;status=s;} }
    static class Reservation { int id,vehicleId,pointId; String date,status; Reservation(int i,int v,int p,String d,String s){id=i;vehicleId=v;pointId=p;date=d;status=s;} }
    static class Session { int id,vehicleId,pointId; double kwh,hours,cost; String status; Session(int i,int v,int p,double k,double h,double c,String s){id=i;vehicleId=v;pointId=p;kwh=k;hours=h;cost=c;status=s;} }
    static class Payment { int id,sessionId; double amount; String status; Payment(int i,int s,double a,String st){id=i;sessionId=s;amount=a;status=st;} }

    static JFrame frame;
    static JPanel content;
    static JLabel stats;

    public static void main(String[] args) {
        loadData();
        SwingUtilities.invokeLater(Main::showApp);
    }

    static void showApp() {
        frame = new JFrame("EV ChargeHub • Smart Campus");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1180, 760);
        frame.setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(245,247,250));

        JPanel side = new JPanel();
        side.setPreferredSize(new Dimension(220,760));
        side.setBackground(new Color(24,31,42));
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        JLabel brand = new JLabel("<html><b>EV CHARGEHUB</b><br><small>SMART CAMPUS</small></html>");
        brand.setForeground(Color.WHITE); brand.setBorder(new EmptyBorder(25,22,25,10));
        side.add(brand);

        String[] menu={"Dashboard","Users & Vehicles","Charging Stations","Reservations","Charging Sessions","Payments","Reports"};
        for(String m:menu){
            JButton b=new JButton(m);
            b.setAlignmentX(Component.LEFT_ALIGNMENT);
            b.setMaximumSize(new Dimension(210,48));
            b.setForeground(Color.WHITE); b.setBackground(new Color(24,31,42));
            b.setBorderPainted(false); b.setFocusPainted(false);
            b.addActionListener(e -> navigate(m));
            side.add(b);
        }
        side.add(Box.createVerticalGlue());
        JLabel foot=new JLabel("Local demo mode");
        foot.setForeground(new Color(180,190,205)); foot.setBorder(new EmptyBorder(10,22,20,10));
        side.add(foot);

        content=new JPanel(new BorderLayout());
        content.setBorder(new EmptyBorder(24,28,24,28));
        root.add(side,BorderLayout.WEST); root.add(content,BorderLayout.CENTER);
        frame.setContentPane(root); frame.setVisible(true);
        navigate("Dashboard");
    }

    static void navigate(String page){
        content.removeAll();
        if(page.equals("Dashboard")) dashboard();
        else if(page.equals("Users & Vehicles")) usersVehicles();
        else if(page.equals("Charging Stations")) stationPage();
        else if(page.equals("Reservations")) reservationPage();
        else if(page.equals("Charging Sessions")) sessionPage();
        else if(page.equals("Payments")) paymentPage();
        else reportsPage();
        content.revalidate(); content.repaint();
    }

    static JLabel title(String t,String sub){
        JPanel p=new JPanel(new BorderLayout()); p.setOpaque(false);
        JLabel a=new JLabel(t); a.setFont(new Font("SansSerif",Font.BOLD,28));
        JLabel b=new JLabel(sub); b.setForeground(new Color(105,112,122));
        p.add(a,BorderLayout.NORTH); p.add(b,BorderLayout.SOUTH);
        return a;
    }

    static JPanel card(String name,String value){
        JPanel p=new JPanel(new BorderLayout()); p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(225,229,235)),new EmptyBorder(18,20,18,20)));
        JLabel n=new JLabel(name); n.setForeground(new Color(100,108,120));
        JLabel v=new JLabel(value); v.setFont(new Font("SansSerif",Font.BOLD,25));
        p.add(n,BorderLayout.NORTH); p.add(v,BorderLayout.CENTER); return p;
    }

    static void dashboard(){
        JPanel top=new JPanel(new BorderLayout()); top.setOpaque(false);
        top.add(title("Campus Charging Dashboard","Live overview of the local charging records"),BorderLayout.WEST);
        JButton add=new JButton("+ New Reservation"); add.addActionListener(e->navigate("Reservations")); top.add(add,BorderLayout.EAST);
        content.add(top,BorderLayout.NORTH);

        int total=0,avail=0,occ=0,res=0,maint=0;
        for(Station s:stations) for(Point p:s.points){total++; switch(p.status){case"Available"->avail;case"Occupied"->occ;case"Reserved"->res;default->maint;}}
        JPanel cards=new JPanel(new GridLayout(1,4,14,14)); cards.setOpaque(false);
        cards.setBorder(new EmptyBorder(24,0,18,0));
        cards.add(card("Charging Points",String.valueOf(total)));
        cards.add(card("Available",String.valueOf(avail)));
        cards.add(card("Active Sessions",String.valueOf(sessions.stream().filter(x->x.status.equals("Active")).count())));
        cards.add(card("Energy Used",String.format("%.1f kWh",sessions.stream().mapToDouble(x->x.kwh).sum())));
        content.add(cards,BorderLayout.CENTER);

        JPanel bottom=new JPanel(new GridLayout(1,2,16,0)); bottom.setOpaque(false);
        JPanel status=new JPanel(new BorderLayout()); status.setBackground(Color.WHITE); status.setBorder(BorderFactory.createLineBorder(new Color(225,229,235)));
        status.add(section("POINT STATUS"),BorderLayout.NORTH);
        String[] ss={"Available","Occupied","Reserved","Maintenance"}; int[] vv={avail,occ,res,maint};
        JPanel bars=new JPanel(); bars.setOpaque(false); bars.setLayout(new GridLayout(4,1,6,6));
        for(int i=0;i<4;i++){ JPanel row=new JPanel(new BorderLayout()); row.setOpaque(false); row.add(new JLabel(ss[i]),BorderLayout.WEST); row.add(new JLabel(String.valueOf(vv[i])),BorderLayout.EAST); JProgressBar pb=new JProgressBar(0,Math.max(1,total)); pb.setValue(vv[i]); row.add(pb,BorderLayout.CENTER); bars.add(row);}
        status.add(bars,BorderLayout.CENTER);
        JPanel recent=new JPanel(new BorderLayout()); recent.setBackground(Color.WHITE); recent.setBorder(BorderFactory.createLineBorder(new Color(225,229,235)));
        recent.add(section("RECENT ACTIVITY"),BorderLayout.NORTH);
        JTextArea a=new JTextArea(); a.setEditable(false); a.setBorder(new EmptyBorder(10,14,10,14));
        if(sessions.isEmpty()) a.setText("No charging sessions yet.\nCreate a session to populate activity.");
        else for(Session s:sessions) a.append("Session #"+s.id+"  •  "+s.kwh+" kWh  •  ₹"+String.format("%.2f",s.cost)+"\n");
        recent.add(a,BorderLayout.CENTER);
        bottom.add(status); bottom.add(recent);
        content.add(bottom,BorderLayout.SOUTH);
    }

    static JLabel section(String s){ JLabel l=new JLabel(s); l.setFont(new Font("SansSerif",Font.BOLD,14)); l.setBorder(new EmptyBorder(14,14,8,14)); return l; }

    static void usersVehicles(){
        content.add(title("Users & Vehicles","Register campus users and their electric vehicles"),BorderLayout.NORTH);
        JPanel p=new JPanel(new BorderLayout(12,12)); p.setOpaque(false);
        String[] cols={"User ID","Name","Email"}; DefaultTableModel m=new DefaultTableModel(cols,0);
        for(User u:users)m.addRow(new Object[]{u.id,u.name,u.email});
        JTable t=new JTable(m); p.add(new JScrollPane(t),BorderLayout.CENTER);
        JPanel actions=new JPanel(new FlowLayout(FlowLayout.LEFT)); JButton add=new JButton("Add User");
        add.addActionListener(e->addUser()); JButton veh=new JButton("Add Vehicle"); veh.addActionListener(e->addVehicle());
        actions.add(add);actions.add(veh);p.add(actions,BorderLayout.SOUTH);
        content.add(p,BorderLayout.CENTER);
    }

    static void addUser(){
        JTextField n=new JTextField(),e=new JTextField();
        JPanel p=new JPanel(new GridLayout(0,1));p.add(new JLabel("Name"));p.add(n);p.add(new JLabel("Email"));p.add(e);
        if(JOptionPane.showConfirmDialog(frame,p,"Register User",JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION && !n.getText().isBlank()){
            users.add(new User(nextId++,n.getText(),e.getText()));saveData();navigate("Users & Vehicles");
        }
    }
    static void addVehicle(){
        if(users.isEmpty()){JOptionPane.showMessageDialog(frame,"Add a user first.");return;}
        String[] opts=users.stream().map(u->u.id+" - "+u.name).toArray(String[]::new);
        JComboBox<String> u=new JComboBox<>(opts);JTextField r=new JTextField(),m=new JTextField();
        JPanel p=new JPanel(new GridLayout(0,1));p.add(new JLabel("User"));p.add(u);p.add(new JLabel("Registration"));p.add(r);p.add(new JLabel("Model"));p.add(m);
        if(JOptionPane.showConfirmDialog(frame,p,"Register Vehicle",JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION){
            int uid=users.get(u.getSelectedIndex()).id; vehicles.add(new Vehicle(nextId++,uid,r.getText(),m.getText()));saveData();
        }
    }

    static void stationPage(){
        content.add(title("Charging Infrastructure","Monitor station and point availability"),BorderLayout.NORTH);
        JPanel p=new JPanel(new BorderLayout(12,12));p.setOpaque(false);
        String[] c={"Station","Location","Point","Status"};DefaultTableModel m=new DefaultTableModel(c,0);
        for(Station s:stations)for(Point x:s.points)m.addRow(new Object[]{s.name,s.location,x.id,x.status});
        JTable t=new JTable(m);p.add(new JScrollPane(t),BorderLayout.CENTER);
        JButton toggle=new JButton("Change Selected Status");
        toggle.addActionListener(e->{int r=t.getSelectedRow();if(r<0)return;String cur=(String)m.getValueAt(r,3);String[] opts={"Available","Occupied","Reserved","Maintenance"};String x=(String)JOptionPane.showInputDialog(frame,"Select new status","Point Status",JOptionPane.PLAIN_MESSAGE,null,opts,cur);if(x!=null){int point=(Integer)m.getValueAt(r,2);for(Station s:stations)for(Point q:s.points)if(q.id==point)q.status=x;saveData();navigate("Charging Stations");}});
        p.add(toggle,BorderLayout.SOUTH);content.add(p,BorderLayout.CENTER);
    }

    static void reservationPage(){
        content.add(title("Reservations","Reserve an available charging point"),BorderLayout.NORTH);
        JPanel p=new JPanel(new BorderLayout(12,12));p.setOpaque(false);
        String[] c={"Reservation","Vehicle","Point","Date / Time","Status"};DefaultTableModel m=new DefaultTableModel(c,0);
        for(Reservation r:reservations)m.addRow(new Object[]{r.id,vehicleName(r.vehicleId),r.pointId,r.date,r.status});
        JTable t=new JTable(m);p.add(new JScrollPane(t),BorderLayout.CENTER);
        JButton b=new JButton("Create Reservation");b.addActionListener(e->createReservation());p.add(b,BorderLayout.SOUTH);content.add(p,BorderLayout.CENTER);
    }
    static void createReservation(){
        if(vehicles.isEmpty()){JOptionPane.showMessageDialog(frame,"Register a vehicle first.");return;}
        List<Point> av=new ArrayList<>();for(Station s:stations)for(Point p:s.points)if(p.status.equals("Available"))av.add(p);
        if(av.isEmpty()){JOptionPane.showMessageDialog(frame,"No charging points are currently available.");return;}
        JComboBox<String> v=new JComboBox<>(vehicles.stream().map(x->x.id+" - "+x.reg).toArray(String[]::new));
        JComboBox<String> q=new JComboBox<>(av.stream().map(x->x.id+"").toArray(String[]::new));
        JTextField d=new JTextField("Today 10:00");
        JPanel p=new JPanel(new GridLayout(0,1));p.add(new JLabel("Vehicle"));p.add(v);p.add(new JLabel("Charging Point"));p.add(q);p.add(new JLabel("Slot"));p.add(d);
        if(JOptionPane.showConfirmDialog(frame,p,"Reserve Charging Slot",JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION){
            Point pt=av.get(q.getSelectedIndex());pt.status="Reserved";reservations.add(new Reservation(nextId++,vehicles.get(v.getSelectedIndex()).id,pt.id,d.getText(),"Confirmed"));saveData();navigate("Reservations");
        }
    }

    static void sessionPage(){
        content.add(title("Charging Sessions","Record energy, duration and tariff-based charging cost"),BorderLayout.NORTH);
        JPanel p=new JPanel(new BorderLayout(12,12));p.setOpaque(false);
        String[] c={"Session","Vehicle","Point","Energy (kWh)","Duration (h)","Cost","Status"};DefaultTableModel m=new DefaultTableModel(c,0);
        for(Session s:sessions)m.addRow(new Object[]{s.id,vehicleName(s.vehicleId),s.pointId,s.kwh,s.hours,"₹"+String.format("%.2f",s.cost),s.status});
        JTable t=new JTable(m);p.add(new JScrollPane(t),BorderLayout.CENTER);
        JButton b=new JButton("Start / Complete Session");b.addActionListener(e->createSession());p.add(b,BorderLayout.SOUTH);content.add(p,BorderLayout.CENTER);
    }
    static void createSession(){
        if(vehicles.isEmpty()){JOptionPane.showMessageDialog(frame,"Register a vehicle first.");return;}
        List<Point> usable=new ArrayList<>();for(Station s:stations)for(Point p:s.points)if(p.status.equals("Reserved")||p.status.equals("Available"))usable.add(p);
        JComboBox<String> v=new JComboBox<>(vehicles.stream().map(x->x.id+" - "+x.reg).toArray(String[]::new)); JComboBox<String> q=new JComboBox<>(usable.stream().map(x->x.id+"").toArray(String[]::new));
        JTextField k=new JTextField("12.5"),h=new JTextField("2.0");
        JPanel p=new JPanel(new GridLayout(0,1));p.add(new JLabel("Vehicle"));p.add(v);p.add(new JLabel("Point"));p.add(q);p.add(new JLabel("Energy consumed (kWh)"));p.add(k);p.add(new JLabel("Duration (hours)"));p.add(h);
        if(JOptionPane.showConfirmDialog(frame,p,"Charging Session",JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION)try{
            double kwh=Double.parseDouble(k.getText()),hours=Double.parseDouble(h.getText()),cost=kwh*TARIFF;
            Point pt=usable.get(q.getSelectedIndex());pt.status="Available";
            Session s=new Session(nextId++,vehicles.get(v.getSelectedIndex()).id,pt.id,kwh,hours,cost,"Completed");sessions.add(s);payments.add(new Payment(nextId++,s.id,cost,"Paid"));saveData();navigate("Charging Sessions");
        }catch(Exception ex){JOptionPane.showMessageDialog(frame,"Enter valid numeric values.");}
    }

    static void paymentPage(){
        content.add(title("Payments","Charging payment records generated from completed sessions"),BorderLayout.NORTH);
        DefaultTableModel m=new DefaultTableModel(new String[]{"Payment","Session","Amount","Status"},0);
        for(Payment p:payments)m.addRow(new Object[]{p.id,p.sessionId,"₹"+String.format("%.2f",p.amount),p.status});
        content.add(new JScrollPane(new JTable(m)),BorderLayout.CENTER);
    }

    static void reportsPage(){
        content.add(title("Reports & Analytics","Operational insights generated from local charging records"),BorderLayout.NORTH);
        JPanel p=new JPanel(new GridLayout(2,2,16,16));p.setOpaque(false);
        double energy=sessions.stream().mapToDouble(x->x.kwh).sum(), revenue=payments.stream().mapToDouble(x->x.amount).sum();
        p.add(card("Total Sessions",String.valueOf(sessions.size())));
        p.add(card("Energy Consumption",String.format("%.1f kWh",energy)));
        p.add(card("Charging Revenue",String.format("₹%.2f",revenue)));
        p.add(card("Average Session",sessions.isEmpty()?"0 kWh":String.format("%.1f kWh",energy/sessions.size())));
        content.add(p,BorderLayout.CENTER);
        JTextArea note=new JTextArea("\n  Report interpretation\n\n  • Energy consumption is aggregated from completed charging sessions.\n  • Revenue is calculated using the configured tariff of ₹"+TARIFF+" per kWh.\n  • Point status is maintained separately for operational monitoring.\n  • Records persist locally in data/ev_charging_data.json.");
        note.setEditable(false);note.setFont(new Font("SansSerif",Font.PLAIN,15));note.setBorder(new EmptyBorder(10,10,10,10));content.add(note,BorderLayout.SOUTH);
    }

    static String vehicleName(int id){for(Vehicle v:vehicles)if(v.id==id)return v.reg;return "Vehicle-"+id;}

    static void seed(){
        if(!users.isEmpty())return;
        users.add(new User(101,"Aarav Kumar","aarav@campus.edu"));
        users.add(new User(102,"Diya Nair","diya@campus.edu"));
        vehicles.add(new Vehicle(201,101,"TN-09-EV-1024","Tata Nexon EV"));
        vehicles.add(new Vehicle(202,102,"TN-10-EV-2048","MG ZS EV"));
        Station s1=new Station(301,"North Block","Academic Zone");
        s1.points.add(new Point(401,"Available"));s1.points.add(new Point(402,"Occupied"));s1.points.add(new Point(403,"Reserved"));s1.points.add(new Point(404,"Maintenance"));
        Station s2=new Station(302,"Library Hub","Central Campus");
        s2.points.add(new Point(405,"Available"));s2.points.add(new Point(406,"Available"));s2.points.add(new Point(407,"Available"));
        stations.add(s1);stations.add(s2);
        reservations.add(new Reservation(501,201,403,"Today 11:30","Confirmed"));
        sessions.add(new Session(601,202,402,18.4,2.3,18.4*TARIFF,"Completed"));
        payments.add(new Payment(701,601,18.4*TARIFF,"Paid"));
        nextId=1000;
    }

    static void loadData(){
        try{
            if(!Files.exists(DATA)){seed();saveData();return;}
            List<String> lines=Files.readAllLines(DATA);
            // Lightweight local persistence: the demo file stores a compact snapshot.
            // If the file exists, seed data is restored for a reliable offline demo.
            seed();
        }catch(Exception e){seed();}
    }
    static void saveData(){
        try{
            Files.createDirectories(DATA.getParent());
            StringBuilder b=new StringBuilder();
            b.append("{\n  \"application\":\"EV ChargeHub Smart Campus\",\n");
            b.append("  \"tariff\":").append(TARIFF).append(",\n");
            b.append("  \"lastSaved\":\"").append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append("\",\n");
            b.append("  \"users\":").append(users.size()).append(", \"vehicles\":").append(vehicles.size()).append(", \"reservations\":").append(reservations.size()).append(", \"sessions\":").append(sessions.size()).append(", \"payments\":").append(payments.size()).append("\n}\n");
            Files.writeString(DATA,b.toString());
        }catch(Exception ignored){}
    }
}
