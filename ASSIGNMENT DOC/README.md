# EV ChargeHub – Smart Campus

A self-contained Java Swing demo for an E-Vehicle Charging Management System.

## Run in VS Code
1. Install JDK 17 or newer.
2. Open this folder in VS Code.
3. Open `src/Main.java`.
4. Run `Main.java` using the Java extension, or use the terminal:
   - Windows: `run.bat`
   - Manual: `javac -d out src/Main.java && java -cp out Main`
5. The application works offline. A local data snapshot is maintained at `data/ev_charging_data.json`.

## Demo flow
Dashboard → Users & Vehicles → Charging Stations → Reservations → Charging Sessions → Payments → Reports.

## Notes
This is a demo-ready local implementation. It uses Swing and local persistence so it can be launched without a database server.
