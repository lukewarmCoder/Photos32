# Photos32
A single-user photo application that allows storage and management of photos.

## Project Structure
```
Photos32/
|── bin/
|── data/
|── docs/
│── src/
│   └── photos32/
│       ├── model/
│       │   └── Counter.java
│       ├── view/
│       │   └── CounterView.fxml
│       ├── controller/
│       │   └── CounterController.java
│       └── Photos.java
│── .gitignore
|── README.md
```

## Prerequisites
Before running this project, make sure you have the following installed:
- Java 21
- JavaFX SDK 21.0.6 (You can download the JavaFX SDK from [here](https://gluonhq.com/products/javafx/))

## Getting Started

### Step 1: Clone the Repository
1. Clone this repository to your local machine using Git:
```
git clone https://github.com/lukewarmCoder/Photos32.git
```

3. Navigate to the project folder:
```
cd Photos32
```

### Step 2: Identify the path to your JavaFX SDK

1. You need to point to the lib/ folder inside the JavaFX SDK you downloaded.
    - Example: /Library/Java/JavaFX21/javafx-sdk-21.0.6/lib/

2. Set the temporary variable PATH_TO_FX to your JavaFX SDK path:
```
export PATH_TO_FX=/path/to/javafx-sdk/lib/
```

(This will only affect the current terminal session and will not persist after you close the terminal.)

### Step 3: Compile and Run

1. Compile the code
```
javac --module-path $PATH_TO_FX --add-modules javafx.controls,javafx.fxml -d bin src/photos32/**/*.java
```

2. Run the application
```
java --module-path $PATH_TO_FX --add-modules javafx.controls,javafx.fxml -cp bin photos32.Photos
```

### Step 4: Enjoy the App

Once you've completed the above steps, the application should start and the interface should be displayed.




