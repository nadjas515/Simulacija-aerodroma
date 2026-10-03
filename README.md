# Airport Simulation

A Java Swing desktop application for planning airport departures and watching flights move across a map in real time. Airports and flights can be entered by hand or loaded from CSV or JSON files, and the simulation automatically spaces out departures from the same airport.

> Coursework for **Object-Oriented Programming 2** at the School of Electrical Engineering, University of Belgrade (2025/26).

## Features

- **Live flight simulation**: a Swing timer advances simulated time (1 s of real time = 10 min). Each plane's position is interpolated directly from the simulated time, so it stays exact whatever the timer step. Start, pause and stop controls.
- **Departure scheduling**: departures from the same airport must be at least 10 minutes apart; conflicting flights are moved to the next free 10-minute slot, and the table shows both the planned and the new time.
- **Interactive map**: custom `paintComponent` rendering that projects coordinates (x ∈ [-180, 180], y ∈ [-90, 90]) onto the panel. Click an airport to select it (it blinks), and use checkboxes in the table to show or hide airports.
- **Data entry with validation**: dialogs for new airports (3 uppercase letter code, coordinates in range) and flights (valid time, known airports); errors are shown in dialogs.
- **File I/O**: load and save in CSV and JSON (via Gson), through abstract `Reader`/`Writer` classes with one subclass per format.
- **Inactivity timeout**: a background thread closes the application after 60 s without user activity, showing a countdown warning for the last 5 s.

## Development phases

The project was built in three phases, each one adding to the previous:

- **Phase A: data and user interface.** The model of airports and flights, reading and writing CSV and JSON files through a common `Reader`/`Writer` interface, the Swing main window with airport and flight tables, input dialogs with validation, error and info dialogs, and the inactivity timer.
- **Phase B: map.** The custom-drawn map panel with airports positioned by their coordinates, selecting an airport by clicking it (it blinks), and showing or hiding airports with checkboxes in the table. The inactivity timer can be paused.
- **Phase C: simulation.** The simulation clock with start, pause and stop, planes moving along their routes based on simulated time, and automatic rescheduling of departures so that flights from the same airport are at least 10 minutes apart.

## Running

Requires JDK 17+ and Gson (`gson-2.10.jar` is included).

```sh
javac -encoding UTF-8 -cp gson-2.10.jar -d out $(find src -name "*.java")
java -cp out:gson-2.10.jar gui.Window          # on Windows use ; instead of :
```

Then choose a file with **Browse...**, press **Load**, and press **Start**. Example files are in [`examples/`](examples/). The user interface messages are in Serbian.

## File formats

CSV, with an airport section and a flight section separated by an empty line:

```
# AIRPORTS
CODE,NAME,X,Y
BEG,Belgrade,20,44
VIE,Vienna,16,48

# FLIGHTS
FROM,TO,DEPARTURE,DURATION
BEG,VIE,08:00,70
```

JSON:

```json
{
  "airports": [{"code": "BEG", "name": "Belgrade", "x": 20, "y": 44}],
  "flights":  [{"from": "BEG", "to": "VIE", "departure": "08:00", "duration": 70}]
}
```

Duration is in minutes.

## Project structure

| Package | Contents |
|---|---|
| `data` | Domain model (`Airport`, `Flight`, `Airplane`) and JSON transfer classes |
| `data.reader`, `data.writer` | Abstract `Reader`/`Writer` with CSV and JSON implementations |
| `logic` | `Simulation`: the simulation clock |
| `gui` | Main window, map panel, input dialogs, message dialogs, inactivity timer |
| `exceptions` | Checked exceptions for invalid input and missing data |
| `util` | Time conversion helpers |
