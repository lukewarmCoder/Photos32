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
- Java 21 installed
- JavaFX SDK 21.0.6 (download from https://gluonhq.com/products/javafx/)

## How to Run
### Step 1: Clone the Repository
1. Clone the repository to your local machine using Git:
``
git clone https://github.com/yourusername/JavaFXApp.git
``

3. Navigate to the project folder
`` cd JavaFXApp ``

1. Retrieve the path to your JavaFX `lib` directory.
   - Ex.) /Library/Java/JavaFX21/javafx-sdk-21.0.6/lib/

### Step 2: Compile

`javac -d bin --module-path (path to your JavaFX library) --add-modules javafx.controls,javafx.fxml src/photos32/**/*.java`

### Step 3: Run


