# Airport Simulation

A Java Swing desktop application for planning airport departures and watching flights move across a map in real time. Airports and flights can be entered by hand or loaded from CSV or JSON files, and departures from the same airport that are too close together are rescheduled automatically.

> Coursework for **Object-Oriented Programming 2** at the School of Electrical Engineering, University of Belgrade (2025/26).

## Features

- **Live flight simulation**: a Swing timer advances simulated time (1 s of real time = 10 min). Each plane's position is interpolated directly from the simulated time, so it stays exact whatever the timer step. Start, pause and stop controls.
- **Departure scheduling**: departures from the same airport that are less than 10 minutes apart are moved to 10-minute marks, and the table shows both the planned and the new time.
- **Interactive map**: custom `paintComponent` rendering that projects coordinates (x ∈ [-180, 180], y ∈ [-90, 90]) onto the panel. Click an airport to select it (it blinks). Ticking **Show** in the airport table displays only the ticked airports and the flights between them; with none ticked, all airports are shown.
- **Data entry with validation**: dialogs for new airports (3 uppercase letter code, coordinates in range) and flights (valid time, known airports); errors are shown in dialogs.
- **File I/O**: load and save in CSV and JSON (via Gson), through abstract `Reader`/`Writer` classes with one subclass per format.
- **Inactivity timeout**: a background thread closes the application after 60 s without a mouse click or key press, showing a countdown warning for the last few seconds. The timer is paused while the simulation is running.

## Development phases

The project was built in three phases, each one adding to the previous:

- **Phase A: data and user interface.** The model of airports and flights, reading and writing CSV and JSON files through abstract `Reader`/`Writer` classes, the Swing main window with airport and flight tables, input dialogs with validation, error and info dialogs, and the inactivity timer.
- **Phase B: map and clock.** The custom-drawn map panel with airports positioned by their coordinates, selecting an airport by clicking it (it blinks), filtering the airports shown on the map with checkboxes in the table, and a clock that shows the passing time. The inactivity timer can be paused.
- **Phase C: simulation.** The clock became the simulation clock with start, pause and stop, planes move along their routes based on simulated time, and departures from the same airport that are less than 10 minutes apart are rescheduled.

## Running

Requires JDK 8 or newer and Gson (`gson-2.10.jar` is included). The commands below are for a Unix-style shell (Linux, macOS, Git Bash).

```sh
javac -encoding UTF-8 -cp gson-2.10.jar -d out $(find src -name "*.java")
java -cp out:gson-2.10.jar gui.Window          # on Windows use ; instead of :
```

Then choose a file with **Browse...**, press **Load**, and press **Start**. Example files are in [`examples/`](examples/). Buttons and labels are in English; dialog and error messages are in Serbian.

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
  "airports": [{"code": "BEG", "name": "Belgrade", "x": 20, "y": 44},
               {"code": "VIE", "name": "Vienna", "x": 16, "y": 48}],
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
| `util` | Time conversion and map projection helpers |
