# Photos32

## Prerequisites
- Java 21 installed
- JavaFX SDK 21.0.6 (download from https://gluonhq.com/products/javafx/)

### Step 1: Get path

1. Retrieve the path to your JavaFX `lib` directory.
   - Ex.) /Library/Java/JavaFX21/javafx-sdk-21.0.6/lib/

### Step 2: Compile

`javac -d bin --module-path /Library/Java/JavaFX21/javafx-sdk-21.0.6/lib/ --add-modules javafx.controls,javafx.fxml src/photos32/**/*.java`

### Step 3: Run


