# Photos32
A multi-user photo application that allows storage and management of photos.

## Project Structure
```
Photos32/
|── bin/
|── data/
|── docs/
│── src/
│   └── photos32/
│       ├── model/
│       │   ├── Album.java
│       │   ├── Photo.java
│       │   ├── Tag.java
│       │   ├── TagType.java
│       │   └── User.java
│       ├── view/
│       │   ├── AdminHome.fxml
│       │   ├── AlbumCard.fxml
│       │   ├── AlbumView.fxml
│       │   ├── FilterWindow.fxml
│       │   ├── Login.fxml
│       │   ├── PhotoCard.fxml
│       │   ├── PhotoView.fxml
│       │   └── UserHome.fxml
│       ├── controller/
│       │   ├── AdminHomeController.java
│       │   ├── AlbumCardController.java
│       │   ├── AlbumViewController.java
│       │   ├── FilterController.java
│       │   ├── LoginController.java
│       │   ├── PhotoCardController.java
│       │   ├── PhotoViewController.java
│       │   └── UserHomeController.java
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

1. 🔐 Generate a GitHub Personal Access Token (PAT)
    - Go to https://github.com/settings/tokens
    - Click "Generate new token".
    - Set a name (e.g., Clone Access) and choose an expiration.
    - Under Repository access, select "Only select repositories" and choose **lukewarmCoder/Photos32**.
    - Open Repository permissions, scroll down to "Contents", and change access to **"Read and write"**.
    - Click "Generate token" at the bottom.
    - Copy the token and save it somewhere safe!

2. Clone the repository to your local machine using Git:
    ```
    git clone https://github.com/lukewarmCoder/Photos32.git
    ```
    - Git will ask for:
        - **Username:** Your GitHub username
        - **Password:** Paste the **personal access token**


3. Navigate to the project folder:
   
    ```
    cd Photos32
    ```

### Step 2: Identify the path to your JavaFX SDK

1. You need to point to the lib/ folder inside the JavaFX SDK you downloaded.
    - Example: /Downloads/javafx-sdk-21.0.6/lib/

2. Set the temporary variable PATH_TO_FX to your JavaFX SDK path:
    - On Mac/Linux:
      
      ```
      export PATH_TO_FX=/path/to/javafx-sdk/lib/
      ```
      
    - On Windows (Command Prompt):
      
        ```
        set PATH_TO_FX=C:\path\to\javafx-sdk\lib\
        ```

     (This will only affect the current terminal session and will not persist after you close the terminal.)

### Step 3: Compile and Run

1. Compile the code
    - On Mac/Linux:
        
        ```
        javac --module-path $PATH_TO_FX --add-modules javafx.controls,javafx.fxml -d bin src/photos32/**/*.java
        ```
      
    - On Windows (Command Prompt):
      
        ```
        javac --module-path %PATH_TO_FX% --add-modules javafx.controls,javafx.fxml -d bin src\photos32\**\*.java
        ```

3. Run the application
    - On Mac/Linux:
        
        ```
        java --module-path $PATH_TO_FX --add-modules javafx.controls,javafx.fxml -cp bin photos32.Photos
        ```
      
    - On Windows (Command Prompt):
      
        ```
        java --module-path %PATH_TO_FX% --add-modules javafx.controls,javafx.fxml -cp bin photos32.Photos
        ```

### Step 4: Enjoy the App!

Once you've completed the above steps, the application should start and the interface should be displayed.




